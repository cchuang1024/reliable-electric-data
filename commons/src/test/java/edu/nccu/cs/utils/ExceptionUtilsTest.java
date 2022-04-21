package edu.nccu.cs.utils;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class ExceptionUtilsTest {

    @Test
    public void testGetStackTrace(){
        Exception ex = new Exception("test");
        String stackTrace = ExceptionUtils.getStackTrace(ex);
        Assertions.assertThat(stackTrace).isNotBlank();
        log.info("stack trace: {}", stackTrace);
    }
}
