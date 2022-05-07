package edu.nccu.cs.datasender.fetcher;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SignedMeterDataRepositoryTest {

    @Autowired
    private SignedMeterDataRepository repository;

    @Test
    public void testFindAll() {

    }
}
