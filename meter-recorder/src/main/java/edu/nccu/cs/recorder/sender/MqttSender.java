package edu.nccu.cs.recorder.sender;

import java.util.Base64;

import edu.nccu.cs.domain.MeterData;
import edu.nccu.cs.domain.SignedMeterData;
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
        var data = meter.getData();

        meterDataGateway.sendToMqtt(DataConvertUtils.jsonFromObject(
                SignedMeterData.builder()
                               .meterData(MeterData.builder()
                                                   .power(data.getPower())
                                                   .energy(data.getEnergy())
                                                   .build())
                               .signature(Base64.getEncoder().encodeToString(data.getSignature()))
                               .build()));

    }
}
