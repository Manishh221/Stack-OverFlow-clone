package io.mountblue.StackOverflow.services.UserService;

import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.repositories.AnswerRepository;
import io.mountblue.StackOverflow.security.UserInfo;

public interface AnswerService {
    void saveAnswer(Answer answer, Long questionId, UserInfo userClass);
}
