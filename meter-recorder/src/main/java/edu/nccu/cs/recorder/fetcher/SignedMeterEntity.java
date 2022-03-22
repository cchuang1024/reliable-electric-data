package edu.nccu.cs.recorder.fetcher;

import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import edu.nccu.cs.recorder.domain.TemporalKeyValueEntity;
import edu.nccu.cs.recorder.exception.SystemException;
import edu.nccu.cs.recorder.util.ByteUtils;
import edu.nccu.cs.recorder.util.DataConvertUtils;
import edu.nccu.cs.recorder.util.ExceptionUtils;
import edu.nccu.cs.recorder.util.TypedPair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static edu.nccu.cs.recorder.util.ByteUtils.getBytesFromLong;
import static edu.nccu.cs.recorder.util.DataConvertUtils.cborFromObject;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Slf4j
public class SignedMeterEntity implements TemporalKeyValueEntity {

    private long timestamp;
    private SignedMeterValue data;

    public static synchronized SignedMeterEntity getInstanceByInstantAndData(Instant instant, SignedMeterData data) {
        return SignedMeterEntity.builder()
                                .timestamp(instant.toEpochMilli())
                                .data(SignedMeterValue.builder()
                                                      .energy(data.getMeterData().getEnergy())
                                                      .power(data.getMeterData().getPower())
                                                      .signature(Base64.getDecoder().decode(data.getSignature()))
                                                      .build())
                                .build();
    }

    public static synchronized SignedMeterEntity getInstanceByTimestampAndData(long timestamp, SignedMeterValue data) {
        return SignedMeterEntity.builder()
                                .timestamp(timestamp)
                                .data(data)
                                .build();
    }

    public static synchronized SignedMeterEntity getInstantFromRawBytes(byte[] key, byte[] value) {
        long timestamp = ByteUtils.getLongFromBytes(key);
        try {
            SignedMeterValue data = DataConvertUtils.objectFromCbor(SignedMeterValue.class, value);
            return SignedMeterEntity.builder()
                                    .timestamp(timestamp)
                                    .data(data)
                                    .build();
        } catch (SystemException ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    @Override
    public byte[] getKey() {
        return getBytesFromLong(this.timestamp);
    }

    @Override
    public byte[] getValue() throws SystemException {
        return cborFromObject(this.data);
    }

    @Override
    public List<TypedPair<byte[]>> getKeyValuePairs() throws SystemException {
        return Collections.singletonList(
                TypedPair.cons(getKey(), getValue()));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class SignedMeterValue {
        private long power;
        private long energy;
        private byte[] signature;
    }
}
