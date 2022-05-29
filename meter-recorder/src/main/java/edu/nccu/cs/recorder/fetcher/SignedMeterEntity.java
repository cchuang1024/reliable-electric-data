package edu.nccu.cs.recorder.fetcher;

import edu.nccu.cs.domain.SignedMeterData;
import edu.nccu.cs.entity.TemporalEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Slf4j
@Entity(value = "SignedMeter",
        indices = {
                @Index(value = "timestamp",
                        type = IndexType.NonUnique),
        })
public class SignedMeterEntity implements TemporalEntity, Mappable {

    public static synchronized SignedMeterEntity getInstanceByInstantAndData(Instant instant, SignedMeterData data) {
        return SignedMeterEntity.builder()
                                .timestamp(instant.toEpochMilli())
                                .energy(data.getMeterData().getEnergy())
                                .power(data.getMeterData().getPower())
                                .signature(data.getSignature())
                                .preTimestamp(0L)
                                .build();
    }

    public static synchronized SignedMeterEntity getInstanceByPreAndNowAndData(Instant pre, Instant now, SignedMeterData data) {
        return SignedMeterEntity.builder()
                                .timestamp(now.toEpochMilli())
                                .energy(data.getMeterData().getEnergy())
                                .power(data.getMeterData().getPower())
                                .signature(data.getSignature())
                                .preTimestamp(pre.toEpochMilli())
                                .build();
    }

    @Id
    private long timestamp;

    private long power;
    private long energy;
    private String signature;
    private long preTimestamp;

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = Document.createDocument();
        document.put("timestamp", getTimestamp());
        document.put("power", getPower());
        document.put("energy", getEnergy());
        document.put("signature", getSignature());
        document.put("preTimestamp", getPreTimestamp());

        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        if (document != null) {
            setTimestamp(document.get("timestamp", Long.class));
            setPower(document.get("power", Long.class));
            setEnergy(document.get("energy", Long.class));
            setSignature(document.get("signature", String.class));
            setPreTimestamp(document.get("preTimestamp", Long.class));
        }
    }
}
