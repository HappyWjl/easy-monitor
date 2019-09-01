package com.ilearn1234.monitor.service.impl;

import com.ilearn1234.monitor.service.TestService;
import com.ilearn1234.monitor.util.InfluxDBUtil;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Map;

@Service("testService")
public class TestServiceImpl implements TestService {

    @Autowired
    InfluxDBUtil influxDBConnection;

    @Override
    public void test(Integer num) {
        Point point = influxDBConnection.pointBuilder(
                "user_login",
                Calendar.getInstance().getTimeInMillis(),
                Map.of("user", "login"),
                Map.of("login-number", num));
        influxDBConnection.insert(point);
    }
}
