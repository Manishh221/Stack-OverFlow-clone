package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.constants.Reputations;
import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.QuestionVote;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.exceptions.InsufficientReputationException;
import io.mountblue.StackOverflow.repositories.AnswerVoteRepository;
import io.mountblue.StackOverflow.repositories.QuestionVoteRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.services.VoteServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String upvoteQuestion(@PathVariable("id") Long id, Model model,@AuthenticationPrincipal UserInfo userClass) {
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation()< Reputations.UPVOTE_EVERYWHERE){
                throw new InsufficientReputationException("You need at least 5 reputation to upvote question.");
            }
        }
        voteServices.upvoteQuestion(id);

        return "redirect:/question/" + id;
    }

    public String downvoteQuestion(@PathVariable("id") Long id, Model model,@AuthenticationPrincipal UserInfo userClass) {
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation()< Reputations.UPVOTE_EVERYWHERE){
                throw new InsufficientReputationException("You need at least 5 reputation to downvote question.");
            }
        }
        voteServices.downvoteQuestion(id);
        return "redirect:/question/" + id;
    }

    public String upvoteAnswer(@PathVariable("id") Long id, Model model,@AuthenticationPrincipal UserInfo userClass) {
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation()< Reputations.UPVOTE_EVERYWHERE){
                throw new InsufficientReputationException("You need at least 5 reputation to upvote answer.");
            }
        }
        voteServices.upvoteAnswer(id);
        return "redirect:/answer/" + id;
    }

    public String downvoteAnswer(@PathVariable("id") Long id, Model model,@AuthenticationPrincipal UserInfo userClass) {
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation()< Reputations.UPVOTE_EVERYWHERE){
                throw new InsufficientReputationException("You need at least 5 reputation to downvote answer.");
            }
        }
        voteServices.downvoteAnswer(id);
        return "redirect:/answer/" + id;
    }
}
