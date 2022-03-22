package edu.nccu.cs.simmeter.config;

import java.security.KeyPair;
import java.security.Security;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class SecurityConfigTest {

    @BeforeAll
    public static void initProvider(){
        Security.addProvider(new BouncyCastleProvider());
    }

    @Autowired
    private KeyPair keyPair;

    @Test
    public void testLoadKeyPair(){
        Assertions.assertThat(keyPair).isNotNull();
    }
}
