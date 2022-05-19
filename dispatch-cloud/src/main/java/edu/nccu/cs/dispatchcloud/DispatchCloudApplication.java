package edu.nccu.cs.dispatchcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableWebFlux
@EnableMongoRepositories
@EnableMongoAuditing
public class DispatchCloudApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DispatchCloudApplication.class);
        application.run(args);
    }
}
