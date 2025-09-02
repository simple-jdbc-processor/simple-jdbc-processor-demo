package com.example.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary
    @ConfigurationProperties("spring.datasource")
    @Bean
    public DataSource master() {
        return new HikariDataSource() {
            public void setUrl(String jdbcUrl) {
                super.setJdbcUrl(jdbcUrl);
            }
        };
    }

    @ConfigurationProperties("spring.datasource-slave1")
    @Bean
    public DataSource slave1() {
        return new HikariDataSource() {
            public void setUrl(String jdbcUrl) {
                super.setJdbcUrl(jdbcUrl);
            }
        };
    }

    @ConfigurationProperties("spring.datasource-slave2")
    @Bean
    public DataSource slave2() {
        return new HikariDataSource() {
            public void setUrl(String jdbcUrl) {
                super.setJdbcUrl(jdbcUrl);
            }
        };
    }


}
