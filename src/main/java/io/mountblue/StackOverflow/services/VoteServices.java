package io.mountblue.StackOverflow.services;

public interface VoteServices {

    void downvoteQuestion(Long questionId);
    void upvoteQuestion(Long questionId);
    void downvoteAnswer(Long answerId);
    void upvoteAnswer(Long answerId);
}
