package edu.nccu.cs.datasender;

import com.groocraft.couchdb.slacker.annotation.EnableCouchDbRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCouchDbRepositories
public class DataSenderApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataSenderApplication.class, args);
    }
}
