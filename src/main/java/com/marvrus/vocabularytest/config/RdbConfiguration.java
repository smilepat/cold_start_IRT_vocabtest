package com.marvrus.vocabularytest.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Lazy
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class RdbConfiguration {
    @Bean(name = "jpaDatasource")
    public DataSource jpaDatasource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        Properties properties = vocabularyJpaDatasourceProperties();
        dataSource.setDriverClassName(properties.getProperty("driver-class-name"));
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setUsername(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));

        return dataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean paymentEntityManager() throws Exception {
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(jpaDatasource());
        entityManager.setPackagesToScan("com.marvrus.vocabularytest.model");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManager.setJpaVendorAdapter(vendorAdapter);
        entityManager.setJpaProperties(vocabularyJpaDatasourceProperties());

        return entityManager;
    }

    @ConfigurationProperties(prefix = "vocabulary.jpa")
    @Bean(name = "vocabularyJpaDatasourceProperties")
    public Properties vocabularyJpaDatasourceProperties() {
        return new Properties();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() throws Exception {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(paymentEntityManager().getObject());
        return transactionManager;
    }
}
