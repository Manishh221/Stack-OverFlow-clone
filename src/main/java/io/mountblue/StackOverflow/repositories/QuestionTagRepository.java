package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {

    List<QuestionTag> findByQuestionId(Long id);

}
