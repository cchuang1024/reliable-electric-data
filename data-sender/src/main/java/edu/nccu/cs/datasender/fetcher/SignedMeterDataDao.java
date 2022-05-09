package edu.nccu.cs.datasender.fetcher;

import com.groocraft.couchdb.slacker.CouchDbClient;
import com.groocraft.couchdb.slacker.configuration.CouchDbProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
@Slf4j
public class SignedMeterDataDao {

    @Value("${couchdb.client.url}")
    private String url;

    @Value("${couchdb.client.username}")
    private String user;

    @Value("${couchdb.client.password}")
    private String pass;

    private CouchDbProperties properties;
    private CouchDbClient client;

    public SignedMeterDataDao() {
        log.info("url at cons: {}", url);
    }

    @PostConstruct
    private void init() {

    }
}
