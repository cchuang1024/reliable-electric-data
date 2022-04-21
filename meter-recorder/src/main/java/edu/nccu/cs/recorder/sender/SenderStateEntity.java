package edu.nccu.cs.recorder.sender;

import edu.nccu.cs.entity.TemporalEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Slf4j
@Entity(value = "SenderState",
        indices = {
                @Index(value = "timestamp",
                        type = IndexType.NonUnique),
        })
public class SenderStateEntity implements TemporalEntity, Mappable {
    public static final int STATE_INIT = 1;
    public static final int STATE_WAIT = 2;
    public static final int STATE_PENDING = 2;
    public static final int STATE_FINISHED = 3;
    public static final int STATE_ABANDON = 4;

    public static final int RETRY_INIT = 0;
    public static final int RETRY_MAX = 300;

    @Id
    private long timestamp;

    private int state;
    private int retry;
    private long actionTime;

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = Document.createDocument();
        document.put("timestamp", getTimestamp());
        document.put("state", getState());
        document.put("retry", getRetry());
        document.put("actionTime", getActionTime());

        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        if (document != null) {
            setTimestamp(document.get("timestamp", Long.class));
            setState(document.get("state", Integer.class));
            setRetry(document.get("retry", Integer.class));
            setActionTime(document.get("actionTime", Long.class));
        }
    }
}
