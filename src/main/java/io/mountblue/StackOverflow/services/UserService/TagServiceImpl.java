package io.mountblue.StackOverflow.services.UserService;

import io.mountblue.StackOverflow.entity.QuestionTag;
import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.repositories.QuestionTagRepository;
import io.mountblue.StackOverflow.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService{

   private TagRepository tagRepository;
   private QuestionTagRepository questionTagRepository;

   @Autowired
    public TagServiceImpl(TagRepository tagRepository, QuestionTagRepository questionTagRepository) {
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
    }

    @Override
    public List<Tag> findAllTags() {

        return tagRepository.findAll();
    }

    @Override
    public List<Tag> findAllTagsByQuestionId(Long questionId) {
       List<QuestionTag> questionTags = questionTagRepository.findByQuestionId(questionId);
       List<Tag> tags = new ArrayList<>();

       for (var theQuestionTag : questionTags) {
           tags.add(theQuestionTag.getTag());
       }

       return tags;
    }
}
