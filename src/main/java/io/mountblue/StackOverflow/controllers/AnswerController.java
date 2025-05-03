package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.services.AnswerService;
import io.mountblue.StackOverflow.services.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/answer/create/{questionId}")
    public String createAnswer(@PathVariable Long questionId, @Valid @ModelAttribute("answer") Answer answer,
                               BindingResult bindingResult, Model model,
                               @AuthenticationPrincipal UserInfo userClass){
        if (bindingResult.hasErrors()) {
            Question question = questionService.findQuestionById(questionId);
            model.addAttribute("question", question);
            model.addAttribute("answer",answer);
            return "redirect:/question/" + questionId;
        }

//        checking user logged in
        if(userClass==null){
            return "redirect:/login";
        }
        if(userClass.getUser().getReputation() <= 1){
            return  "redirect:/question/" + questionId;
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

    @PostMapping("answer/update/{answerId}")
    public String updateAnswer(@PathVariable Long answerId,
                               @RequestParam("updatedContent") String updatedContent,
                               @AuthenticationPrincipal UserInfo userClass,
                               Model model) {

        if (userClass == null) {
            return "redirect:/login";
        }

        Answer existedAnswer = answerService.findAnswerById(answerId);
        if (existedAnswer == null) {
            model.addAttribute("errorMessage", "Answer not found.");
            return "error";
        }


        if (userClass.getUser().getReputation() <=1) {
            return "redirect:/login";
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
