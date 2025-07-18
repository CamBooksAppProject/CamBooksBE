package org.example.v1.post.community.controller;

import org.example.v1.post.community.domain.Community;
import org.example.v1.post.community.dto.CommunityPreviewDto;
import org.example.v1.post.community.dto.CommunityRequestDto;
import org.example.v1.post.community.dto.CommunityResponseDto;
import org.example.v1.post.community.service.CommunityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/cambooks/community")
public class CommunityController {
    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @PostMapping
    public ResponseEntity<CommunityResponseDto> createCommunity(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart(value = "dto") CommunityRequestDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        String email = userDetails.getUsername();
        CommunityResponseDto communityResponseDto = communityService.create(email, dto, images);
        return new ResponseEntity<>(communityResponseDto, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<CommunityPreviewDto>> getAllCommunities(){
        List<CommunityPreviewDto> all = communityService.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }
    @GetMapping("/{postId}")
    public ResponseEntity<CommunityResponseDto> getCommunity(@PathVariable Long postId){
        CommunityResponseDto byId = communityService.getById(postId);
        return new ResponseEntity<>(byId, HttpStatus.OK);
    }
    @GetMapping("/check/{postId}")
    public ResponseEntity<?> checkWriterIsMe(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId){
        boolean isMine = communityService.checkWriterIdIsMyId(userDetails.getUsername(), postId);
        if(!isMine){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteCommunity(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId){
        String email = userDetails.getUsername();
        communityService.deleteCommunity(email, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/{postId}")
    public ResponseEntity<?> updateCommunity(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId, @RequestBody CommunityRequestDto dto){
        String email = userDetails.getUsername();
        communityService.updateCommunity(email, postId, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
