package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> , JpaSpecificationExecutor<Question> {
    @Query("SELECT DISTINCT q FROM Question q " +
            "JOIN q.questionTags qt " +
            "JOIN qt.tag t " +
            "WHERE t IN :tags AND q.id <> :questionId " +
            "ORDER BY q.createdAt DESC")
    List<Question> findRelatedQuestionsByTags(@Param("tags") Set<Tag> tags,
                                              @Param("questionId") Long questionId);

}