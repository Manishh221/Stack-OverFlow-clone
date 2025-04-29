package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Question;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class QuestionController {

    @GetMapping("/ask-question")
    public String question(Model model) {
        return "CreateQuestion";
    }

    @PostMapping("/create/question")
    public String createPost(@ModelAttribute("question")Question question) {

    return "Home";
    }


}
