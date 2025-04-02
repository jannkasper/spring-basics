package com.kasper.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kasper.model.UserStatus;

public interface UserStatusRepository extends MongoRepository<UserStatus, String> {
    List<UserStatus> findByRoomId(String roomId);
    List<UserStatus> findByOnlineTrue();
}
