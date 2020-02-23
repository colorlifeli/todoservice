package org.me.todoservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = { "org.me.todoservice" })
public class TodoserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoserviceApplication.class, args);
	}

}
