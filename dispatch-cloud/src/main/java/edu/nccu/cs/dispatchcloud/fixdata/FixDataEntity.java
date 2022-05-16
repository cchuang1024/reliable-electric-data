package edu.nccu.cs.dispatchcloud.fixdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groocraft.couchdb.slacker.DocumentBase;
import com.groocraft.couchdb.slacker.annotation.Document;
import lombok.*;

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

    public enum FixState {
        INIT,
        WAIT,
        DONE
    }
}
