package edu.nccu.cs.simmeter.sign;

import java.security.KeyPair;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static edu.nccu.cs.simmeter.sign.SignedMeterDataHandler.PubKey.fromKeyPair;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class SignedMeterDataHandler {

    @Autowired
    private SignedMeterDataService service;
    @Autowired
    private KeyPair keyPair;

    public Mono<ServerResponse> getPubKey(ServerRequest request) {
        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(fromValue(fromKeyPair(keyPair)));
    }

    public Mono<ServerResponse> getSigned(ServerRequest request) {
        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(fromValue(service.getSignedMeterNow()));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PubKey {
        public static synchronized PubKey fromKeyPair(KeyPair keyPair) {
            return new PubKey(Base64.toBase64String(keyPair.getPublic().getEncoded()));
        }

        private String publicKey;
    }
}
