package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.QuestionVote;
import io.mountblue.StackOverflow.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionVoteRepository extends JpaRepository<QuestionVote,Long> {
    Optional<QuestionVote> findByUserAndQuestion(Users user, Question question);
    Optional<QuestionVote> findByQuestion(Question question);
    int countByQuestionAndUpvoteTrue(Question question);
    int countByQuestionAndDownvoteTrue(Question question);

}
