package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.entity.AnswerEdits;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.AnswerRepository;
import io.mountblue.StackOverflow.security.UserInfo;

import java.util.List;

public interface AnswerService {
    void saveAnswer(Answer answer, Long questionId, UserInfo userClass);

    void deleteAnswer(Long answerId);

    Answer findAnswerById(Long answerId);

    void updateAnswer(Answer answer);

    List<AnswerEdits> getHistory(Answer answer);

    void recordEdit(Answer answer, Users user, String newContent);
}
