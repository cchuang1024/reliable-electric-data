package edu.nccu.cs.dispatchcloud.verifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groocraft.couchdb.slacker.DocumentBase;
import com.groocraft.couchdb.slacker.annotation.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Document("fixData")
@Getter
@Setter
@ToString
public class FixDataEntity extends DocumentBase {
    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("state")
    private FixState state;

    public enum FixState {
        INIT,
        WAIT,
        DONE;
    }
}
