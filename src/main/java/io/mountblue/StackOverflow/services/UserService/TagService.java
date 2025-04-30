package io.mountblue.StackOverflow.services.UserService;

import io.mountblue.StackOverflow.entity.Tag;

import java.util.List;

public interface TagService {

    List<Tag> findAllTags();
    List<Tag> findAllTagsByQuestionId(Long QuestionId);
}
