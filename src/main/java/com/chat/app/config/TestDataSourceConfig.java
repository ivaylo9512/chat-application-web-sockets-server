package com.chat.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Profile("test")
public class TestDataSourceConfig {
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "create");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        em.setJpaPropertyMap(properties);
        em.setDataSource(dataSource());
        em.setPackagesToScan("com/chat/app/models");
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }
    @Bean(name = "test-datasource")
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://192.168.0.105:3306/chat-app");
        dataSource.setUsername( "root" );
        dataSource.setPassword( "1234" );
         return dataSource;
    }
}
