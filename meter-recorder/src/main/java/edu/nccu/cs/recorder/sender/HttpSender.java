package edu.nccu.cs.recorder.sender;

import edu.nccu.cs.recorder.config.RecorderConfiguration;
import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import edu.nccu.cs.utils.DataConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static edu.nccu.cs.recorder.sender.SignedMeterDataRequest.DB_NAME;

@Component
@Slf4j
public class HttpSender {

    @Autowired
    private RecorderConfiguration configuration;

    public void send(SignedMeterEntity meter) throws Exception {
        SignedMeterDataRequest saved =
                SignedMeterDataRequest.builder()
                                      .recorderId(configuration.getRecorder().getId())
                                      .timestamp(meter.getTimestamp())
                                      .preTimestamp(meter.getPreTimestamp())
                                      .power(meter.getPower())
                                      .energy(meter.getEnergy())
                                      .signature(meter.getSignature())
                                      .build()
                                      .init();

        log.info("ready to send: {}", DataConvertUtils.jsonFromObject(saved));

        for (RecorderConfiguration.CollectorProfile collector : configuration.getCollectors()) {
            WebClient client = WebClient.create(String.format("http://%s:%d", collector.getHost(), collector.getPort()));

            String response = client.post()
                                    .uri(String.format("/%s", DB_NAME))
                                    .headers(header -> {
                                        header.setContentType(MediaType.APPLICATION_JSON);
                                        header.setBasicAuth(collector.getUser(), collector.getPass());
                                    })
                                    .body(BodyInserters.fromValue(saved))
                                    .accept(MediaType.APPLICATION_JSON)
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .onErrorResume(e -> Mono.just(e.getMessage()))
                                    .block();

            log.info("response to {}: {}", collector.getId(), response);
        }
    }
}
