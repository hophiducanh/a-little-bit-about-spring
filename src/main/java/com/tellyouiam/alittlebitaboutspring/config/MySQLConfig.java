package com.tellyouiam.alittlebitaboutspring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.tellyouiam")
public class MySQLConfig {
	@Autowired
	private Environment env;
	
	@Bean
	@Profile("dev") //difference with @Conditional annotation
	//-Dspring.profiles.active=dev (VM option)
	public DataSource dataSource() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
		
		dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.datasource.driver-class-name")));
		dataSource.setUsername(env.getProperty("spring.datasource.username"));
		dataSource.setPassword(env.getProperty("spring.datasource.password"));
		dataSource.setUrl(env.getProperty("spring.datasource.url"));
		
		return dataSource;
	}
	
	@Bean
	LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource());
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManagerFactoryBean.setPackagesToScan("com.tellyouiam.alittlebitaboutspring.entity");
		
		Properties jpaProperties = new Properties();
		
		//Configures the used database dialect. This allows Hibernate to create SQL
		//that is optimized for the used database.
		jpaProperties.put("spring.jpa.properties.hibernate.dialect", env.getRequiredProperty("spring.jpa.properties.hibernate.dialect"));
		
		//Specifies the action that is invoked to the database when the Hibernate
		//SessionFactory is created or closed.
		jpaProperties.put("spring.jpa.hibernate.ddl-auto",
				env.getRequiredProperty("spring.jpa.hibernate.ddl-auto")
		);
		
		//Configures the naming strategy that is used when Hibernate creates
		//new database objects and schema elements
//		jpaProperties.put("hibernate.ejb.naming_strategy",
//				env.getRequiredProperty("hibernate.ejb.naming_strategy")
//		);
		
		//If the value of this property is true, Hibernate writes all SQL
		//statements to the console.
		jpaProperties.put("spring.jpa.show-sql",
				env.getRequiredProperty("spring.jpa.show-sql")
		);
		
		//If the value of this property is true, Hibernate will format the SQL
		//that is written to the console.
		jpaProperties.put("spring.jpa.properties.hibernate.format_sql",
				env.getRequiredProperty("spring.jpa.properties.hibernate.format_sql")
		);
		
		entityManagerFactoryBean.setJpaProperties(jpaProperties);
		
		return entityManagerFactoryBean;
	}
	
	@Bean
	JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}
	
//	@Bean
//	public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
//		final DataSourceInitializer initializer = new DataSourceInitializer();
//		initializer.setDataSource(dataSource);
//
//		final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//		initializer.setDatabasePopulator(populator);
//		return initializer;
//	}
	
}
