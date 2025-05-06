package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.constants.Reputations;
import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.exceptions.InsufficientReputationException;
import io.mountblue.StackOverflow.repositories.AnswerRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.services.AnswerService;
import io.mountblue.StackOverflow.services.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class AnswerController {
    private final AnswerService answerService;
    private final QuestionService questionService;
    private final AnswerRepository answerRepository;

    public AnswerController(AnswerService answerService, QuestionService questionService, AnswerRepository answerRepository) {
        this.answerService = answerService;
        this.questionService = questionService;
        this.answerRepository = answerRepository;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @PostMapping("/answer/create/{questionId}")
    public String createAnswer(@PathVariable Long questionId, @Valid @ModelAttribute("answer") Answer answer,
                               BindingResult bindingResult, Model model,
                               @AuthenticationPrincipal UserInfo userClass,
                               RedirectAttributes redirectAttributes){
        //        checking user logged in
        if(userClass==null){
            return "redirect:/login";
        }
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation() < Reputations.ANSWER_EVERYWHERE){
                redirectAttributes.addFlashAttribute("reputationError", "You need at least 50 reputation to answer.");
                return "redirect:/question/" + questionId;
            }
        }
        if (bindingResult.hasErrors()) {
            Question question = questionService.findQuestionById(questionId);
            model.addAttribute("question", question);
            model.addAttribute("answer",answer);
            return "redirect:/question/" + questionId;
        }


//        saving Answer
        try {
            answerService.saveAnswer(answer, questionId, userClass);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error saving answer: " + e.getMessage());
            return "redirect:/question/" + questionId;
        }
//        after saving back to question
        return "redirect:/question/" + questionId;
    }

    @PostMapping("/answer/delete/{answerId}")
    public String deleteAnswer(@PathVariable Long answerId, Model model,
                               @AuthenticationPrincipal UserInfo userClass){
        if(userClass==null){
            return "redirect:/login";
        }
//        getting answer
        Answer answer = answerService.findAnswerById(answerId);
        if (!userClass.getUser().getEmail().equals(answer.getUser().getEmail())
                && !userClass.getUser().getRole().equals("ADMIN")) {
            return "redirect:/login";
        }
//        getting question
        Long questionId = answer.getQuestion().getId();
        try {
            answerService.deleteAnswer(answerId);
        }catch (Exception e){
            model.addAttribute("errorMessage", "Error deleting answer: " + e.getMessage());
            return "redirect:/question/" + questionId;
        }
//        deleting answer
//        redirecting back to question
        return "redirect:/question/" + questionId ;
    }

    @PostMapping("/answer/accept/{answerId}")
    public String acceptAnswer(@PathVariable Long answerId, @AuthenticationPrincipal UserInfo userClass){

        Answer answer = answerService.findAnswerById(answerId);
        if (userClass.getUser().getEmail().equals(answer.getQuestion().getUser().getEmail())){
            if(answer.isAccepted()){
                answer.setAccepted(false);
            }else{
                answer.setAccepted(true);
            }
            answer.setUpdatedAt(LocalDateTime.now());
            answerRepository.save(answer);
            return "redirect:/question/" + answer.getQuestion().getId();
        }
        return "redirect:/login";
      }


    @PostMapping("answer/update/{answerId}")
    public String updateAnswer(@PathVariable Long answerId,
                               @RequestParam("updatedContent") String updatedContent,
                               @AuthenticationPrincipal UserInfo userClass,
                               Model model, RedirectAttributes redirectAttributes) {

        if (userClass == null) {
            return "redirect:/login";
        }
        Answer existedAnswer = answerService.findAnswerById(answerId);
        if(userClass != null){
            Users user = userClass.getUser();
            if(user.getReputation() < Reputations.ANSWER_EVERYWHERE){
                redirectAttributes.addFlashAttribute("reputationError", "You need at least 200 reputation to answer.");
                return "redirect:/question/" + existedAnswer.getQuestion().getId();
            }
        }

        if (existedAnswer == null) {
            model.addAttribute("errorMessage", "Answer not found.");
            return "error";
        }


        // Update content
        existedAnswer.setContent(updatedContent);

        try {
            answerService.updateAnswer(existedAnswer);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error saving answer: " + e.getMessage());
        }

        return "redirect:/question/" + existedAnswer.getQuestion().getId();
    }

}
