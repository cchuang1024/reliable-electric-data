package edu.nccu.cs.recorder.sender;

import edu.nccu.cs.domain.SignedMeterDataRequest;
import edu.nccu.cs.recorder.config.MqttConfig;
import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import edu.nccu.cs.utils.DataConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MqttSender {

    @Autowired
    private MqttConfig.MeterDataGateway meterDataGateway;

    public void send(SignedMeterEntity meter) throws Exception {
        meterDataGateway.sendToMqtt(DataConvertUtils.jsonFromObject(
                SignedMeterDataRequest.builder()
                                      .timestamp(meter.getTimestamp())
                                      .power(meter.getPower())
                                      .energy(meter.getEnergy())
                                      .signature(meter.getSignature())
                                      .build()));

    }
}
