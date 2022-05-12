package edu.nccu.cs.datasender.fetcher;

import edu.nccu.cs.datasender.signedmeterdata.SignedMeterDataDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class SignedMeterDataDaoTest {

    @Autowired
    private SignedMeterDataDao dao;

    @Test
    public void testPostConstruct(){
        log.info(dao.toString());
    }
}
