package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {

    List<QuestionTag> findByQuestionId(Long id);

    @Query("DELETE FROM QuestionTag qt WHERE qt.question.id = :questionId")
    void deleteTagsByQuestionId(@Param("questionId") Long questionId);

    @Modifying
    @Query("delete from QuestionTag qt where qt.id = :id")
    void deleteByQuestionId(@Param("id") Long id);

    List<QuestionTag> findByQuestion(Question savedPost);
}
