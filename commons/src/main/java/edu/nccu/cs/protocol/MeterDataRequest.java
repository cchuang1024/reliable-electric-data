package edu.nccu.cs.protocol;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MeterDataRequest<T> {

    private String applicationId;
    private String token;
    private Long edgeTime;
    private List<T> payload;
}
