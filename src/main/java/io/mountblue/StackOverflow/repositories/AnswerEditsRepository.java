package io.mountblue.StackOverflow.repositories;
import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.entity.AnswerEdits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerEditsRepository extends JpaRepository<AnswerEdits,Long> {
    List<AnswerEdits> findByAnswerOrderByCreatedAtDesc(Answer answer);
}
