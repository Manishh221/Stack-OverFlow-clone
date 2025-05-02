package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.dto.TagWithCountDTO;
import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.services.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags")
    public String getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt") String sort,
            Model model) {

        Sort sorting;
        if ("question".equals(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "questionCount");
        } else {
            sorting = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<TagWithCountDTO> tagsPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            tagsPage = tagService.searchTags(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            tagsPage = tagService.findPaginatedTags(pageable);
        }

        model.addAttribute("tags", tagsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tagsPage.getTotalPages());
        model.addAttribute("sort", sort);

        return "TagsList";
    }


}
