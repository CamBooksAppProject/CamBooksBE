package org.example.v1.searchResult.controller;

import org.example.v1.post.community.service.CommunityService;
import org.example.v1.post.generalForum.dto.GeneralForumPreviewDto;
import org.example.v1.post.generalForum.service.GeneralForumService;
import org.example.v1.post.usedTrade.service.UsedTradeService;
import org.example.v1.searchResult.dto.SearchResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cambooks/search-result")
public class SearchResultController {

    private final GeneralForumService generalForumService;
    private final UsedTradeService usedTradeService;
    private final CommunityService communityService;

    public SearchResultController(
            GeneralForumService generalForumService,
            UsedTradeService usedTradeService,
            CommunityService communityService
    ) {
        this.generalForumService = generalForumService;
        this.usedTradeService = usedTradeService;
        this.communityService = communityService;
    }

    @GetMapping
    public ResponseEntity<List<SearchResultDto>> searchAll(@RequestParam String keyword) {
        List<GeneralForumPreviewDto> general = generalForumService.searchByKeyword(keyword);
        List<SearchResultDto> generalResults = general.stream()
                .map(post -> new SearchResultDto(
                        "generalForum",
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        null, // writerName은 필요 없으니 null 처리
                        post.getCreatedAt()
                ))
                .toList();

        List<SearchResultDto> used = usedTradeService.searchByKeyword(keyword);
        List<SearchResultDto> community = communityService.searchByKeyword(keyword);

        List<SearchResultDto> all = new ArrayList<>();
        all.addAll(generalResults);
        all.addAll(used);
        all.addAll(community);

        return ResponseEntity.ok(all);
    }

}
