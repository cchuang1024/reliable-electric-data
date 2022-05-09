package edu.nccu.cs.datasender.fetcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groocraft.couchdb.slacker.DocumentBase;
import com.groocraft.couchdb.slacker.annotation.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Document("meter")
@Getter
@Setter
@ToString
public class SignedMeterDataEntity extends DocumentBase {
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

    @JsonProperty("init")
    private Boolean init;

    @JsonProperty("retry")
    private Integer retry;
}
