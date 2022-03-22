package edu.nccu.cs.recorder.data;

import java.time.Instant;

import edu.nccu.cs.recorder.fetcher.SignedMeterData;
import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import edu.nccu.cs.recorder.exception.SystemException;
import edu.nccu.cs.recorder.util.ByteUtils;
import edu.nccu.cs.recorder.util.DataConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DataFormatTransTest {

    private String testJson =
            " {\"meterData\":{\"power\":0,\"energy\":0},\"signature\":\"MEQCIDTcSfwXsl2V3/WXtxmuDUaUx1ccCtdPOx0yc/ZO27XgAiAeuDX5YzvRQwgBfww41GsEOHLHBrs0dUI4NKiVUmIIpQ==\"}";

    @Test
    public void testLoadFromJson() throws SystemException {
        SignedMeterData signed = DataConvertUtils.objectFromJson(SignedMeterData.class, testJson);
        String targetJson = DataConvertUtils.jsonFromObject(signed);

        byte[] targetCbor = DataConvertUtils.cborFromObject(signed);
        byte[] targetSmile = DataConvertUtils.smileFromObject(signed);
        byte[] targetBson = DataConvertUtils.bsonFromObject(signed);

        log.info("length of json: {}", targetJson.length());
        log.info("length of cbor: {}", targetCbor.length);
        log.info("length of smile: {}", targetSmile.length);
        log.info("length of bson: {}", targetBson.length);

        SignedMeterEntity entity1 = SignedMeterEntity.getInstanceByInstantAndData(Instant.now(), signed);
        log.info("length of key: {}", entity1.getKey().length);
        log.info("length of value: {}", entity1.getValue().length);

        SignedMeterEntity.SignedMeterValue data =
                DataConvertUtils.objectFromCbor(SignedMeterEntity.SignedMeterValue.class, entity1.getValue());
        long timestamp = ByteUtils.getLongFromBytes(entity1.getKey());
        SignedMeterEntity entity2 = SignedMeterEntity.getInstanceByTimestampAndData(timestamp, data);
        log.info("entity 1: {}", entity1);
        log.info("entity 2: {}", entity2);
    }
}
