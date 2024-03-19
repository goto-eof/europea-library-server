package com.andreidodu.europealibrary.client;

import com.andreidodu.europealibrary.dto.OpenLibraryAuthenticationRequestDTO;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

public interface OpenLibraryClient {
    @RequestLine("POST /account/login")
    String authenticate(@RequestBody OpenLibraryAuthenticationRequestDTO authenticationRequestDTO);
}
