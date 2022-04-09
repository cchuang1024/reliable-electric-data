package edu.nccu.cs.recorder.data;

import edu.nccu.cs.domain.SignedMeterData;
import edu.nccu.cs.exception.SystemException;
import edu.nccu.cs.utils.DataConvertUtils;
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

        log.info("length of json: {}", targetJson.length());
        log.info("length of cbor: {}", targetCbor.length);
    }
}
