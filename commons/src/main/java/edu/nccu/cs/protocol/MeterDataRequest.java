package edu.nccu.cs.protocol;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class MeterDataRequest<T> {

    private String applicationId;
    private String token;
    private Long edgeTime;
    private List<T> payload;
}
