        package org.example.gateway.Security;

        import io.jsonwebtoken.security.Keys;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
        import org.springframework.security.config.web.server.ServerHttpSecurity;
        import org.springframework.security.core.authority.SimpleGrantedAuthority;
        import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
        import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
        import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
        import org.springframework.security.web.server.SecurityWebFilterChain;
        import reactor.core.publisher.Flux;

        import javax.crypto.SecretKey;
        import java.util.Base64;
        import java.util.List;


        @Configuration
        @EnableWebFluxSecurity
        public class SecurityConfig {
            @Value("${jwt.secret}")
            private String SECRET;



            @Bean
            public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {


                return http
                        .csrf(ServerHttpSecurity.CsrfSpec::disable)


                        .authorizeExchange(ex -> ex
                                        .pathMatchers("/api/v1/customer/auth/**").permitAll()
                                        .anyExchange().authenticated()
                        )
                        .oauth2ResourceServer(oauth -> oauth
                                .jwt(jwt -> jwt
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                )
                        )


                        .build();
            }


            @Bean
            public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {


                ReactiveJwtAuthenticationConverter converter =
                        new ReactiveJwtAuthenticationConverter();


                converter.setJwtGrantedAuthoritiesConverter(jwt -> {


                    List<String> roles = jwt.getClaimAsStringList("roles");


                    if (roles == null) {
                        roles = List.of(); // 👈 prevents NPE
                    }


                    return Flux.fromIterable(roles)
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role));
                });


                return converter;
            }
            @Bean
            public ReactiveJwtDecoder reactiveJwtDecoder() {
                byte[] decodedKey = Base64.getDecoder().decode(SECRET);

                if (decodedKey.length < 32) {
                    throw new IllegalStateException(
                            "JWT secret key must be at least 32 bytes after Base64 decoding"
                    );
                }

                SecretKey key = Keys.hmacShaKeyFor(decodedKey);
                return NimbusReactiveJwtDecoder.withSecretKey(key).build();
            }
        }