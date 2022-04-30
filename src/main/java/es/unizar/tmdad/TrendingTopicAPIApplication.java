package es.unizar.tmdad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrendingTopicAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrendingTopicAPIApplication.class, args);
	}

}
