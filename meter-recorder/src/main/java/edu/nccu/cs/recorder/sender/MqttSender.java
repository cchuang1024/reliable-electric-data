package edu.nccu.cs.recorder.sender;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import edu.nccu.cs.domain.MeterData;
import edu.nccu.cs.domain.SignedMeterData;
import edu.nccu.cs.recorder.config.MqttConfig;
import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import edu.nccu.cs.recorder.fetcher.SignedMeterRepository;
import edu.nccu.cs.utils.DataConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static edu.nccu.cs.recorder.sender.SenderStateEntity.STATE_FINISHED;
import static edu.nccu.cs.recorder.sender.SenderStateEntity.STATE_PENDING;
import static edu.nccu.cs.utils.ExceptionUtils.getStackTrace;
import static java.lang.System.currentTimeMillis;

@Component
@Slf4j
public class MqttSender {

    @Autowired
    private MqttConfig.NoticeGateway noticeGateway;

    public void send(SignedMeterEntity meter) throws Exception {
        var data = meter.getData();

        noticeGateway.sendToMqtt(DataConvertUtils.jsonFromObject(
                SignedMeterData.builder()
                               .meterData(MeterData.builder()
                                                   .power(data.getPower())
                                                   .energy(data.getEnergy())
                                                   .build())
                               .signature(Base64.getEncoder().encodeToString(data.getSignature()))
                               .build()));

    }
}
