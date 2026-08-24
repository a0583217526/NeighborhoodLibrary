package com.library.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import com.library.notification_service.service.EmailService;
import org.springframework.stereotype.Service;

@SpringBootApplication
@EnableAsync
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
