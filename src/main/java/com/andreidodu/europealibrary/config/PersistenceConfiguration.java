package com.andreidodu.europealibrary.config;

import lombok.RequiredArgsConstructor;
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

    @Primary
    @Bean("transactionManager")
    public HibernateTransactionManager transactionManager() {
        return new HibernateTransactionManager(Objects.requireNonNull(sessionFactory().getObject()));
    }
//
//    @Bean("transactionManager")
//    public JpaTransactionManager transactionManager() {
//        return new JpaTransactionManager(Objects.requireNonNull(sessionFactory().getObject()));
//    }
//
//    @Bean("dataSourceTransactionManager")
//    public DataSourceTransactionManager dataSourceTransactionManager() {
//        return new DataSourceTransactionManager(dataSource());
//    }

//    @Bean("jdbcTransactionManager")
//    public JdbcTransactionManager jdbcTransactionManager() {
//        return new JdbcTransactionManager(secondDataSource());
//    }

//    @Bean("jpaTransactionManager")
//    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
//        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
//        jpaTransactionManager.setDataSource(dataSource());
//        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
//        return jpaTransactionManager;
//    }
//

//    @Bean("resourcelessTransactionManager")
//    public ResourcelessTransactionManager resourcelessTransactionManager() {
//        return new ResourcelessTransactionManager();
//    }

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

//    @Bean("secondDataSource")
//    public DataSource secondDataSource() {
//        return DataSourceBuilder.create()
//                .url(batchDbConfig.getUrl())
//                .username(batchDbConfig.getUsername())
//                .password(batchDbConfig.getPassword())
//                .build();
//    }
}
