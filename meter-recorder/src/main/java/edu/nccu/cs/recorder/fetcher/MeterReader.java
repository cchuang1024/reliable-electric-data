package edu.nccu.cs.recorder.fetcher;

import edu.nccu.cs.domain.MeterData;
import edu.nccu.cs.recorder.config.RecorderConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class MeterReader implements Supplier<MeterData> {

    @Autowired
    private RecorderConfiguration configuration;

    @Override
    public MeterData get() {
        String url = String.format("http://%s:%d", configuration.getMeter().getHost(), configuration.getMeter().getPort());
        log.warn("url: {}", url);
        WebClient client = WebClient.create(url);

        Mono<MeterData> resp =
                client.get()
                      .uri("/normal")
                      .accept(MediaType.APPLICATION_JSON)
                      .retrieve()
                      .bodyToMono(MeterData.class);

        return resp.block();
    }
}
