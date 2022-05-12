package edu.nccu.cs.dispatchcloud.config;

import edu.nccu.cs.dispatchcloud.receiver.ReceiverHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class Routes {

    public RouterFunction<ServerResponse> routeReceiver(ReceiverHandler handler){
        return RouterFunctions.route(POST("/edgeData").and(accept(MediaType.APPLICATION_JSON)), handler::saveEdgeData);
    }
}
