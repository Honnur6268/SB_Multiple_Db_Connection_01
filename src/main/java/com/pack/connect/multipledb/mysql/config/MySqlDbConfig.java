package com.pack.connect.multipledb.mysql.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryBean", basePackages = {
		"com.pack.connect.multipledb.mysql.repository" }, transactionManagerRef = "transactionManager")
public class MySqlDbConfig {

	@Autowired
	private Environment environment;

	// dataSource
	@Bean
	@Primary
	public DataSource dataSource() {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(environment.getProperty("spring.db2.datasource.url"));
		dataSource.setDriverClassName(environment.getProperty("spring.db2.datasource.driver-class-name"));
		dataSource.setUsername(environment.getProperty("spring.db2.datasource.username"));
		dataSource.setPassword(environment.getProperty("spring.db2.datasource.password"));

		return dataSource;
	}

	// entityManagerFactory
	@Bean(name = "entityManagerFactoryBean")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();

		JpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

		Map<String, String> props = new HashMap<>();
		props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		props.put("hibernate.show_sql", "true");
		props.put("hibernate.hbm2ddl.auto", "create");

		bean.setDataSource(dataSource());
		bean.setJpaVendorAdapter(adapter);
		bean.setJpaPropertyMap(props);
		bean.setPackagesToScan("com.pack.connect.multipledb.mysql.entities");

		return bean;
	}

	// platformTransactionManager
	@Bean(name = "transactionManager")
	@Primary
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager manager = new JpaTransactionManager();
		manager.setEntityManagerFactory(entityManagerFactoryBean().getObject());

		return manager;
	}

}
