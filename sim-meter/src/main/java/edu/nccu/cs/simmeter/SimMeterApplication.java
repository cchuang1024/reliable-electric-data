package edu.nccu.cs.simmeter;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableAsync
@EnableWebFlux
@EnableScheduling
@ConfigurationPropertiesScan("edu.nccu.cs.simmeter.**")
public class SimMeterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimMeterApplication.class, args);
    }

}
