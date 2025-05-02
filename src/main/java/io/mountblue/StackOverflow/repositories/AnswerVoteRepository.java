package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.AnswerVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerVoteRepository extends JpaRepository<AnswerVote,Long> {
}
