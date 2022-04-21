package edu.nccu.cs.recorder.fetcher;

import java.util.function.Supplier;

import edu.nccu.cs.domain.SignedMeterData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class SignedMeterReader implements Supplier<SignedMeterData> {

    @Value("${meter.host}")
    private String meterHost;

    @Value("${meter.port}")
    private Integer meterPort;

    @Override
    public SignedMeterData get() {
        String url = String.format("http://%s:%d", meterHost, meterPort);
        log.warn("url: {}", url);
        WebClient client = WebClient.create(url);

        Mono<SignedMeterData> resp =
                client.get()
                      .uri("/signed")
                      .accept(MediaType.APPLICATION_JSON)
                      .retrieve()
                      .bodyToMono(SignedMeterData.class);

        return resp.block();
    }
}
