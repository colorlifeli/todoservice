package org.me.todoservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan(basePackages = { "org.me.todoservice" })
@ServletComponentScan
public class TodoserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoserviceApplication.class, args);
	}

}
