package org.example.v1.member.controller;

import org.example.v1.post.usedTrade.dto.UsedTradePreviewDto;
import org.example.v1.post.usedTrade.service.UsedTradeService;
import org.example.v1.post.usedTrade.service.UsedTradeStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cambooks/mypage")
public class MyPageController {
    private final UsedTradeService usedTradeService;
    private final UsedTradeStatusService usedTradeStatusService;

    public MyPageController(UsedTradeService usedTradeService, UsedTradeStatusService usedTradeStatusService) {
        this.usedTradeService = usedTradeService;
        this.usedTradeStatusService = usedTradeStatusService;
    }
    @GetMapping("/purchases")
    public ResponseEntity<List<UsedTradePreviewDto>> getMyPurchasedTrades(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<UsedTradePreviewDto> list = usedTradeStatusService.findTradePreviewsByBuyerEmail(email);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/sales")
    public ResponseEntity<List<UsedTradePreviewDto>> getMySellingTrades(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<UsedTradePreviewDto> myPosts = usedTradeService.getMyUsedTradeListByEmail(email);
        return ResponseEntity.ok(myPosts);
    }

}
