package io.mountblue.StackOverflow.services.UserService;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService{

   private QuestionRepository questionRepository;

   @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question createNewQuestion(Question question) {


        return null;
    }
}
