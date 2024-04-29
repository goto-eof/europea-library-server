package com.andreidodu.europealibrary.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.andreidodu.europealibrary.repository"})
@RequiredArgsConstructor
public class PersistenceConfiguration {
    private final BatchDbConfig batchDbConfig;

    @Value("${com.andreidodu.europea-library.transaction-timeout-milliseconds}")
    private Integer transactionTimeoutMilliseconds;

    @Primary
    @Bean("transactionManager")
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager(Objects.requireNonNull(sessionFactory().getObject()));
        hibernateTransactionManager.setDefaultTimeout(this.transactionTimeoutMilliseconds);
        return hibernateTransactionManager;
    }

    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.andreidodu.europealibrary.model");
        return sessionFactory;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean("dataSource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(batchDbConfig.getUrl())
                .username(batchDbConfig.getUsername())
                .password(batchDbConfig.getPassword())
                .build();
    }

}
