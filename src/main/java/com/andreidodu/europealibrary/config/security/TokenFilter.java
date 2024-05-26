package com.andreidodu.europealibrary.config.security;

import com.andreidodu.europealibrary.constants.CookieConst;
import com.andreidodu.europealibrary.model.security.Token;
import com.andreidodu.europealibrary.repository.security.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            log.debug("client sent a token, checking it's validity...");

            String deviceId = Arrays.stream(
                            Optional.ofNullable(request.getCookies())
                                    .orElseThrow(() ->
                                            new AccessDeniedException("Authentication error")
                                    ))
                    .filter(cookie -> CookieConst.COOKIE_NAME_DEVICE_ID.equalsIgnoreCase(cookie.getName()))
                    .map(Cookie::getValue)
                    .findAny()
                    .orElseThrow(() -> new AccessDeniedException("Authentication error"));
            log.debug("device-id cookie is valid");
            token = token.substring(7);
            List<Token> tokenList = tokenRepository.findByTokenAndAgentIdAndValidFlag(token, deviceId, true);
            if (tokenList.isEmpty()) {
                throw new AccessDeniedException("Invalid token or device-id");
            }
        }
        log.debug("token is still valid");
        filterChain.doFilter(request, response);
    }
}
