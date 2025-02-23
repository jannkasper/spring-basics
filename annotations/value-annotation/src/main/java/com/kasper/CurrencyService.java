package com.kasper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    @Value("#{'${ecommerce.supported.currencies}'.split(',')}")
    private List<String> supportedCurrencies;

    public void displayCurrencies() {
        System.out.println("Supported Currencies: " + supportedCurrencies);
    }
}

