package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.AnswerRepository;
import io.mountblue.StackOverflow.repositories.QuestionRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void saveAnswer(Answer answer, Long questionId, UserInfo userClass){
        try {
            answer.setUser(userClass.getUser());
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + questionId));
            answer.setQuestion(question);
            answerRepository.save(answer);
        }catch (Exception e){
            throw new RuntimeException("Error saving answer: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAnswer(Long answerId){
        try {
            answerRepository.deleteById(answerId);
        }catch (Exception e){
            throw new RuntimeException("Error deleting answer: " + e.getMessage(), e);
        }
    }

    @Override
    public Answer findAnswerById(Long answerId){
        try {
            return answerRepository.findById(answerId)
                    .orElseThrow(() -> new IllegalArgumentException("Answer not found with id: " + answerId));
        }catch (Exception e){
            throw new RuntimeException("Error finding answer: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateAnswer(Answer answer){
        try {
            answerRepository.save(answer);
        }catch (Exception e){
            throw new RuntimeException("Error updating answer: " + e.getMessage(), e);
        }
    }
}
