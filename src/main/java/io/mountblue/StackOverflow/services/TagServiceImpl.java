package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.dto.TagWithCountDTO;
import io.mountblue.StackOverflow.entity.QuestionTag;
import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.repositories.QuestionTagRepository;
import io.mountblue.StackOverflow.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;

    public TagServiceImpl(TagRepository tagRepository, QuestionTagRepository questionTagRepository) {
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
    }

    @Override
    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Page<TagWithCountDTO> findPaginatedTags(Pageable pageable) {
        return tagRepository.findAllTagsWithQuestionCount(pageable);
    }

    @Override
    public Page<TagWithCountDTO> searchTags(String keyword, Pageable pageable) {
        return tagRepository.searchTagWithQuestionCount(keyword, pageable);
    }

    @Override
    public Page<TagWithCountDTO> searchTagsSortedByQuestionCount(String keyword, Pageable pageable) {
        return tagRepository.searchTagWithQuestionCountSorted(keyword, pageable);
    }

    @Override
    public Page<TagWithCountDTO> findAllTagsSortedByQuestionCount(Pageable pageable) {
        return tagRepository.findAllTagsSortedByQuestionCount(pageable);
    }



    @Override
    public List<Tag> findAllTagsByQuestionId(Long questionId) {
        List<QuestionTag> questionTags = questionTagRepository.findByQuestionId(questionId);
        List<Tag> tags = new ArrayList<>();

        for (var questionTag : questionTags) {
            tags.add(questionTag.getTag());
        }

        return tags;
    }

}
