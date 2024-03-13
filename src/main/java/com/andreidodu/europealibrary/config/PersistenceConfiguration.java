package com.andreidodu.europealibrary.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(basePackages = {"com.andreidodu.europealibrary.repository"})
// @EntityScan(basePackages = {"com.andreidodu.europalibrary.model"})
@RequiredArgsConstructor
public class PersistenceConfiguration {
    private final BatchDbConfig batchDbConfig;

    @Bean
    public HibernateTransactionManager transactionManager() {
        return new HibernateTransactionManager(Objects.requireNonNull(sessionFactory().getObject()));
    }

    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.andreidodu.europealibrary.model");
        return sessionFactory;
    }


    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(batchDbConfig.getUrl())
                .username(batchDbConfig.getUsername())
                .password(batchDbConfig.getPassword())
                .build();
    }
}
