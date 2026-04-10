package com.common.Feignclient;

import com.common.Constants.HeaderConstant;
import com.common.SecurityCommon.UserContext;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignclientInterceptor {

    @Value("${internal.api-key}")
    private String internalApiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            String userId = UserContext.getUserId();

            if (userId != null) {
                requestTemplate.header(HeaderConstant.USER_ID, userId);
            }


            requestTemplate.header(HeaderConstant.INTERNAL_API_KEY, internalApiKey);
        };
    }
}
