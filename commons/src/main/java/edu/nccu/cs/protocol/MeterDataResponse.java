package edu.nccu.cs.protocol;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterDataResponse {
    private Long cloudTime;
    private PiggyBackMessage message;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PiggyBackMessage{
        private MessageType type;
        private List<Long> payload;
    }

    public enum MessageType {
        SUCCESS,
        NOT_AUTHORIZED,
        FIX;
    }
}
