package edu.nccu.cs.recorder.config;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "configuration")
@Getter
@Setter
@ToString
public class RecorderConfiguration {

    private RecorderProfile recorder;
    private MeterProfile meter;
    private List<CollectorProfile> collectors;

    @Getter
    @Setter
    @ToString
    public static class RecorderProfile {
        private String id;
    }

    @Getter
    @Setter
    @ToString
    public static class MeterProfile {
        private String host;
        private Integer port;
    }

    @Getter
    @Setter
    @ToString
    public static class CollectorProfile {
        private String id;
        private String host;
        private Integer port;
        private String user;
        private String pass;
    }
}
