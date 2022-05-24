package edu.nccu.cs.datasender.dev;

import edu.nccu.cs.protocol.MeterDataRequest;
import edu.nccu.cs.protocol.MeterDataResponse;
import edu.nccu.cs.protocol.MeterDataResponse.MessageType;
import edu.nccu.cs.protocol.MeterDataResponse.PiggyBackMessage;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

// @RestController
@Slf4j
public class TestCloudController {

    // @PostMapping(PATH_ELECTRIC_DATA)
    public MeterDataResponse testReceive(
            // @RequestBody
            MeterDataRequest<SignedMeterDataRequest> request) {
        log.info("received request: {}", request);
        return MeterDataResponse.builder()
                                .cloudTime(System.currentTimeMillis())
                                .message(PiggyBackMessage.builder()
                                                         .type(MessageType.SUCCESS)
                                                         .payload(Collections.emptyList())
                                                         .build())
                                .build();
    }

}
