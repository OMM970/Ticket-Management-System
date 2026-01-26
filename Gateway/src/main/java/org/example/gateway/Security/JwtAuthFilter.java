package org.example.gateway.Security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter  implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {


                    Jwt jwt = auth.getToken();

                    String customerId = jwt.getSubject(); // sub
                    String email = jwt.getClaim("email");
                    String type = jwt.getClaim("type");

                    ServerHttpRequest mutatedRequest =
                            exchange.getRequest().mutate()
                                    .header("X-Customer-Id", customerId)
                                    .header("X-User-Email", email)
                                    .header("X-User-Type", type)
                                    .build();


                    return chain.filter(
                            exchange.mutate().request(mutatedRequest).build()
                    );
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
