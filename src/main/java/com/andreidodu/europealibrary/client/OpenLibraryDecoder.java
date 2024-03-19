package com.andreidodu.europealibrary.client;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class OpenLibraryDecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        // TODO extract to const or put in properties file
        if (response.request().httpMethod().name().equals("POST") && response.request().url().equals("https://openlibrary.org/account/login")) {
            List<HttpCookie> cookie = HttpCookie.parse(Objects.requireNonNull(response.headers().get(HttpHeaders.SET_COOKIE).stream().findFirst().orElse(null)));
            Response.Body newBody = response.toBuilder()
                    .body(cookie.stream()
                                    .findFirst()
                                    .map(HttpCookie::getValue)
                                    .orElse(null),
                            Charset.defaultCharset())
                    .build()
                    .body();
            return new Default()
                    .decode(Response.builder()
                                    .body(newBody)
                                    .request(response.request())
                                    .build(),
                            type);
        }
        return new Default().decode(response, type);
    }
}
