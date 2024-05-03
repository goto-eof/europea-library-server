package com.andreidodu.europealibrary.client;

import com.andreidodu.europealibrary.dto.GoogleBookResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "google-books", url = "${com.andreidodu.europea-library.google.books.query-url}")
public interface GoogleBooksClient {
    @RequestMapping(method = RequestMethod.GET)
    GoogleBookResponseDTO retrieveMetaInfo(@RequestParam String q, @RequestParam int maxResults, @RequestParam String key);
}
