package org.example.v1.post.usedTrade.controller;

import lombok.RequiredArgsConstructor;
import org.example.v1.post.usedTrade.dto.UsedTradeRequestDto;
import org.example.v1.post.usedTrade.dto.UsedTradeResponseDto;
import org.example.v1.post.usedTrade.service.UsedTradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cambooks/used-trade")
@RequiredArgsConstructor
public class UsedTradeController {

    private final UsedTradeService usedTradeService;

    @PostMapping("/{memberId}")
    public ResponseEntity<UsedTradeResponseDto> create(@PathVariable Long memberId, @RequestBody UsedTradeRequestDto dto) {
        UsedTradeResponseDto usedTradeResponseDto = usedTradeService.create(memberId, dto);
        return ResponseEntity.ok(usedTradeResponseDto);
    }
    @GetMapping
    public ResponseEntity<List<UsedTradeResponseDto>> getAll() {
        return ResponseEntity.ok(usedTradeService.getAll());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<UsedTradeResponseDto> getById(@PathVariable Long postId) {
        return ResponseEntity.ok(usedTradeService.getById(postId));
    }
}