package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.QuestionTag;
import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.services.UserService.QuestionService;
import io.mountblue.StackOverflow.services.UserService.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.*;

@Controller
public class QuestionController {

    private QuestionService questionService;
    private TagService tagService;

    private static final List<String> DEMO_TAGS = List.of(
            "java", "javascript", "python", "kotlin",
            "spring", "spring-boot", "hibernate", "jpa",
            "reactjs", "next.js", "tailwind-css",
            "rest", "jwt", "oauth-2.0",
            "mysql", "postgresql",
            "docker", "aws", "git"
    );


    @GetMapping("/ask-question")
    public String askQuestion(Model model) {

        if (!model.containsAttribute("question")) {
            model.addAttribute("question", new Question());
        }

        model.addAttribute("allTags", DEMO_TAGS);
        return "CreateQuestion";
    }

    @PostMapping("/review")
    public String reviewQuestion(
            @Valid @ModelAttribute("question") Question question,  // runs JSR-303 validation
            BindingResult br,
            @RequestParam(value = "tags", required = false) String tagsCsv,
            Model model) {

        // If there are field errors, bounce back to Ask page so the user can fix them
        if (br.hasErrors()) {
            model.addAttribute("allTags", DEMO_TAGS);
            return "CreateQuestion";
        }
        List<String> selectedTags = (tagsCsv == null || tagsCsv.isBlank())
                ? List.of()
                : Arrays.stream(tagsCsv.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        model.addAttribute("selectedTags", selectedTags);
        model.addAttribute("allTags", DEMO_TAGS);
        return "ReviewQuestion";
    }

    @GetMapping("/")
    public String home() {
        return "Home";
    }

    @Autowired
    public QuestionController(QuestionService questionService, TagService tagService) {
        this.questionService = questionService;
        this.tagService = tagService;
    }

    @PostMapping("/create-question")
    public String createQuestion(@ModelAttribute("question")Question question,
                                 @RequestParam(value = "tags", required = false)
                                     List<String> tagNames) {
    questionService.createNewQuestion(question, tagNames);
      return "Home";
    }

    @GetMapping("/update/form/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        Question theQuestion = questionService.findQuestionById(id);

        List<Tag> theTag = tagService.findAllTagsByQuestionId(theQuestion.getId());

        model.addAttribute("question", theQuestion);
        model.addAttribute("allTags", theTag);

        return "/#";
    }

    @PostMapping("/update/Question")
    public String updateQuestion(@ModelAttribute("question") Question question, List<String> tags) {

        return null;
    }

}
