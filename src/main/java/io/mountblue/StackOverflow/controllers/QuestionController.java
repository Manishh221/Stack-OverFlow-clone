package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.dto.QuestionResponseDto;
import io.mountblue.StackOverflow.entity.*;
import io.mountblue.StackOverflow.repositories.AnswerVoteRepository;
import io.mountblue.StackOverflow.repositories.QuestionRepository;
import io.mountblue.StackOverflow.repositories.QuestionVoteRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.services.QuestionService;
import io.mountblue.StackOverflow.services.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private QuestionRepository questionRepository;
    private QuestionVoteRepository questionVoteRepository;
    private AnswerVoteRepository answerVoteRepository;

    @Autowired
    public QuestionController(QuestionService questionService, TagService tagService,QuestionRepository questionRepository,QuestionVoteRepository questionVoteRepository,AnswerVoteRepository answerVoteRepository) {
        this.questionService = questionService;
        this.tagService = tagService;
        this.questionRepository = questionRepository;
        this.questionVoteRepository = questionVoteRepository;
        this.answerVoteRepository = answerVoteRepository;
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

@GetMapping("/question")
public String searchQuestionsFromQuery(
        @RequestParam(required = false) String q,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Boolean noAnswers,
        @RequestParam(required = false) Boolean noAccepted,
        @RequestParam(required = false) Integer daysOld,
        @RequestParam(required = false, defaultValue = "newest") String sortBy,
        @RequestParam(required = false) List<String> tags,
        Model model
) {
    String tag = null, user = null, title = null;
    boolean accepted = false, unanswered = false;

    if ((noAccepted == null && noAnswers == null && daysOld == null && (tags == null || tags.isEmpty()))
            && q != null && !q.isBlank()) {

        q = q.toLowerCase();

            if (q.contains("tag:")) {
                tag = extractValue(q, "tag:");
            }
            if (q.contains("user:")) {
                user = extractValue(q, "user:");
            }
            if (q.contains("title:")) {
                title = extractValue(q, "title:");
            }
            accepted = q.contains("is:accepted");
            unanswered = q.contains("answers:0");

        }

    Sort sort = switch (sortBy.toLowerCase()) {
        case "recent" -> Sort.by(Sort.Order.desc("updatedAt"));
        default -> Sort.by(Sort.Order.desc("createdAt"));
    };

    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Question> questionsPage = questionService.advancedSearch(
            tag,
            user,
            title,
            accepted,
            unanswered,
            noAccepted != null && noAccepted,
            noAnswers != null && noAnswers,
            daysOld,
            tags,
            pageable
    );

    Page<QuestionResponseDto> dtoPage = questionsPage.map(questionService::getAllQUestionData);

    model.addAttribute("questionsPage", dtoPage);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", questionsPage.getTotalPages());
    model.addAttribute("q", q);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("noAnswers", noAnswers);
    model.addAttribute("noAccepted", noAccepted);
    model.addAttribute("daysOld", daysOld);
    model.addAttribute("selectedTags", tags);

    return "NewestQuestions";
}

    private String extractValue(String q, String key) {
        int start = q.indexOf(key) + key.length();
        int end = q.indexOf(" ", start);
        if (end == -1) end = q.length();
        String value = q.substring(start, end);
        return value.replaceAll("\"", "").trim();
    }


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
    public String updateQuestion(@AuthenticationPrincipal UserInfo userInfo,@PathVariable Long id,@RequestParam("updatedDescription") String updatedDescription) {

        Question existingQuestion = questionService.findQuestionById(id);
        if(userInfo.getUser().getRole().equals("ADMIN") || (userInfo.getUser().getEmail().equals(existingQuestion.getUser().getEmail())) || (userInfo.getUser().getReputation()>1)){
        existingQuestion.setDescription(updatedDescription);
        existingQuestion.setUpdatedAt(LocalDateTime.now());

        questionRepository.save(existingQuestion);
        }else{
            return "redirect:/login";
        }

        return  "redirect:/question/" + id;
    }

    @PostMapping("/questions/delete/{id}")
    public String deleteQuestion(@AuthenticationPrincipal UserInfo userInfo,@PathVariable Long id) {

        Question existingQuestion = questionService.findQuestionById(id);
        if(userInfo.getUser().getRole().equals("ADMIN") || (userInfo.getUser().getEmail().equals(existingQuestion.getUser().getEmail()))){
            questionService.deleteQuestionById(id);
        }else{
            return "redirect:/login";
        }
        return  "redirect:/";
    }

    @GetMapping("/question/{questionId}")
    public String showQuestion( @PathVariable Long questionId,Model model,@AuthenticationPrincipal UserInfo userInfo){
        Question question = questionService.findQuestionById(questionId);
        Optional<QuestionVote> voteOpt = questionVoteRepository.findByUserAndQuestion(userInfo.getUser(),question);
        boolean questionupvote = false;
        boolean questiondownvote = false;
        boolean answerupvote = false;
        boolean answerdownvote = false;
        if(voteOpt.isPresent()){
            QuestionVote vote = voteOpt.get();
            questionupvote=vote.isUpvote();
            questiondownvote=vote.isDownvote();
        }
        Map<Long, Boolean> answerUpvoteMap = new HashMap<>();
        Map<Long, Boolean> answerDownvoteMap = new HashMap<>();

        for (Answer answer : question.getAnswerList()) {
            Optional<AnswerVote> av = answerVoteRepository.findByUserAndAnswer(userInfo.getUser(), answer);
            answerUpvoteMap.put(answer.getId(), av.isPresent() && av.get().isUpvote());
            answerDownvoteMap.put(answer.getId(), av.isPresent() && av.get().isDownvote());
        }
        int questionUpvotes = questionVoteRepository.countByQuestionAndUpvoteTrue(question);
        int questionDownvotes = questionVoteRepository.countByQuestionAndDownvoteTrue(question);
        Map<Long, Integer> answerVoteCountMap = new HashMap<>();

        for (Answer answer : question.getAnswerList()) {
            Optional<AnswerVote> av = answerVoteRepository.findByUserAndAnswer(userInfo.getUser(), answer);
            answerUpvoteMap.put(answer.getId(), av.isPresent() && av.get().isUpvote());
            answerDownvoteMap.put(answer.getId(), av.isPresent() && av.get().isDownvote());

            int upvotes = answerVoteRepository.countByAnswerAndUpvoteTrue(answer);
            int downvotes = answerVoteRepository.countByAnswerAndDownvoteTrue(answer);
            answerVoteCountMap.put(answer.getId(), upvotes - downvotes);
        }

        model.addAttribute("answerUpvotes", answerUpvoteMap);
        model.addAttribute("answerDownvotes", answerDownvoteMap);
        model.addAttribute("answerVoteCounts", answerVoteCountMap);
        model.addAttribute("questionVoteCount", questionUpvotes - questionDownvotes);
        model.addAttribute("questionupvote",questionupvote);
        model.addAttribute("questiondownvote",questiondownvote);
        model.addAttribute("question",question);
        model.addAttribute("answer",new Answer());
        return "QuestionDetail";
    }


}
