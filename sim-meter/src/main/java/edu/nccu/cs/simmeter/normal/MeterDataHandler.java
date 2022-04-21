package edu.nccu.cs.simmeter.normal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class MeterDataHandler {

    @Autowired
    private MeterDataService service;

    public Mono<ServerResponse> getMeterData(ServerRequest request) {
        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(service.getMeterNow()));
    }
}
