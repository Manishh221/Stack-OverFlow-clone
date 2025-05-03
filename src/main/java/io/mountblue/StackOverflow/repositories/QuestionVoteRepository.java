package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.QuestionVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionVoteRepository extends JpaRepository<QuestionVote,Long> {
    Optional<QuestionVote> findByUserIdAndQuestionId(Long userId, Long questionId);

}
