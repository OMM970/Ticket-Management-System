package org.example.gateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;


@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    @Bean
    public RouteLocator genericRoutes(
            RouteLocatorBuilder builder,
            UriConfiguration uriConfiguration) {

        RouteLocatorBuilder.Builder routes = builder.routes();

        uriConfiguration.getRoutes().forEach((serviceName, serviceUri) ->
                routes.route(serviceName + "-service", r ->
                        r.path("/api/v1/" + serviceName + "/**","/api/v1/admin/" +  serviceName + "/**")
                                .uri(serviceUri)
                )
        );

        return routes.build();
    }
}
@Component
@ConfigurationProperties(prefix = "services")
@Getter
@Setter
class UriConfiguration {
    private Map<String, String> routes;
}
