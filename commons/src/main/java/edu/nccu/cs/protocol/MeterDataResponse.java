package edu.nccu.cs.protocol;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MeterDataResponse {
    private Long cloudTime;
    private PiggyBackMessage message;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class PiggyBackMessage {
        private MessageType type;
        private List<Long> payload;
        private String message;
        private Throwable cause;
    }

    public enum MessageType {
        SUCCESS,
        NOT_AUTHORIZED,
        FIX,
        ERROR;
    }
}
