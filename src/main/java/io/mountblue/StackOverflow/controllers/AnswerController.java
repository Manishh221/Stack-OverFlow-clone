package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.services.UserService.AnswerService;
import io.mountblue.StackOverflow.services.UserService.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AnswerController {
    private final AnswerService answerService;
    private final QuestionService questionService;

    public AnswerController(AnswerService answerService, QuestionService questionService) {
        this.answerService = answerService;
        this.questionService = questionService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @PostMapping("/answer/create/{id}")
    public String createAnswer(@PathVariable Long questionId, @Valid @ModelAttribute("answer") Answer answer,
                               BindingResult bindingResult, Model model,
                               @AuthenticationPrincipal UserInfo userClass){
//        checking user logged in
        if(userClass==null){
            return "redirect:/login";
        }
        if(userClass.getUser().getReputation() <= 1){
            return  "redirect/question/" + questionId;
        }
//        checking if answer is empty
        if (bindingResult.hasErrors()) {
            model.addAttribute("answer",answer);
            return "/question/" + questionId;
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

    @PostMapping("/answer/delete/{id}")
    public String deleteAnswer(@PathVariable Long answerId, Model model,
                               @AuthenticationPrincipal UserInfo userClass){
        if(userClass==null){
            return "redirect:/login";
        }
//        getting answer
        Answer answer = answerService.findAnswerById(answerId);

        if((userClass.getUser().getEmail()  != answer.getUser().getEmail()) || !userClass.getUser().getRole().equals("ADMIN")){
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

    @PostMapping("answer/update/{id}")
    public String updateAnswer(@PathVariable Long answerId, @Valid @ModelAttribute("answer") Answer answer,
                               BindingResult bindingResult, Model model,
                               @AuthenticationPrincipal UserInfo userClass){
        if(userClass==null){
            return "redirect:/login";
        }
//        getting answer
        Answer existedAnswer = answerService.findAnswerById(answerId);
//        checking if answer is empty
        if (bindingResult.hasErrors()) {
            model.addAttribute("answer",answer);
            return "/question/" + answer.getQuestion().getId();
        }
//        authorization
        if((userClass.getUser().getEmail()  != existedAnswer.getUser().getEmail()) || !userClass.getUser().getRole().equals("ADMIN")){
            return "redirect:/login";
        }
//        updating  Answer
        try {
            answerService.updateAnswer(answer);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error saving answer: " + e.getMessage());
            return "redirect:/question/" + existedAnswer.getQuestion().getId();
        }

        return "redirect:/question/" + existedAnswer.getQuestion().getId();
    }
}
