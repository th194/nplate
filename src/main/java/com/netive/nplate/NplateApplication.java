package com.netive.nplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class NplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(NplateApplication.class, args);
	}

}
