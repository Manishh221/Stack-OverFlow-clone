package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.services.UserService.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class QuestionController {

    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/create/question")
    public String createQuestion(@ModelAttribute("question")Question question,
                             @RequestParam("tag") List<String> tags) {
    questionService.createNewQuestion(question, tags);

      return "Home";
    }

    @GetMapping("/question")
    public String fullQuestion(){
        return "QuestionDetail";
    }
}
