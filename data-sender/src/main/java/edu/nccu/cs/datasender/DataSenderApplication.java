package edu.nccu.cs.datasender;

import com.groocraft.couchdb.slacker.annotation.EnableCouchDbRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableWebFlux
@EnableCouchDbRepositories
public class DataSenderApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DataSenderApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }
}
