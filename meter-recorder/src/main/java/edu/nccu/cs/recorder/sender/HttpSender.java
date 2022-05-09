package edu.nccu.cs.recorder.sender;

import edu.nccu.cs.recorder.config.RecorderConfiguration;
import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import edu.nccu.cs.utils.DataConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static edu.nccu.cs.recorder.sender.SignedMeterDataRequest.DB_NAME;

@Component
@Slf4j
public class HttpSender {

    @Autowired
    private RecorderConfiguration configuration;

    public void send(SignedMeterEntity meter) throws Exception {
        SignedMeterDataRequest saved = buildSentData(meter);
        log.info("ready to send: {}", DataConvertUtils.jsonFromObject(saved));

        for (RecorderConfiguration.CollectorProfile collector : configuration.getCollectors()) {
            WebClient client = WebClient.create(buildUrl(collector));

            String response = handlePost(client, collector, saved);
            log.info("response to {}: {}", collector.getId(), response);

            if (dbNotExists(response)) {
                log.info("create db: {}", DB_NAME);
                createDb(client, collector);
                handlePost(client, collector, saved);
            }
        }
    }

    private SignedMeterDataRequest buildSentData(SignedMeterEntity meter) {
        return SignedMeterDataRequest.builder()
                                     .recorderId(configuration.getRecorder().getId())
                                     .timestamp(meter.getTimestamp())
                                     .preTimestamp(meter.getPreTimestamp())
                                     .power(meter.getPower())
                                     .energy(meter.getEnergy())
                                     .signature(meter.getSignature())
                                     .build()
                                     .init();
    }

    private String buildUrl(RecorderConfiguration.CollectorProfile collector) {
        return String.format("http://%s:%d", collector.getHost(), collector.getPort());
    }

    private void createDb(WebClient client, RecorderConfiguration.CollectorProfile collector) {
        String response = client.put()
                                .uri(buildPath())
                                .headers(headers -> this.buildHeaders(headers, collector))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(String.class)
                                .onErrorResume(e -> Mono.just(e.getMessage()))
                                .block();
        log.info("create db response: {}", response);
    }

    private String buildPath() {
        return String.format("/%s", DB_NAME);
    }

    private boolean dbNotExists(String response) {
        return Objects.requireNonNull(response)
                      .contains("404 Not Found from POST");
    }

    private String handlePost(WebClient client, RecorderConfiguration.CollectorProfile collector, SignedMeterDataRequest saved) {
        return client.post()
                     .uri(buildPath())
                     .headers(headers -> this.buildHeaders(headers, collector))
                     .body(BodyInserters.fromValue(saved))
                     .accept(MediaType.APPLICATION_JSON)
                     .retrieve()
                     .bodyToMono(String.class)
                     .onErrorResume(e -> Mono.just(e.getMessage()))
                     .block();
    }

    private void buildHeaders(HttpHeaders headers, RecorderConfiguration.CollectorProfile collector) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(collector.getUser(), collector.getPass());
    }
}
