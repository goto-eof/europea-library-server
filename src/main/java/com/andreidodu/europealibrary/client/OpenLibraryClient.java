package com.andreidodu.europealibrary.client;

import com.andreidodu.europealibrary.dto.OpenLibraryAuthenticationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "open-library", url = "https://openlibrary.org")
public interface OpenLibraryClient {


    @RequestMapping(method = RequestMethod.POST, value = "/account/login", produces = "application/json")
    void authenticate(@RequestBody OpenLibraryAuthenticationRequestDTO authenticationRequestDTO);
}
