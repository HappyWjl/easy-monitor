package com.ilearn1234.monitor.controller;

import com.ilearn1234.monitor.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    TestService testService;

    @GetMapping("/login")
    public void test(Integer num) {
        testService.test(num);
    }
}
