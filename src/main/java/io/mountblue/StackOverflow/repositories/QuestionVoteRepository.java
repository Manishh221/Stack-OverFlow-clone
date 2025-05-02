package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.QuestionVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionVoteRepository extends JpaRepository<QuestionVote,Long> {
}
