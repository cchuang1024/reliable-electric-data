package edu.nccu.cs.dispatchcloud.receiver;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class SignedMeterDataRepositoryTest {

    @Autowired
    private SignedMeterDataRepository repository;

    @Test
    public void testLoadContext() {
        assertThat(repository).isNotNull();
    }
}
