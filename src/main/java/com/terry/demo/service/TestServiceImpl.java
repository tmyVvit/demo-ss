package com.terry.demo.service;

import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    public void test() {
        System.out.println("test");
    }
}
