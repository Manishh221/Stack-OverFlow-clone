package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.dto.TagWithCountDTO;
import io.mountblue.StackOverflow.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {

    List<Tag> findAllTags();

    Page<TagWithCountDTO> searchTagsSortedByQuestionCount(String keyword, Pageable pageable);

    List<Tag> findAllTagsByQuestionId(Long questionId);

    Page<TagWithCountDTO> findPaginatedTags(Pageable pageable);

    Page<TagWithCountDTO> searchTags(String keyword, Pageable pageable);
}
