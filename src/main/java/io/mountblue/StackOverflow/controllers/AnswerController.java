package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.services.UserService.AnswerService;
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

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
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
            return "redirect:/question/" + questionId;
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
}
