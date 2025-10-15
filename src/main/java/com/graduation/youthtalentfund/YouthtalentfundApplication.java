package com.graduation.youthtalentfund;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing // Kích hoạt JPA Auditing
public class YouthtalentfundApplication {
	@PostConstruct
	void init() {
		// Set default timezone cho toàn bộ ứng dụng là UTC
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(YouthtalentfundApplication.class, args);
	}

}
