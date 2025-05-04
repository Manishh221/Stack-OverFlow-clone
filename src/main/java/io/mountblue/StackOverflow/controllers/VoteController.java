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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/upvote-question/{id}")
    public String upvoteQuestion(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserInfo userClass, RedirectAttributes redirectAttributes) {
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation()< Reputations.UPVOTE_EVERYWHERE){
                redirectAttributes.addFlashAttribute("reputationError", "You need at least 15 reputation to upvote.");
                return "redirect:/question/" + id;
            }
        }
        voteServices.upvoteQuestion(id);

        return "redirect:/question/" + id;
    }

    @PostMapping("/downvote-question/{id}")
    public String downvoteQuestion(@PathVariable("id") Long id, Model model,@AuthenticationPrincipal UserInfo userClass ,RedirectAttributes redirectAttributes ) {
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation()< Reputations.UPVOTE_EVERYWHERE){
                redirectAttributes.addFlashAttribute("reputationError", "You need at least 15 reputation to upvote.");
                return "redirect:/question/" + id;
            }
        }
        voteServices.downvoteQuestion(id);
        return "redirect:/question/" + id;
    }

    @PostMapping("/upvote-answer/{id}/{questionId}")
    public String upvoteAnswer(@PathVariable("id") Long id,@PathVariable("questionId")Long questionId, Model model,@AuthenticationPrincipal UserInfo userClass,RedirectAttributes redirectAttributes) {
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation()< Reputations.UPVOTE_EVERYWHERE){
                redirectAttributes.addFlashAttribute("reputationError", "You need at least 15 reputation to upvote.");
                return "redirect:/question/" + questionId;
            }
        }
        voteServices.upvoteAnswer(id);
        return "redirect:/question/" + questionId;
    }

    @PostMapping("/downvote-answer/{id}/{questionId}")
    public String downvoteAnswer(@PathVariable("id") Long id,@PathVariable("questionId")Long questionId, Model model,@AuthenticationPrincipal UserInfo userClass,RedirectAttributes redirectAttributes) {
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation()< Reputations.UPVOTE_EVERYWHERE){
                redirectAttributes.addFlashAttribute("reputationError", "You need at least 15 reputation to upvote.");
                return "redirect:/question/" + questionId;
            }
        }
        voteServices.downvoteAnswer(id);
        return "redirect:/question/" + questionId;
    }
}
