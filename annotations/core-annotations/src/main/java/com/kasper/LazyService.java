package com.kasper;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy  // Will not be initialized until first used
public class LazyService {
    public LazyService() {
        System.out.println("LazyService Initialized!");
    }

    public void performLazyOperation() {
        System.out.println("LazyService operation performed!");
    }
}
