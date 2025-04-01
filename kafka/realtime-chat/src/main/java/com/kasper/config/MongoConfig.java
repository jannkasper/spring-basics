package com.kasper.config;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
            new LocalDateTimeToLongConverter(),
            new LongToLocalDateTimeConverter(),
            new DateListToLocalDateTimeConverter()
        ));
    }

    @WritingConverter
    static class LocalDateTimeToLongConverter implements Converter<LocalDateTime, Long> {
        @Override
        public Long convert(LocalDateTime source) {
            return source == null ? null : source.toInstant(ZoneOffset.UTC).toEpochMilli();
        }
    }

    @ReadingConverter
    static class LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime> {
        @Override
        public LocalDateTime convert(Long source) {
            return source == null ? null : LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(source), ZoneOffset.UTC);
        }
    }
    
    @ReadingConverter
    static class DateListToLocalDateTimeConverter implements Converter<List<Integer>, LocalDateTime> {
        @Override
        public LocalDateTime convert(List<Integer> source) {
            if (source == null || source.size() < 6) {
                return null;
            }
            
            try {
                int year = source.get(0);
                int month = source.get(1);
                int day = source.get(2);
                int hour = source.get(3);
                int minute = source.get(4);
                int second = source.get(5);
                int nano = source.size() >= 7 ? source.get(6) : 0;
                
                return LocalDateTime.of(year, month, day, hour, minute, second, nano);
            } catch (Exception e) {
                return null;
            }
        }
    }
}