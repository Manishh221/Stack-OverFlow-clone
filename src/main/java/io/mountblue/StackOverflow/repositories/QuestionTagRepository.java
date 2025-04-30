package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {
}
