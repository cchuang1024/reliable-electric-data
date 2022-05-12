package edu.nccu.cs.datasender.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ZkServiceTest {

    @Autowired
    private ZkService zkService;

    @Test
    public void testCreateNode() {
        // zkService.destroy();

        log.info("create id node: {} and token node: {}", "1", "token");
        zkService.createNode("token", "1");

        log.info("create id node: {} and token node: {}", "2", "TOKEN");
        zkService.createNode("TOKEN", "2");
    }
}
