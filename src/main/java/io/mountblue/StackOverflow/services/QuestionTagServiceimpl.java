package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.QuestionTag;
import io.mountblue.StackOverflow.repositories.QuestionTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionTagServiceimpl implements QuestionTagService {
    private QuestionTagRepository questionTagRepository;

    public QuestionTagServiceimpl(QuestionTagRepository questionTagRepository) {
        this.questionTagRepository = questionTagRepository;
    }

    @Override
    public void deleteQuestionTag(QuestionTag questionTag) {
        questionTag.getQuestion().getQuestionTags().remove(questionTag);
        questionTagRepository.deleteByQuestionId(questionTag.getId());
    }

    @Override
    public List<QuestionTag> getTags(Question question){
        return questionTagRepository.findByQuestion(question);
    }

    @Override
    public void saveQuestionTag(QuestionTag questionTag){
        questionTagRepository.save(questionTag);
    }
}
