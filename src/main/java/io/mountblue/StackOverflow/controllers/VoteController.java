package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.repositories.AnswerVoteRepository;
import io.mountblue.StackOverflow.repositories.QuestionVoteRepository;
import io.mountblue.StackOverflow.services.VoteServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class VoteController {

    private VoteServices voteServices;
    private QuestionVoteRepository questionVoteRepository;
    private AnswerVoteRepository answerVoteRepository;

    @Autowired
    public VoteController(VoteServices voteServices,QuestionVoteRepository questionVoteRepository,AnswerVoteRepository answerVoteRepository) {
        this.voteServices = voteServices;
        this.questionVoteRepository = questionVoteRepository;
        this.answerVoteRepository = answerVoteRepository;
    }

    @PostMapping("/upvote/{id}")
    public String upvoteQuestion(@PathVariable("id") String id, Model model) {
//        voteServices.
//        model.addAttribute("upvotedUser",)
        return "";
    }
}
