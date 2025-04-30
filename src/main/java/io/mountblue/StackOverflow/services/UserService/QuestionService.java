package io.mountblue.StackOverflow.services.UserService;

import io.mountblue.StackOverflow.controllers.QuestionController;
import io.mountblue.StackOverflow.entity.Question;

import java.util.List;

public interface QuestionService {

    Question createNewQuestion(Question question, List<String> tags);
    void deleteQuestionById(Long id);
    Question updateQuestion (Question question, List<String> tagName);
    Question findQuestionById (Long id);
}
