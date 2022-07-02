package edu.nccu.cs.datasender.signedmeterdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groocraft.couchdb.slacker.DocumentBase;
import com.groocraft.couchdb.slacker.annotation.Document;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Document("meter")
@Getter
@Setter
@ToString
@Builder
public class SignedMeterDataEntity extends DocumentBase {

    public static final String STATE_INIT = "init";
    public static final String STATE_PENDING = "pending";
    public static final String STATE_DONE = "done";

    @JsonProperty("recorderId")
    private String recorderId;

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

    @JsonProperty("state")
    private String state;

    @JsonProperty("retry")
    private Integer retry;
}
