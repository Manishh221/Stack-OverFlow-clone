package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Question;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class QuestionController {

    @GetMapping("/ask-question")
    public String question(Model model) {
        List<String> demoTags = List.of(
                /* languages & runtimes */
                "java", "javascript", "typescript", "python", "kotlin", "go", "rust",

                /* Java ecosystem */
                "spring", "spring-boot", "spring-security", "hibernate", "jpa",
                "thymeleaf", "maven", "gradle",

                /* Web / frontend */
                "reactjs", "next.js", "angular", "vue.js", "tailwind-css", "css", "html",

                /* APIs & auth */
                "rest", "jwt", "oauth-2.0", "authentication", "authorization",

                /* Databases */
                "mysql", "postgresql", "mongodb", "sqlite",

                /* Dev-ops & cloud */
                "docker", "kubernetes", "aws", "azure", "gcp",

                /* Misc */
                "git", "github-actions", "linux", "machine-learning", "ai-agents"
        );

        model.addAttribute("allTags", demoTags);
        return "CreateQuestion";
    }

    @PostMapping("/review")
    public String review(@Valid
                         @ModelAttribute("questionForm") Question form,
                         BindingResult br,
                         Model model) {

        model.addAttribute("hasErrors", br.hasErrors());
        return "ReviewForm";
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
