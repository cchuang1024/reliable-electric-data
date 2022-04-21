package edu.nccu.cs.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MeterData {
    public static final int POWER_SCALE = 2;
    public static final double POWER_UNIT = 0.01;
    public static final long POWER_RATIO = 100;
    public static final int ENERGY_SCALE = 2;
    public static final double ENERGY_UNIT = 0.01;
    public static final long ENERGY_RATIO = 100;
    public static final int TIME_SCALE = 8;

    private long power;
    private long energy;
}
