package io.mountblue.StackOverflow.services.UserService;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService{

   private QuestionRepository questionRepository;

   @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question createNewQuestion(Question question, List<String> tags) {
       Question theQuestion = new Question();

       theQuestion.setTitle(question.getTitle());
       theQuestion.setDescription(question.getDescription());

       //Not Completed.

       theQuestion.setUser(question.getUser());

       return questionRepository.save(theQuestion);
    }
}
