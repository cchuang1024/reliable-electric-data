package edu.nccu.cs.simmeter.normal;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "meter")
@Getter
@Setter
@ToString
public class MeterDataConfig {

    public static final int BASE_VALUE_SIZE = 7;
    public static final double DIFF_VALUE = 25.0;

    private List<Double> baseValues;
}
