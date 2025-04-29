package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}
