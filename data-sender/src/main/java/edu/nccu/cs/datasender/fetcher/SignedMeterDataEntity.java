package edu.nccu.cs.datasender.fetcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groocraft.couchdb.slacker.DocumentBase;
import com.groocraft.couchdb.slacker.annotation.Document;

@Document("meter")
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
}
