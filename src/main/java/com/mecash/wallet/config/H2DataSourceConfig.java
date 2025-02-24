package com.mecash.wallet.config;

import org.h2.api.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

@Configuration
public class H2DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:file:./data/mecash-db;MODE=MYSQL;DB_CLOSE_DELAY=-1") // ✅ Matches application.properties
                .driverClassName("org.h2.Driver")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.mecash.wallet.model"); // ✅ Scans for JPA Entities
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(hibernateProperties());
        return em;
    }

    @Bean
    public JpaTransactionManager transactionManager(DataSource dataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource); // ✅ Uses DataSource directly
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update"); // ✅ Matches properties
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        return properties;
    }

    /**
     * ✅ H2 Trigger for Auto-Timestamp on CreatedAt field
     */
    public static class CreatedAtTrigger implements Trigger {
        private static final int CREATED_AT_INDEX = 4;

        @Override
        public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
            // No initialization needed
        }

        @Override
        public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
            if (newRow != null && oldRow == null && newRow.length > CREATED_AT_INDEX) {
                newRow[CREATED_AT_INDEX] = newRow[CREATED_AT_INDEX] == null
                        ? new Timestamp(System.currentTimeMillis())
                        : newRow[CREATED_AT_INDEX];
            }
        }

        @Override
        public void close() throws SQLException {
            // No cleanup needed
        }

        @Override
        public void remove() throws SQLException {
            // No cleanup needed
        }
    }
}
