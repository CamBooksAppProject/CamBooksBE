package org.example.v1.naverSearch.service;

import lombok.RequiredArgsConstructor;
import org.example.v1.naverSearch.dto.NaverBookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NaverBookService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public NaverBookResponse searchBooks(String query, int display, int start) {
        try {
            String url = "https://openapi.naver.com/v1/search/book.json?query=" + query
                    + "&display=" + display
                    + "&start=" + start;

            System.out.println("Final Request URL: " + url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<NaverBookResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    NaverBookResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to search books", e);
        }
    }

}
