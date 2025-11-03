package org.example.v1.post.usedTrade.controller;

import lombok.RequiredArgsConstructor;
import org.example.v1.member.service.MemberService;
import org.example.v1.post.usedTrade.domain.TradeStatusType;
import org.example.v1.post.usedTrade.domain.UsedTradeStatus;
import org.example.v1.post.usedTrade.dto.UsedTradePreviewDto;
import org.example.v1.post.usedTrade.dto.UsedTradeRequestDto;
import org.example.v1.post.usedTrade.dto.UsedTradeResponseDto;
import org.example.v1.post.usedTrade.service.UsedTradeService;
import org.example.v1.post.usedTrade.service.UsedTradeStatusService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/cambooks/used-trade")
@RequiredArgsConstructor
public class UsedTradeController {

    private final UsedTradeService usedTradeService;
    private final MemberService memberService;
    private final UsedTradeStatusService usedTradeStatusService;

    @PostMapping(value = "/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsedTradeResponseDto> create(
            @PathVariable Long memberId,
            @RequestPart(value = "dto") UsedTradeRequestDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        UsedTradeResponseDto usedTradeResponseDto = usedTradeService.create(memberId, dto, images);
        return ResponseEntity.ok(usedTradeResponseDto);
    }
    @GetMapping
    public ResponseEntity<List<UsedTradePreviewDto>> getAll(@RequestParam(required = false) Long universityId) {
        if(universityId == null){
            return ResponseEntity.ok(usedTradeService.getAll());
        }else
            return ResponseEntity.ok(usedTradeService.getByUniversity(universityId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<UsedTradeResponseDto> getById(@PathVariable Long postId) {
        return ResponseEntity.ok(usedTradeService.getById(postId));
    }

    @GetMapping("/{postId}/banner")
    public ResponseEntity<UsedTradeResponseDto> getByIdForBanner(@PathVariable Long postId) {
        return ResponseEntity.ok(usedTradeService.getByIdForBanner(postId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<UsedTradeResponseDto> update(@PathVariable Long postId, @RequestParam Long memberId, @RequestBody UsedTradeRequestDto dto) {
        UsedTradeResponseDto updated = usedTradeService.update(postId, memberId, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/trade")
    public ResponseEntity<?> trade(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long postId, @RequestParam Long buyerId) {
        String email = userDetails.getUsername();
        usedTradeStatusService.updateStatus(postId, email, buyerId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId) {
        String email = userDetails.getUsername();
        usedTradeService.deleteByPostId(email, postId);
        return ResponseEntity.ok().build();
    }
}