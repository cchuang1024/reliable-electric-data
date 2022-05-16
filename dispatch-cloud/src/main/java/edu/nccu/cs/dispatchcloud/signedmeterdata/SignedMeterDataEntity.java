package edu.nccu.cs.dispatchcloud.signedmeterdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groocraft.couchdb.slacker.DocumentBase;
import com.groocraft.couchdb.slacker.annotation.Document;
import lombok.*;

import java.sql.Timestamp;

@Document("meter")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignedMeterDataEntity extends DocumentBase {

    public static final long FIRST_PRETIMESTAMP = 0L;

    @JsonProperty("edgeId")
    private String edgeId;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("power")
    private Long power;

    @JsonProperty("energy")
    private Long energy;

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("preTimestamp")
    private Long preTimestamp;

    @JsonProperty("checkState")
    private CheckState checkState;

    @JsonProperty("initTime")
    private Timestamp initTime;

    @JsonProperty("fixTime")
    private Timestamp fixTime;

    @JsonProperty("doneTime")
    private Timestamp doneTime;

    public enum CheckState {
        INIT,
        FIX,
        DONE
    }
}
