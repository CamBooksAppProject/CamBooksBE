package org.example.v1.post.community.controller;


import org.example.v1.post.community.dto.CommunityJoinResponse;
import org.example.v1.post.community.service.CommunityJoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cambooks/community/join")
public class CommunityJoinController {

    private final CommunityJoinService communityJoinService;

    public CommunityJoinController(CommunityJoinService communityJoinService) {
        this.communityJoinService = communityJoinService;
    }

    @PostMapping("/{communityId}")
    public ResponseEntity<CommunityJoinResponse> joinCommunity(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long communityId) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(communityJoinService.joinAndLeaveCommunity(email, communityId));
    }
}
