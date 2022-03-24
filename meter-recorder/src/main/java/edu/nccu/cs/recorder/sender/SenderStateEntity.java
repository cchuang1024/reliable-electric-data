package edu.nccu.cs.recorder.sender;

import edu.nccu.cs.entity.TemporalKeyValueEntity;
import edu.nccu.cs.exception.SystemException;
import edu.nccu.cs.utils.ByteUtils;
import edu.nccu.cs.utils.DataConvertUtils;
import edu.nccu.cs.utils.ExceptionUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static edu.nccu.cs.utils.DataConvertUtils.cborFromObject;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Slf4j
public class SenderStateEntity implements TemporalKeyValueEntity {

    public static synchronized SenderStateEntity getInstantFromRawBytes(byte[] key, byte[] value) {
        long timestamp = ByteUtils.getLongFromBytes(key);
        try {
            SenderStateEntity.SenderData data = DataConvertUtils.objectFromCbor(SenderData.class, value);
            return SenderStateEntity.builder()
                                    .timestamp(timestamp)
                                    .data(data)
                                    .build();
        } catch (SystemException ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    public static final int STATE_INIT = 1;
    public static final int STATE_WAIT = 2;
    public static final int STATE_PENDING = 2;
    public static final int STATE_FINISHED = 3;
    public static final int STATE_ABANDON = 4;

    public static final int RETRY_INIT = 0;
    public static final int RETRY_MAX = 300;

    private long timestamp;
    private SenderData data;

    @Override
    public byte[] getKey() {
        return ByteUtils.getBytesFromLong(this.timestamp);
    }

    @Override
    public byte[] getValue() throws SystemException {
        return cborFromObject(this.data);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class SenderData {
        private int state;
        private int retry;
        private long actionTime;
    }
}
