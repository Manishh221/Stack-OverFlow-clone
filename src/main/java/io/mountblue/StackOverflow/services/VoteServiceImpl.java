package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.constants.Reputations;
import io.mountblue.StackOverflow.controllers.AnswerController;
import io.mountblue.StackOverflow.entity.*;
import io.mountblue.StackOverflow.repositories.AnswerRepository;
import io.mountblue.StackOverflow.repositories.AnswerVoteRepository;
import io.mountblue.StackOverflow.repositories.QuestionRepository;
import io.mountblue.StackOverflow.repositories.QuestionVoteRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class VoteServiceImpl implements VoteServices {
    private QuestionVoteRepository questionVoteRepository;
    private AnswerVoteRepository answerVoteRepository;
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;

    @Autowired
    public VoteServiceImpl(QuestionVoteRepository questionVoteRepository, AnswerVoteRepository answerVoteRepository, QuestionRepository questionRepository,AnswerRepository answerRepository) {
        this.questionVoteRepository = questionVoteRepository;
        this.answerVoteRepository = answerVoteRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public void downvoteQuestion(Long questionId) {
        QuestionVote questionVote = new QuestionVote();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userDetails = (UserInfo) auth.getPrincipal();
        Users user = userDetails.getUser();
        if (user.getReputation() > Reputations.DOWNVOTE_EVERYWHERE) {
            Question question = questionRepository.findById(questionId).get();
            Users questionUser = question.getUser();
            questionUser.setReputation(questionUser.getReputation()-Reputations.QUESTION_REPUTATION_DOWNVOTE);
            questionVote.setQuestion(question);
            questionVote.setUser(user);
            questionVote.setDownvote(true);
            questionVoteRepository.save(questionVote);
        }
    }

    @Override
    public void upvoteQuestion(Long questionId) {
        QuestionVote questionVote = new QuestionVote();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userDetails = (UserInfo) auth.getPrincipal();
        Users user = userDetails.getUser();
        if (user.getReputation() > Reputations.UPVOTE_EVERYWHERE) {
            Question question = questionRepository.findById(questionId).get();
            Users questionUser = question.getUser();
            questionUser.setReputation(questionUser.getReputation()+Reputations.QUESTION_REPUTATION_UPVOTE);
            questionVote.setQuestion(question);
            questionVote.setUser(user);
            questionVote.setUpvote(true);
            questionVoteRepository.save(questionVote);
        }
    }

    @Override
    public void downvoteAnswer(Long answerId) {
        AnswerVote answerVote = new AnswerVote();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userDetails = (UserInfo) auth.getPrincipal();
        Users user = userDetails.getUser();
        if (user.getReputation() > Reputations.DOWNVOTE_EVERYWHERE) {
            Answer answer = answerRepository.findById(answerId).get();
            Users answerUser = answer.getUser();
            answerUser.setReputation(answerUser.getReputation()-Reputations.ANSWER_REPUTATION_DOWNVOTE);
            answerVote.setAnswer(answer);
            answerVote.setUser(user);
            answerVote.setDownvote(true);
            answerVoteRepository.save(answerVote);
        }
    }

    @Override
    public void upvoteAnswer(Long answerId) {
        AnswerVote answerVote = new AnswerVote();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userDetails = (UserInfo) auth.getPrincipal();
        Users user = userDetails.getUser();
        if (user.getReputation() > Reputations.UPVOTE_EVERYWHERE) {
            Answer answer = answerRepository.findById(answerId).get();
            Users answerUser = answer.getUser();
            answerUser.setReputation(answerUser.getReputation()+Reputations.ANSWER_REPUTATION_UPVOTE);
            answerVote.setAnswer(answer);
            answerVote.setUser(user);
            answerVote.setUpvote(true);
            answerVoteRepository.save(answerVote);
        }
    }

}
