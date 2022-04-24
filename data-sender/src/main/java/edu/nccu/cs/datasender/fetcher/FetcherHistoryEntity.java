package edu.nccu.cs.datasender.fetcher;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Index;

@Getter
@Setter
@AllArgsConstructor
// @NoArgsConstructor
@Builder
@ToString
@Slf4j
@Entity(value = "FetcherHistory",
        indices = {
                @Index(value = "timestamp",
                        type = IndexType.NonUnique),
        })
public class FetcherHistoryEntity implements Mappable {

        @Override
        public Document write(NitriteMapper mapper) {
                return null;
        }

        @Override
        public void read(NitriteMapper mapper, Document document) {

        }
}
