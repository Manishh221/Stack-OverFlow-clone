package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Question;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class QuestionController {

    private QuestionService questionService;
    private TagService tagService;

    private static final List<String> DEMO_TAGS = List.of(
            // languages & runtimes
            "java", "javascript", "python", "kotlin",

            // Java ecosystem
            "spring", "spring-boot", "hibernate", "jpa",

            // web / frontend
            "reactjs", "next.js", "tailwind-css",

            // APIs & auth
            "rest", "jwt", "oauth-2.0",

            // databases
            "mysql", "postgresql",

            // misc
            "docker", "aws", "git"
    );
    /* ---------- STEP-1 : show blank form -------------------------------- */
//    @GetMapping("/ask-question")
//    public String askQuestion(Model model) {
//
//        // put empty Question in session if not already present
//        if (!model.containsAttribute("question")) {
//            model.addAttribute("question", new Question());
//        }
//
//        model.addAttribute("allTags", tagService.findAllTagNames()); // List<String>
//        return "ask-question";
//    }
//
//    /* ---------- STEP-2 : review page ------------------------------------ */
//    @PostMapping("/review")
//    public String reviewQuestion(@Valid @ModelAttribute("question") Question question,
//                                 BindingResult br,
//                                 Model model) {
//
//        model.addAttribute("allTags", tagService.findAllTagNames());
//        return "review-question";     // button “Post” will be disabled if br.hasErrors()
//    }
//
//    /* ---------- STEP-3 : final submit ----------------------------------- */
//    @PostMapping("/questions/submit")
//    public String submitQuestion(@ModelAttribute("question") Question question,
//                                 @RequestParam("tags") List<String> tagNames,
//                                 SessionStatus status) {
//
//        /* 1. Resolve or create Tag entities from tagNames */
//        List<Tag> tags = tagService.resolveTags(tagNames);
//
//        /* 2. Attach to Question via QuestionTag objects (helper in service) */
//        tagService.attachTagsToQuestion(question, tags);
//
//        /* 3. Persist the question (cascade saves QuestionTag entries) */
//        tagService.saveQuestion(question);
//
//        /* 4. clear session-scoped draft */
//        status.setComplete();
//
//        return "redirect:/";          // or redirect to /questions/{id}
//    }

    /* ---------- home page (unchanged) ----------------------------------- */

    @GetMapping("/ask-question")
    public String askQuestion(Model model) {

        if (!model.containsAttribute("question")) {
            model.addAttribute("question", new Question());
        }

        model.addAttribute("allTags", DEMO_TAGS);
        return "CreateQuestion";
    }

    /* ---------- STEP 2 : review page ------------------------------- */
    @PostMapping("/review")
    public String reviewQuestion(@Valid @ModelAttribute("question") Question question,
                                 BindingResult br,
                                 Model model) {

        model.addAttribute("allTags", DEMO_TAGS);
        return "ReviewQuestion";           // Post-button is disabled if br.hasErrors()
    }

    /* ---------- STEP 3 : final submit ------------------------------ */
    @PostMapping("/questions/submit")
    public String submitQuestion(@ModelAttribute("question") Question question,
                                 @RequestParam("tags") List<String> tagNames,
                                 SessionStatus status) {

        /* -----------------------------------------------------------
           TODO: replace the block below with real persistence logic
           once TagService / repositories are wired up
         -----------------------------------------------------------*/
        System.out.println("✓ Question ready to save:");
        System.out.println("  title       = " + question.getTitle());
        System.out.println("  description = " + question.getDescription());
        System.out.println("  tags        = " + tagNames);

        // clear the session-scoped draft
        status.setComplete();

        return "redirect:/";               // or redirect to /questions/{id}
    }

    /* ---------- Home (stub) ---------------------------------------- */

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
                                 @RequestParam("tags") List<String> tags) {
    questionService.createNewQuestion(question, tags);

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
