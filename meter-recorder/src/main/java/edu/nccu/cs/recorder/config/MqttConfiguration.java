package edu.nccu.cs.recorder.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mqtt")
@Getter
@Setter
@ToString
public class MqttConfiguration {
    private String url;
    private String clientId;
    private String topic;
    private Integer qos;
    private Integer timeout;
}
