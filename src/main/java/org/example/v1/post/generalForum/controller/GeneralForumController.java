package org.example.v1.post.generalForum.controller;

import org.example.v1.post.generalForum.domain.GeneralForum;
import org.example.v1.post.generalForum.dto.GeneralForumPreviewDto;
import org.example.v1.post.generalForum.dto.GeneralForumRequestDto;
import org.example.v1.post.generalForum.dto.GeneralForumResponseDto;
import org.example.v1.post.generalForum.service.GeneralForumService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cambooks/general-forum")
public class GeneralForumController {
    private final GeneralForumService generalForumService;

    public GeneralForumController(GeneralForumService generalForumService) {
        this.generalForumService = generalForumService;
    }

    @PostMapping
    public ResponseEntity<GeneralForumPreviewDto> createGeneralForum(@AuthenticationPrincipal UserDetails userDetails, @RequestBody GeneralForumRequestDto generalForumRequestDto) {
        String email = userDetails.getUsername();
        GeneralForumPreviewDto generalForumPreviewDto = generalForumService.create(email, generalForumRequestDto);
        return ResponseEntity.ok(generalForumPreviewDto);
    }
    @GetMapping
    public ResponseEntity<List<GeneralForumPreviewDto>> getAll () {
        return ResponseEntity.ok(generalForumService.getAll());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<GeneralForumResponseDto> getGeneralForum(@PathVariable Long postId) {
        GeneralForumResponseDto byId = generalForumService.getById(postId);
        return ResponseEntity.ok(byId);
    }
}
