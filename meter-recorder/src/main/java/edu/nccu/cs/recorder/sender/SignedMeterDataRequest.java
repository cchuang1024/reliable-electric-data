package edu.nccu.cs.recorder.sender;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SignedMeterDataRequest {

    public static final String DB_NAME = "meter";
    public static final String STATE_INIT = "init";
    @JsonProperty("_id")
    private String id;

    private String recorderId;
    private Long timestamp;
    private Long power;
    private Long energy;
    private String signature;
    private Long preTimestamp;
    private String state;
    private Integer retry;

    public SignedMeterDataRequest init() {
        Objects.requireNonNull(recorderId, "recorder id must not be null.");
        Objects.requireNonNull(timestamp, "timestamp must not be null.");

        this.id = String.format("%s::%d", recorderId, timestamp);
        this.state = STATE_INIT;
        this.retry = 0;

        return this;
    }
}
