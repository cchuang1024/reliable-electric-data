package edu.nccu.cs.recorder.fetcher;

import edu.nccu.cs.domain.SignedMeterData;
import edu.nccu.cs.recorder.config.RecorderConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class SignedMeterReader implements Supplier<SignedMeterData> {

    @Autowired
    private RecorderConfiguration configuration;

    @Override
    public SignedMeterData get() {
        String url = String.format("http://%s:%d", configuration.getMeter().getHost(), configuration.getMeter().getPort());
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
