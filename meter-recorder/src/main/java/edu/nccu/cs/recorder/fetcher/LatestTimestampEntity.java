package edu.nccu.cs.recorder.fetcher;


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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Slf4j
@Entity(value = "LatestTimestamp",
        indices = {
                @Index(value = "id",
                        type = IndexType.NonUnique),
        })
public class LatestTimestampEntity implements Mappable {

    public static final long DEFAULT_ID = 1L;

    @Id
    private long id;

    private long timestamp;

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = Document.createDocument();
        document.put("id", getId());
        document.put("timestamp", getTimestamp());
        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        if (document != null) {
            setId(document.get("id", Long.class));
            setTimestamp(document.get("timestamp", Long.class));
        }
    }
}
