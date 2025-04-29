package io.mountblue.StackOverflow.services.UserService;

import io.mountblue.StackOverflow.entity.Question;

import java.util.List;

public interface QuestionService {

    Question createNewQuestion(Question question, List<String> tags);

}
