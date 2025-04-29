package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/")
    public String getHome(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
//        Page<Question> questions=pageService.getQuestionsListOnBasisOfTags();
//        model.addAttribute("questions", questions);
        return "Home";
    }

    @PostMapping("/create/question")
    public String createPost(@ModelAttribute("question")Question question) {

    return "Home";
    }


}
