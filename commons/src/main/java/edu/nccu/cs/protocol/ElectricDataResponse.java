package edu.nccu.cs.protocol;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectricDataResponse<E, F> {

    private Long serverTime;
    private List<E> meterData;
    private List<F> fixData;

}
