package com.david.jcb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
@EnableScheduling
public class JcbApplication {

	public static void main(String[] args) {
		SpringApplication.run(JcbApplication.class, args);
	}

	@Bean
	public ThreadPoolTaskScheduler taskScheduler() {
		final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(1000);
		return taskScheduler;
	}
}
