package org.example.v1.member.controller;

import org.example.v1.post.usedTrade.dto.UsedTradePreviewDto;
import org.example.v1.post.usedTrade.service.UsedTradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cambooks/mypage")
public class MyPageController {
    private final UsedTradeService usedTradeService;

    public MyPageController(UsedTradeService usedTradeService) {
        this.usedTradeService = usedTradeService;
    }
    @GetMapping("/posts/used-trade/{memberId}")
    public ResponseEntity<List<UsedTradePreviewDto>> getByMemberId(@PathVariable Long memberId) {
        List<UsedTradePreviewDto> myPosts = usedTradeService.findByMemberId(memberId);
        return ResponseEntity.ok(myPosts);
    }

}
