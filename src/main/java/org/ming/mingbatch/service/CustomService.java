package org.ming.mingbatch.service;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomService {
    public void hello(String name) {
        log.info("hello service was called! by {}", name);
    }
}
