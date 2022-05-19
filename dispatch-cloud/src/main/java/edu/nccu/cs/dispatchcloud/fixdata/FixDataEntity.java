package edu.nccu.cs.dispatchcloud.fixdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.nccu.cs.entity.DocumentBase;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document("fix_data")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FixDataEntity extends DocumentBase {
    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("state")
    private FixState state;

    @JsonProperty("initTime")
    private Timestamp initTime;

    @JsonProperty("waitTime")
    private Timestamp waitTime;

    @JsonProperty("doneTime")
    private Timestamp doneTime;

    public FixDataEntity init() {
        String id = String.format("FIX::%d", timestamp);
        super.setId(id);
        return this;
    }

    public enum FixState {
        INIT,
        WAIT,
        DONE
    }
}
