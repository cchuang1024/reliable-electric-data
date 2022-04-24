package edu.nccu.cs.recorder.sender;

import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import edu.nccu.cs.utils.DataConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static edu.nccu.cs.recorder.sender.SignedMeterDataRequest.DB_NAME;

@Component
@Slf4j
public class HttpSender {

    @Value("${collector.host}")
    private String collectorHost;

    @Value("${collector.port}")
    private Integer collectorPort;

    @Value("${collector.user}")
    private String user;

    @Value("${collector.pass}")
    private String pass;

    @Value("${recorder.id}")
    private String recorderId;

    public void send(SignedMeterEntity meter) throws Exception {
        SignedMeterDataRequest saved =
                SignedMeterDataRequest.builder()
                                      .recorderId(recorderId)
                                      .timestamp(meter.getTimestamp())
                                      .preTimestamp(meter.getPreTimestamp())
                                      .power(meter.getPower())
                                      .energy(meter.getEnergy())
                                      .signature(meter.getSignature())
                                      .build()
                                      .init();

        log.info("ready to send: {}", DataConvertUtils.jsonFromObject(saved));

        WebClient client = WebClient.create(String.format("http://%s:%d", collectorHost, collectorPort));

        String response = client.post()
                                .uri(String.format("/%s", DB_NAME))
                                .headers(header -> {
                                    header.setContentType(MediaType.APPLICATION_JSON);
                                    header.setBasicAuth(user, pass);
                                })
                                .body(BodyInserters.fromValue(saved))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(String.class)
                                .onErrorResume(e -> Mono.just(e.getMessage()))
                                .block();

        log.info("response: {}", response);


    }

    private URI getUri() {
        var uri = URI.create(String.format("http://%s:%d/%s", collectorHost, collectorPort, DB_NAME));
        log.info("send to: {}", uri);
        return uri;
    }
}
