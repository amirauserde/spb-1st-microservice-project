package com.amir.microservices.notification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@SpringBootApplication
public class NotificationMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationMicroserviceApplication.class, args);
	}

}
