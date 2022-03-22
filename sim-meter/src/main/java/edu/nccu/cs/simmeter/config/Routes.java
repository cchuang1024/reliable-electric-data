package edu.nccu.cs.simmeter.config;

import edu.nccu.cs.simmeter.normal.MeterDataHandler;
import edu.nccu.cs.simmeter.sign.SignedMeterDataHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> routeMeterData(MeterDataHandler handler) {
        return RouterFunctions.route(GET("/normal").and(accept(MediaType.APPLICATION_JSON)), handler::getMeterData);
    }

    @Bean
    public RouterFunction<ServerResponse> routeSignedMeterData(SignedMeterDataHandler handler) {
        return RouterFunctions.route(GET("/signed").and(accept(MediaType.APPLICATION_JSON)), handler::getSigned)
                              .andRoute(GET("/pubKey").and(accept(MediaType.APPLICATION_JSON)), handler::getPubKey);
    }
}
