package com.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    @ConfigurationProperties("spring.datasource")
    @Bean
    public CustomDataSourceProperties masterDataSourceProperties() {
        return new CustomDataSourceProperties();
    }

    @ConfigurationProperties("spring.datasource-slave1")
    @Bean
    public CustomDataSourceProperties slave1DataSourceProperties() {
        return new CustomDataSourceProperties();
    }

    @ConfigurationProperties("spring.datasource-slave2")
    @Bean
    public CustomDataSourceProperties slave2DataSourceProperties() {
        return new CustomDataSourceProperties();
    }

    @Primary
    @Bean
    public HikariDataSource masterDataSource(@Qualifier("masterDataSourceProperties") CustomDataSourceProperties properties) {
        return hikariDataSource(properties);
    }

    @Bean
    public HikariDataSource slave1DataSource(@Qualifier("slave1DataSourceProperties") CustomDataSourceProperties properties) {
        return hikariDataSource(properties);
    }

    @Bean
    public HikariDataSource slave2DataSource(@Qualifier("slave2DataSourceProperties") CustomDataSourceProperties properties) {
        return hikariDataSource(properties);
    }


    private HikariDataSource hikariDataSource(CustomDataSourceProperties properties) {
        HikariConfig hikari = properties.getHikari();
        hikari.setJdbcUrl(properties.getUrl());
        hikari.setUsername(properties.getUsername());
        hikari.setPassword(properties.getPassword());
        return new HikariDataSource(hikari);
    }


}
