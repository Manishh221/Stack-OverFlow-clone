package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.constants.Reputations;
import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.entity.Comment;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.exceptions.InsufficientReputationException;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.services.AnswerService;
import io.mountblue.StackOverflow.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/answers/{answerId}")
public class CommentController {
    private AnswerService answerService;
    private CommentService commentService;

    @Autowired
    public CommentController(AnswerService answerService, CommentService commentService) {
        this.answerService = answerService;
        this.commentService = commentService;
    }

    @PostMapping("/comment/create")
    public String addComment(@PathVariable Long answerId,
                             @RequestParam("commentText") String commentText,
                             @AuthenticationPrincipal UserInfo userClass){
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation()< Reputations.COMMENT_EVERYWHERE){
                throw new InsufficientReputationException("You need at least 30 reputation to answer.");
            }
        }
        Answer answer = answerService.findAnswerById(answerId);
        Long questionId = answer.getQuestion().getId();
        Comment comment = new Comment();
        Users user = userClass.getUser();
        comment.setName(user.getUsername());
        comment.setEmail(user.getEmail());
        comment.setContent(commentText);
        comment.setAnswer(answer);
        commentService.createComment(comment);
        return "redirect:/question/" + questionId;
    }

    @PostMapping("/comment/{commentId}/delete")
    public String delComment(@PathVariable Long answerId,@PathVariable Long commentId,
                            @AuthenticationPrincipal UserInfo userClass){
        Comment comment = commentService.getComment(commentId);
        Answer answer = answerService.findAnswerById(answerId);
        Long questionId = answer.getQuestion().getId();
         Users user = userClass.getUser();
            if(user.getEmail().equals(comment.getEmail()) ||
                    (user.getRole().equals("ADMIN"))
            ){
                commentService.deleteComment(commentId);
            }
            return "redirect:/question/" + questionId;
    }

    @PostMapping("/comment/{commentId}/update")
    public String updateComment(@PathVariable Long answerId,
                                @PathVariable Long commentId,
                                @RequestParam("updatedComment") String commentText,
                                @AuthenticationPrincipal UserInfo userClass){
        Comment comment = commentService.getComment(commentId);
        Answer answer = answerService.findAnswerById(answerId);
        Long questionId = answer.getQuestion().getId();
        Users user = userClass.getUser();
        if(user.getEmail().equals(comment.getEmail()) ||
                (user.getRole().equals("ADMIN"))
        ){
             commentService.updateComment(commentId,commentText);
        }

        return "redirect:/question/" + questionId;
    }
}
