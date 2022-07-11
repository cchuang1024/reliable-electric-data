package edu.nccu.cs.recorder;

import org.rocksdb.RocksDB;
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
@ConfigurationPropertiesScan("edu.nccu.cs.recorder.config")
public class MeterRecorderApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MeterRecorderApplication.class);
        // application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

}
