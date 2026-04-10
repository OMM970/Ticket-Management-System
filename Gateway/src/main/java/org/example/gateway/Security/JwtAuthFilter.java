package org.example.gateway.Security;

import org.springframework.beans.factory.annotation.Value;
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
public class JwtAuthFilter implements GlobalFilter, Ordered {
    @Value("${internal.api-key}")
    private String internalApiKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {

                    try {
                        Jwt jwt = auth.getToken();

                        String customerId = jwt.getSubject();
                        String email = jwt.getClaim("email") != null ? jwt.getClaim("email") : "";
                        String type = jwt.getClaim("type") != null ? jwt.getClaim("type") : "";

                        ServerHttpRequest mutatedRequest =
                                exchange.getRequest().mutate()
                                        .header("X-Customer-Id", customerId)
                                        .header("X-User-Email", email)
                                        .header("X-User-Type", type)
                                        .header("X-Api-Key", internalApiKey)
                                        .build();

                        return chain.filter(
                                exchange.mutate().request(mutatedRequest).build()
                        );

                    } catch (Exception e) {
                        return chain.filter(exchange);
                    }
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}