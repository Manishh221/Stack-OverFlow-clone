package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.constants.Reputations;
import io.mountblue.StackOverflow.controllers.AnswerController;
import io.mountblue.StackOverflow.entity.*;
import io.mountblue.StackOverflow.repositories.*;
import io.mountblue.StackOverflow.security.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteServices {
    private QuestionVoteRepository questionVoteRepository;
    private AnswerVoteRepository answerVoteRepository;
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;
    private UserRepository userRepository;

    @Autowired
    public VoteServiceImpl(QuestionVoteRepository questionVoteRepository, AnswerVoteRepository answerVoteRepository, QuestionRepository questionRepository,AnswerRepository answerRepository,UserRepository userRepository) {
        this.questionVoteRepository = questionVoteRepository;
        this.answerVoteRepository = answerVoteRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void downvoteQuestion(Long questionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userDetails = (UserInfo) auth.getPrincipal();
        Users voter = userDetails.getUser();

        if (voter.getReputation() > Reputations.DOWNVOTE_EVERYWHERE) {
            Question question = questionRepository.findById(questionId).orElseThrow();
            Users questionOwner = question.getUser();

            Optional<QuestionVote> existingVoteOpt = questionVoteRepository.findByUserAndQuestion(voter, question);

            if (existingVoteOpt.isPresent()) {
                QuestionVote existingVote = existingVoteOpt.get();
                if (existingVote.isDownvote()) {
                    questionVoteRepository.delete(existingVote);
                    questionOwner.setReputation(questionOwner.getReputation() + Reputations.QUESTION_REPUTATION_DOWNVOTE);
                    userRepository.save(questionOwner);
                    return;
                }
                if (existingVote.isUpvote()) {
                    existingVote.setUpvote(false);
                    existingVote.setDownvote(true);
                    questionVoteRepository.save(existingVote);
                    questionOwner.setReputation(questionOwner.getReputation() + 2 * Reputations.QUESTION_REPUTATION_UPVOTE);
                    userRepository.save(questionOwner);
                    return;
                }
            }
            QuestionVote vote = new QuestionVote();
            vote.setUser(voter);
            vote.setQuestion(question);
            vote.setUpvote(false);
            vote.setDownvote(true);
            questionVoteRepository.save(vote);

            questionOwner.setReputation(questionOwner.getReputation() - Reputations.QUESTION_REPUTATION_DOWNVOTE);
            userRepository.save(questionOwner);

        }
    }


    @Override
    public void upvoteQuestion(Long questionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userDetails = (UserInfo) auth.getPrincipal();
        Users voter = userDetails.getUser();

        if (voter.getReputation() > Reputations.UPVOTE_EVERYWHERE) {
            Question question = questionRepository.findById(questionId).orElseThrow();
            Users questionOwner = question.getUser();

            Optional<QuestionVote> existingVoteOpt = questionVoteRepository.findByUserAndQuestion(voter, question);

            if (existingVoteOpt.isPresent()) {
                QuestionVote existingVote = existingVoteOpt.get();

                if (existingVote.isUpvote()) {
                    questionVoteRepository.delete(existingVote);
                    questionOwner.setReputation(questionOwner.getReputation() - Reputations.QUESTION_REPUTATION_UPVOTE);
                    userRepository.save(questionOwner);
                    return;
                }

                if (existingVote.isDownvote()) {
                    existingVote.setDownvote(false);
                    existingVote.setUpvote(true);
                    questionVoteRepository.save(existingVote);

                    questionOwner.setReputation(questionOwner.getReputation() + 2 * Reputations.QUESTION_REPUTATION_UPVOTE);
                    userRepository.save(questionOwner);
                    return;
                }
            }

            QuestionVote vote = new QuestionVote();
            vote.setUser(voter);
            vote.setQuestion(question);
            vote.setUpvote(true);
            vote.setDownvote(false);
            questionVoteRepository.save(vote);

            questionOwner.setReputation(questionOwner.getReputation() + Reputations.QUESTION_REPUTATION_UPVOTE);
            userRepository.save(questionOwner);
        }
    }


    @Override
    public void downvoteAnswer(Long answerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userDetails = (UserInfo) auth.getPrincipal();
        Users voter = userDetails.getUser();

        if (voter.getReputation() > Reputations.DOWNVOTE_EVERYWHERE) {
            Answer answer = answerRepository.findById(answerId).orElseThrow();
            Users answerAuthor = answer.getUser();

            Optional<AnswerVote> existingVoteOpt = answerVoteRepository.findByUserAndAnswer(voter, answer);

            if (existingVoteOpt.isPresent()) {
                AnswerVote existingVote = existingVoteOpt.get();

                if (existingVote.isDownvote()) {
                    answerVoteRepository.delete(existingVote);
                    answerAuthor.setReputation(answerAuthor.getReputation() + Reputations.ANSWER_REPUTATION_DOWNVOTE);
                    userRepository.save(answerAuthor);
                    return;
                }

                if (existingVote.isUpvote()) {
                    existingVote.setUpvote(false);
                    existingVote.setDownvote(true);
                    answerVoteRepository.save(existingVote);
                    answerAuthor.setReputation(answerAuthor.getReputation() - 2 * Reputations.ANSWER_REPUTATION_UPVOTE);
                    userRepository.save(answerAuthor);
                    return;
                }
            }
            AnswerVote vote = new AnswerVote();
            vote.setAnswer(answer);
            vote.setUser(voter);
            vote.setUpvote(false);
            vote.setDownvote(true);
            answerVoteRepository.save(vote);

            answerAuthor.setReputation(answerAuthor.getReputation() - Reputations.ANSWER_REPUTATION_DOWNVOTE);
            userRepository.save(answerAuthor);
        }
    }


    @Override
    public void upvoteAnswer(Long answerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userDetails = (UserInfo) auth.getPrincipal();
        Users voter = userDetails.getUser();

        if (voter.getReputation() > Reputations.UPVOTE_EVERYWHERE) {
            Answer answer = answerRepository.findById(answerId).orElseThrow();
            Users answerAuthor = answer.getUser();

            Optional<AnswerVote> existingVoteOpt = answerVoteRepository.findByUserAndAnswer(voter, answer);

            if (existingVoteOpt.isPresent()) {
                AnswerVote existingVote = existingVoteOpt.get();

                if (existingVote.isUpvote()) {
                    answerVoteRepository.delete(existingVote);
                    answerAuthor.setReputation(answerAuthor.getReputation() - Reputations.ANSWER_REPUTATION_UPVOTE);
                    userRepository.save(answerAuthor);
                    return;
                }

                if (existingVote.isDownvote()) {
                    existingVote.setDownvote(false);
                    existingVote.setUpvote(true);
                    answerVoteRepository.save(existingVote);
                    answerAuthor.setReputation(answerAuthor.getReputation() + 2 * Reputations.ANSWER_REPUTATION_UPVOTE);
                    userRepository.save(answerAuthor);
                    return;
                }
            }

            AnswerVote vote = new AnswerVote();
            vote.setAnswer(answer);
            vote.setUser(voter);
            vote.setUpvote(true);
            vote.setDownvote(false);
            answerVoteRepository.save(vote);

            answerAuthor.setReputation(answerAuthor.getReputation() + Reputations.ANSWER_REPUTATION_UPVOTE);
            userRepository.save(answerAuthor);
        }
    }


}
