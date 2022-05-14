package edu.nccu.cs.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import edu.nccu.cs.exception.SystemException;
import edu.nccu.cs.utils.DataConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class MeterDataRequestTest {

    @Test
    public void testSerializeAndDeserialize() throws SystemException, JsonProcessingException {
        SignedMeterDataRequest payload = SignedMeterDataRequest.builder()
                                                               .energy(1L)
                                                               .power(1L)
                                                               .timestamp(System.currentTimeMillis())
                                                               .preTimestamp(0L)
                                                               .signature("T")
                                                               .build();
        MeterDataRequest<SignedMeterDataRequest> request = MeterDataRequest.<SignedMeterDataRequest>builder()
                                                                           .applicationId("1")
                                                                           .edgeTime(System.currentTimeMillis())
                                                                           .token("T")
                                                                           .payload(List.of(payload))
                                                                           .build();
        String json = DataConvertUtils.jsonFromObject(request);

        log.info("json: {}", json);

        MeterDataRequest<SignedMeterDataRequest> deserialized = DataConvertUtils.jsonMapper().readValue(json, new TypeReference<>() {
        });

        Assertions.assertThat(request)
                  .isEqualTo(deserialized);
    }
}
