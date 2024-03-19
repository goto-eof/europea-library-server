package com.andreidodu.europealibrary.client;

import feign.InvocationContext;
import feign.ResponseInterceptor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ClientResponseInterceptor implements ResponseInterceptor {
    @Override
    public Object intercept(InvocationContext invocationContext, Chain chain) throws Exception {
        feign.Response response = invocationContext.response();
        feign.Request request = response.request();
        var session = response.headers().get("session");
        return invocationContext.proceed();
    }
}
