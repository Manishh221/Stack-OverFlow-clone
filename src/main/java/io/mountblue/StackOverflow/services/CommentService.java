package io.mountblue.StackOverflow.services;


import io.mountblue.StackOverflow.entity.Comment;

public interface CommentService {
    void createComment(Comment comment);

    void deleteComment(Long id);

    void updateComment(Long id,String comment);

    Comment getComment(Long id);
}
