package edu.nccu.cs.recorder.config;

import org.assertj.core.api.Assertions;
import org.dizitart.no2.Nitrite;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RecorderConfigTest {

    @Autowired
    @Qualifier("signedMeterDB")
    private Nitrite signedMeterDB;

    @Autowired
    @Qualifier("senderMetaDB")
    private Nitrite senderMetaDB;

    @Test
    public void testLoadContext(){
        assertThat(signedMeterDB).isNotNull();
        assertThat(senderMetaDB).isNotNull();
    }
}
