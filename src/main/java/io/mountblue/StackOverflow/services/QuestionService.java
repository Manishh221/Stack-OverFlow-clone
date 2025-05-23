package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.controllers.QuestionController;
import io.mountblue.StackOverflow.dto.QuestionResponseDto;
import io.mountblue.StackOverflow.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {

    Question createNewQuestion(Question question, List<String> tags);
    void deleteQuestionById(Long id);
    Question updateQuestion (Question question, List<String> tagName);
    Question findQuestionById (Long id);
    Page<QuestionResponseDto> findAllQuestions(int pageNumber);
    QuestionResponseDto getAllQUestionData(Question question);
    Page<Question> advancedSearch(
            String tag,
            String user,
            String title,
            boolean accepted,
            boolean unanswered,
            boolean noAcceptedAnswer,
            boolean noAnswer,
            Integer daysOld,
            List<String> tags, // multiple tags for OR filter
            Pageable pageable
    );
    Page<Question> searchQuestions(String tag, String user, String title, boolean accepted, boolean unanswered, Pageable pageable);

    List<Question> getRelatedQuestions(Long questionId);
}
