package com.ilearn1234.monitor.bean;

import com.ilearn1234.monitor.util.InfluxDBUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.username}")
    String username;

    @Value("${influxdb.password}")
    String password;

    @Value("${influxdb.openurl}")
    String openurl;

    @Value("${influxdb.database}")
    String database;

    @Bean
    public InfluxDBUtil initInfluxDB() {
        return new InfluxDBUtil(username, password, openurl, database, "");
    }
}
