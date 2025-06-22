package org.example.v1.post.usedTrade.controller;

import lombok.RequiredArgsConstructor;
import org.example.v1.post.usedTrade.dto.UsedTradePreviewDto;
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
    public ResponseEntity<List<UsedTradePreviewDto>> getAll() {
        return ResponseEntity.ok(usedTradeService.getAll());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<UsedTradeResponseDto> getById(@PathVariable Long postId) {
        return ResponseEntity.ok(usedTradeService.getById(postId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<UsedTradeResponseDto> update(@PathVariable Long postId, @RequestParam Long memberId, @RequestBody UsedTradeRequestDto dto) {
        UsedTradeResponseDto updated = usedTradeService.update(postId, memberId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> delete(@PathVariable Long memberId, @RequestParam Long postId) {
        usedTradeService.delete(memberId, postId);
        return ResponseEntity.noContent().build();
    }
}