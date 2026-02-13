package com.example.rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableCaching // 必须开启，否则Spring Cache注解无效
@MapperScan("com.example.rbac.mapper")
@ConfigurationPropertiesScan
public class RbacApplication {

	public static void main(String[] args) {
		SpringApplication.run(RbacApplication.class, args);
	}

}
