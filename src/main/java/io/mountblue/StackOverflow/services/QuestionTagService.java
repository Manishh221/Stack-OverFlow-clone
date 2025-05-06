package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.QuestionTag;

import java.util.List;

public interface QuestionTagService {
    void deleteQuestionTag(QuestionTag questionTag);

    List<QuestionTag> getTags(Question question);

    void saveQuestionTag(QuestionTag questionTag);
}
