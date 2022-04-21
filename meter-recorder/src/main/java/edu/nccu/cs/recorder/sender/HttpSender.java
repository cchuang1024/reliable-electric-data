package edu.nccu.cs.recorder.sender;

import edu.nccu.cs.domain.SignedMeterDataRequest;
import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import edu.nccu.cs.utils.DataConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HttpSender {

    @Value("${collector.host}")
    private String collectorHost;

    @Value("${collector.port}")
    private Integer collectorPort;

    public void send(SignedMeterEntity meter) throws Exception {

    }
}
