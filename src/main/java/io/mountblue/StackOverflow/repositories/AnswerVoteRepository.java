package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.entity.AnswerVote;
import io.mountblue.StackOverflow.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerVoteRepository extends JpaRepository<AnswerVote,Long> {

    Optional<AnswerVote> findByUserAndAnswer(Users user, Answer answer);
    int countByAnswerAndUpvoteTrue(Answer answer);
    int countByAnswerAndDownvoteTrue(Answer answer);
}
