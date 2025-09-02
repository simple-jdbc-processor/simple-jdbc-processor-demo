package com.example.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;

@Data
public class CustomDataSourceProperties {

    private String url;

    private String username;

    private String password;

    private HikariConfig hikari;
}
