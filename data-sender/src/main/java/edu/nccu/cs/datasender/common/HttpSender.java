package edu.nccu.cs.datasender.common;

import edu.nccu.cs.protocol.MeterDataRequest;
import edu.nccu.cs.protocol.MeterDataResponse;
import edu.nccu.cs.protocol.MeterDataResponse.MessageType;
import edu.nccu.cs.protocol.MeterDataResponse.PiggyBackMessage;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static edu.nccu.cs.protocol.Constants.PATH_ELECTRIC_DATA;

@Component
@Slf4j
public class HttpSender {

    @Value("${cloud.host}")
    private String cloudHost;

    @Value("${cloud.port}")
    private Integer cloudPort;

    public MeterDataResponse sendMeterData(MeterDataRequest<SignedMeterDataRequest> request) {
        // log.info("send: {}", request);

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                                                          .codecs(configurer ->
                                                                  configurer.defaultCodecs()
                                                                            .maxInMemorySize(10 * 1024))
                                                          .build();
        // WebClient client = WebClient.create(buildUrl());
        WebClient client = WebClient.builder()
                                    .exchangeStrategies(strategies)
                                    .baseUrl(buildUrl())
                                    .build();

        return handlePost(client, request);
    }

    private String buildUrl() {
        return String.format("http://%s:%d", cloudHost, cloudPort);
    }

    private MeterDataResponse handlePost(WebClient client, MeterDataRequest<SignedMeterDataRequest> request) {
        return client.post()
                     .uri(PATH_ELECTRIC_DATA)
                     .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                     .body(BodyInserters.fromValue(request))
                     .accept(MediaType.APPLICATION_JSON)
                     .retrieve()
                     .bodyToMono(MeterDataResponse.class)
                     .onErrorResume(e ->
                             Mono.just(MeterDataResponse.builder()
                                                        .message(PiggyBackMessage.builder()
                                                                                 .message(e.getMessage())
                                                                                 .cause(e)
                                                                                 .type(MessageType.ERROR)
                                                                                 .build())
                                                        .build()))
                     .block();
    }
}
