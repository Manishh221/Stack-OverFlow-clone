package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.dto.QuestionResponseDto;
import io.mountblue.StackOverflow.entity.*;
import io.mountblue.StackOverflow.repositories.QuestionRepository;
import io.mountblue.StackOverflow.repositories.QuestionTagRepository;
import io.mountblue.StackOverflow.repositories.TagRepository;
import io.mountblue.StackOverflow.repositories.UserTagsRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuestionServiceImpl implements QuestionService{

    private TagRepository tagRepository;
    private QuestionRepository questionRepository;
    private QuestionTagRepository questionTagRepository;
    private UserTagsRepository userTagsRepository;

   @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository,
                               TagRepository tagRepository, QuestionTagRepository questionTagRepository, UserTagsRepository userTagsRepository) {
        this.questionRepository = questionRepository;
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
        this.userTagsRepository = userTagsRepository;
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

           UserTags userTags = new UserTags();
           userTags.setUser(user);
           userTags.setTag(tag);
           userTagsRepository.save(userTags);
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
public Page<QuestionResponseDto> findAllQuestions(int pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("updatedAt").descending());
    Page<Question> questionPage = questionRepository.findAll(pageable);

    return questionPage.map(this::getAllQUestionData);
}

    public QuestionResponseDto getAllQUestionData(Question question) {
        List<QuestionTag> questionTag = questionTagRepository.findByQuestionId(question.getId());
        List<String> tags = new ArrayList<>();
        for (QuestionTag questionTag1 : questionTag) {
            tags.add(questionTag1.getTag().getTagName());
        }
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(question.getUpdatedAt(), now);
        long minutes = duration.toMinutes();
        String timeAgo;

        if (minutes < 1) {
            timeAgo = "just now";
        } else if (minutes < 60) {
            timeAgo = minutes + " minute(s) ago";
        } else if (minutes < 1440) {
            timeAgo = (minutes / 60) + " hour(s) ago";
        } else {
            timeAgo = (minutes / 1440) + " day(s) ago";
        }
        QuestionResponseDto questionResponseDto = new QuestionResponseDto();
        questionResponseDto.setTitle(question.getTitle());
        questionResponseDto.setId(question.getId());
        questionResponseDto.setDescription(question.getDescription());
        questionResponseDto.setVotes(question.getUpvote() - question.getDownvote());
        questionResponseDto.setAuthor(question.getUser().getUsername());
        questionResponseDto.setTags(tags);
        questionResponseDto.setAnswers(question.getAnswerList().size());
        questionResponseDto.setTimeAgo(timeAgo);

        return questionResponseDto;
    }

//    public List<Question> getRelatedQuestions(Long questionId) {
//        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
//        if (optionalQuestion.isEmpty()) {
//            throw new RuntimeException("Question not found");
//        }
//
//        Question question = optionalQuestion.get();
////        Set<Tag> tags = question.getTags();
//
//        return questionRepository.findRelatedQuestionsByTags(tags, questionId);
//    }


}


