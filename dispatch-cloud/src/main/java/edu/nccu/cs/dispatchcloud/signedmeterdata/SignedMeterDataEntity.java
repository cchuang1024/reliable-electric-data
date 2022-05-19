package edu.nccu.cs.dispatchcloud.signedmeterdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.nccu.cs.dispatchcloud.common.DocumentBase;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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
    private Date initTime;

    @JsonProperty("fixTime")
    private Date fixTime;

    @JsonProperty("doneTime")
    private Date doneTime;

    public SignedMeterDataEntity init() {
        String id = String.format("%s::%d", edgeId, timestamp);
        super.setId(id);
        return this;
    }

    public enum CheckState {
        INIT,
        FIX,
        DONE
    }
}
