package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.dto.QuestionResponseDto;
import io.mountblue.StackOverflow.entity.Answer;
import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.QuestionRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.services.QuestionService;
import io.mountblue.StackOverflow.services.TagService;
import io.mountblue.StackOverflow.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class QuestionController {

    private QuestionService questionService;
    private TagService tagService;
    private UserService userService;
    private QuestionRepository questionRepository;

    @Autowired
    public QuestionController(QuestionService questionService, TagService tagService,UserService userService,QuestionRepository questionRepository) {
        this.questionService = questionService;
        this.tagService = tagService;
        this.userService = userService;
        this.questionRepository = questionRepository;
    }

//    --------------------------get all Questions-----------------------------------------------
    @GetMapping("/Show-all-questions/{page-number}")
    public String showAllQuestions (@PathVariable("page-number") int pageNumber, Model model) {
        Page<QuestionResponseDto> allQuestions = questionService.findAllQuestions(pageNumber);

        model.addAttribute("allQuestions", allQuestions);

        return "Home";
    }

//    ---------------------------finding all tags----------------------------------------------
    public List<String> allTags() {
        List<String> tags = new ArrayList<>();

        for (var tag: tagService.findAllTags()) {
            tags.add(tag.getTagName());
        }
        return tags;
    }

//    ----------------ask any Question--------------------------------------------------------
    @GetMapping("/ask-question")
    public String askQuestion(@AuthenticationPrincipal UserInfo userInfo , Model model) {
        if(userInfo== null){
            return "redirect:/login";
        }
        model.addAttribute("user",userInfo.getUser());
        if (!model.containsAttribute("question")) {
            model.addAttribute("question", new Question());
        }

        model.addAttribute("allTags", allTags());
        return "CreateQuestion";
    }

//    ---------------------------review before submitting the question-------------------------
    @PostMapping("/review")
    public String reviewQuestion(
            @Valid @ModelAttribute("question") Question question,  // runs JSR-303 validation
            BindingResult br,
            @RequestParam(value = "tags", required = false) String tagsCsv,
            Model model) {

        if (br.hasErrors()) {
            model.addAttribute("allTags", allTags());
            return "CreateQuestion";
        }
        List<String> selectedTags = (tagsCsv == null || tagsCsv.isBlank())
                ? List.of()
                : Arrays.stream(tagsCsv.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        model.addAttribute("selectedTags", selectedTags);
        model.addAttribute("allTags", allTags());
        return "ReviewQuestion";
    }

//    @GetMapping("/")
//    public String home() {
//        return "Home";
//    }

//    --------------------------storing the Question------------------------------------------
    @PostMapping("/create-question")
    public String createQuestion(@AuthenticationPrincipal UserInfo userInfo ,@Valid @ModelAttribute("question")Question question,
                                 BindingResult br,
                                 @RequestParam(value = "tags", required = false)
                                 String tagsCsv,Model model) {
        if(userInfo==null){
            return "redirect:/login";
        }
        model.addAttribute("user",userInfo.getUser());
        List<String> tagNames = (tagsCsv == null || tagsCsv.isBlank())
                ? List.of()
                : Arrays.stream(tagsCsv.split(",")).map(String::trim).filter(t -> !t.isEmpty()).toList();

        if (br.hasErrors() || tagNames.isEmpty()) {
            model.addAttribute("allTags", allTags());
            if (tagNames.isEmpty()) {
                model.addAttribute("tagsError", "At least one tag is required.");
            }
            return "CreateQuestion";  // same form view
        }

        questionService.createNewQuestion(question, tagNames);
        return "redirect:/";
    }


//    -------------------------update the Question--------------------------------------------
    @GetMapping("/update/form/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        Question theQuestion = questionService.findQuestionById(id);
        List<Tag> theTag = tagService.findAllTagsByQuestionId(theQuestion.getId());

        model.addAttribute("question", theQuestion);
        model.addAttribute("allTags", theTag);

        return "/#";
    }

//    ------------------------storing updated question-----------------------------------------
    @PostMapping("/questions/update/{id}")
    public String updateQuestion(@PathVariable Long id ,@AuthenticationPrincipal UserInfo userInfo ,@ModelAttribute("question") Question question, List<String> tags) {

        Question existingQuestion = questionService.findQuestionById(id);
        if(userInfo.getUser().getEmail().equals(existingQuestion.getUser().getEmail()) || (userInfo.getUser().getRole().equals("ADMIN"))){
            existingQuestion.setTitle(question.getTitle());
            existingQuestion.setDescription(question.getDescription());
            existingQuestion.setUpdatedAt(LocalDateTime.now());
            questionRepository.save(existingQuestion);
        }else{
            return "redirect:/login";
        }

        return "redirect:/question/" + id;
    }

    @GetMapping("/question/{id}")
    public String showQuestion(@PathVariable Long id, Model model,  @RequestParam(value = "updatedUserId", required = false) Long updatedUserId){
        Question question = questionService.findQuestionById(id);
        model.addAttribute("question",question);
        if(updatedUserId!=null){
            Users updatedUser = userService.findUser(updatedUserId);
            model.addAttribute("updatedUser",updatedUser);
        }
        model.addAttribute("answer",new Answer());
        return "QuestionDetail";
    }
}
