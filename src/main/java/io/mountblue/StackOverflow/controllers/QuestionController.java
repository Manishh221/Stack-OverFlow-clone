package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.services.UserService.QuestionService;
import io.mountblue.StackOverflow.services.UserService.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class QuestionController {

    //-----------------Declaring the fields to call service class methods------------
    private QuestionService questionService;
    private TagService tagService;

//    -----------------injection the bean---------------------------------------------
    @Autowired
    public QuestionController(QuestionService questionService, TagService tagService) {
        this.questionService = questionService;
        this.tagService = tagService;
    }

//    -----------------------get all the questions------------------------------------
    @GetMapping("/Show-all-questions{id}")
    public String showAllQuestions(@PathVariable("id") int pageNumber, Model model) {
        Page<Question> allQuestions = questionService.findAllQuestions(pageNumber);

        model.addAttribute("allQuestions", allQuestions);

        return "Home";
    }

//    -----------------------Storing the question-------------------------------------
    @PostMapping("/create/question")
    public String createQuestion(@ModelAttribute("question")Question question,
                                 @RequestParam("tag") List<String> tags) {
    questionService.createNewQuestion(question, tags);

      return "Home";
    }

//    --------------------update the perticular question by their id-----------
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

        return "/#";
    }

    @GetMapping("/question")
    public String  fullQuestion(){
        return "QuestionDetail";
    }
}
