package io.mountblue.StackOverflow.services.UserService;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.QuestionTag;
import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.QuestionRepository;
import io.mountblue.StackOverflow.repositories.QuestionTagRepository;
import io.mountblue.StackOverflow.repositories.TagRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class QuestionServiceImpl implements QuestionService{

    private TagRepository tagRepository;
    private QuestionRepository questionRepository;
    private QuestionTagRepository questionTagRepository;


   @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository,
                               TagRepository tagRepository, QuestionTagRepository questionTagRepository) {
        this.questionRepository = questionRepository;
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
    }

//  --------------  delete the question--------------------------------------
    @Override
    public void deleteQuestionById(Long id) {
        questionRepository.deleteById(id);
    }

//    ---------------------get question by id------------------------------------
    @Override
    public Question findQuestionById(Long id) {
        return questionRepository.findById(id).get();
    }

    //  ---------------  create new  Question--------------------------------------
    @Override
    public Question createNewQuestion(Question theQuestion, List<String> tagNames) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo=(UserInfo) authentication.getPrincipal();
        Users user=userInfo.getUser();
        theQuestion.setUser(user);
       Question savedQuestion = questionRepository.save(theQuestion);

        Set<QuestionTag> questionTagSet = new HashSet<>();

       for (String tagName: tagNames){
           Tag tag = tagRepository.findByTagName(tagName.trim())
                   .orElseGet(() -> {
                       Tag newTag = new Tag();
                       newTag.setTagName(tagName.trim());
                       newTag.setCreatedAt(LocalDateTime.now());
                       return tagRepository.save(newTag);
                   });

           QuestionTag questionTag = new QuestionTag();
           questionTag.setQuestion(savedQuestion);
           questionTag.setTag(tag);
           questionTag.setCreatedAt(LocalDateTime.now());

           questionTagSet.add(questionTag);
       }

        questionTagRepository.saveAll(questionTagSet);
        savedQuestion.setQuestionTags(questionTagSet);

        return questionRepository.save(savedQuestion);
    }

// -------------------------update existing question:---------------------------------------
    @Override
    public Question updateQuestion(Question theQuestion, List<String> tagNames) {
            Question savedQuestion = questionRepository.save(theQuestion);

            questionTagRepository.deleteTagsByQuestionId(savedQuestion.getId());

           Set<QuestionTag> questionTagSet = new HashSet<>();

           for (String tagName: tagNames){
               Tag tag = tagRepository.findByTagName(tagName.trim())
                       .orElseGet(() -> {
                           Tag newTag = new Tag();
                           newTag.setTagName(tagName.trim());
                           newTag.setCreatedAt(LocalDateTime.now());
                           return tagRepository.save(newTag);
                       });

               QuestionTag questionTag = new QuestionTag();
               questionTag.setQuestion(savedQuestion);
               questionTag.setTag(tag);
               questionTag.setCreatedAt(LocalDateTime.now());

               questionTagSet.add(questionTag);
           }

           questionTagRepository.saveAll(questionTagSet);
           savedQuestion.setQuestionTags(questionTagSet);

           return questionRepository.save(savedQuestion);
       }

//--------------------------find all the questions by page number------------------------------------------
    @Override
    public Page<Question> findAllQuestions(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("updatedAt").descending());

        return questionRepository.findAll(pageable);
    }
}


