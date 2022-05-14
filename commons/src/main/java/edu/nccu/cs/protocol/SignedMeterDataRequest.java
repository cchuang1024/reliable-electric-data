package edu.nccu.cs.protocol;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class SignedMeterDataRequest {
    private long timestamp;
    private long power;
    private long energy;
    private String signature;
    private long preTimestamp;
}
