package org.example.v1.naverSearch.controller;

import lombok.RequiredArgsConstructor;
import org.example.v1.naverSearch.dto.NaverBookResponse;
import org.example.v1.naverSearch.service.NaverBookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class NaverBookController {

    private final NaverBookService naverBookService;

    @GetMapping("/search")
    public NaverBookResponse search(@RequestParam String query,
                                    @RequestParam(defaultValue = "10") int display,
                                    @RequestParam(defaultValue = "1") int start) {
        return naverBookService.searchBooks(query, display, start);
    }

    @GetMapping("/search/isbn")
    public NaverBookResponse searchByIsbn(@RequestParam String isbn) {
        return naverBookService.searchBooks(isbn, 10, 1);
    }
}
