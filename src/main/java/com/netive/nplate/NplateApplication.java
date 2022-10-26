package com.netive.nplate;

import com.netive.nplate.configuration.WebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;

@ServletComponentScan
@SpringBootApplication
public class NplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(NplateApplication.class, args);
	}

}
