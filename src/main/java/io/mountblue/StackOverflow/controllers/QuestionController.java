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
import java.util.stream.Collectors;

@Controller
public class QuestionController {

    private QuestionService questionService;
    private TagService tagService;
    private QuestionRepository questionRepository;
    private QuestionVoteRepository questionVoteRepository;
    private AnswerVoteRepository answerVoteRepository;

    @Autowired
    public QuestionController(QuestionService questionService, TagService tagService, QuestionRepository questionRepository, QuestionVoteRepository questionVoteRepository, AnswerVoteRepository answerVoteRepository) {
        this.questionService = questionService;
        this.tagService = tagService;
        this.questionRepository = questionRepository;
        this.questionVoteRepository = questionVoteRepository;
        this.answerVoteRepository = answerVoteRepository;
    }

    //    --------------------------get all Questions-----------------------------------------------
    @GetMapping("/Show-all-questions/{page-number}")
    public String showAllQuestions(@PathVariable("page-number") int pageNumber, Model model) {

        Page<QuestionResponseDto> allQuestions = questionService.findAllQuestions(pageNumber);

        model.addAttribute("allQuestions", allQuestions);

        return "Home";
    }

    //    ---------------------------finding all tags----------------------------------------------
    public List<String> allTags() {
        List<String> tags = new ArrayList<>();

        for (var tag : tagService.findAllTags()) {
            tags.add(tag.getTagName());
        }
        return tags;
    }

    //    ----------------ask any Question--------------------------------------------------------
    @GetMapping("/ask-question")
    public String askQuestion(@AuthenticationPrincipal UserInfo userInfo, Model model) {
        if (userInfo == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", userInfo.getUser());
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

    @GetMapping("/search")
    public String searchQuestionsFromQuery(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        String tag = null;
        String user = null;
        String title = null;
        boolean accepted = false;
        boolean unanswered = false;

        if (q != null) {
            // Simple pattern-based parsing
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
            if (q.contains("is:accepted")) {
                accepted = true;
            }
            if (q.contains("answers:0")) {
                unanswered = true;
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("upvote")).and(Sort.by(Sort.Order.asc("downvote"))));
        Page<Question> questionsPage = questionService.searchQuestions(tag, user, title, accepted, unanswered, pageable);

        model.addAttribute("questionsPage", questionsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questionsPage.getTotalPages());
        model.addAttribute("q", q); // to keep search bar value

        return "questionList";
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
    public String createQuestion(@AuthenticationPrincipal UserInfo userInfo, @Valid @ModelAttribute("question") Question question,
                                 BindingResult br,
                                 @RequestParam(value = "tags", required = false)
                                 String tagsCsv, Model model) {
        if (userInfo == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", userInfo.getUser());
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
        if (question.getId() != null) {
            questionService.updateQuestion(question, tagNames);
        } else {
            questionService.createNewQuestion(question, tagNames);
        }
        return "redirect:/";
    }

    //    ------------------------storing updated question-----------------------------------------

        @GetMapping("/questions/edit/{id}")
        public String editQuestion(@PathVariable Long id, @AuthenticationPrincipal UserInfo userInfo, Model model) {
            if (userInfo == null) return "redirect:/login";
            Question question = questionService.findQuestionById(id);
            if(question==null){
                return "redirect:/";
            }
            boolean isOwner = userInfo.getUser().getEmail().equals(question.getUser().getEmail());
            boolean isAdmin = userInfo.getUser().getRole().equals("ADMIN");
            if (!(isOwner || isAdmin)) {
                return "redirect:/login";
            }
            model.addAttribute("question", question);
            model.addAttribute("isEdit", true);
            // ✅ 1. Set all tags for the dropdown
            List<String> allTags = tagService.findAllTags()
                    .stream()
                    .map(Tag::getTagName)
                    .collect(Collectors.toList());
            model.addAttribute("allTags", allTags);

            StringBuilder tagStringBuilder = new StringBuilder();
            for (QuestionTag questionTag : question.getQuestionTags()) {
                if (tagStringBuilder.length() > 0) {
                    tagStringBuilder.append(", ");
                }
                tagStringBuilder.append(questionTag.getTag().getTagName());
            }

            // ✅ 2. Set selected tags as comma-separated string (for pre-fill)
            question.setTagString(tagStringBuilder.toString());
            model.addAttribute("tagsCsv", question.getTagStringFull());
            System.out.println("tags string : "+question.getTagStringFull());
            return "CreateQuestion";
        }

//    Delete question

    @PostMapping("/questions/delete/{id}")
    public String deleteQuestion(@AuthenticationPrincipal UserInfo userInfo, @PathVariable Long id) {

        Question existingQuestion = questionService.findQuestionById(id);
        if (userInfo.getUser().getRole().equals("ADMIN") || (userInfo.getUser().getEmail().equals(existingQuestion.getUser().getEmail()))) {
            questionService.deleteQuestionById(id);
        } else {
            return "redirect:/login";
        }
        return "redirect:/";
    }

    @GetMapping("/question/{questionId}")
    public String showQuestion(@PathVariable Long questionId, Model model, @AuthenticationPrincipal UserInfo userInfo) {
        Question question = questionService.findQuestionById(questionId);
        Optional<QuestionVote> voteOpt = questionVoteRepository.findByUserAndQuestion(userInfo.getUser(), question);
        boolean questionupvote = false;
        boolean questiondownvote = false;
        boolean answerupvote = false;
        boolean answerdownvote = false;
        if (voteOpt.isPresent()) {
            QuestionVote vote = voteOpt.get();
            questionupvote = vote.isUpvote();
            questiondownvote = vote.isDownvote();
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
        model.addAttribute("questionupvote", questionupvote);
        model.addAttribute("questiondownvote", questiondownvote);
        model.addAttribute("question", question);
        model.addAttribute("answer", new Answer());
        List<Question> relatedQuestions = questionService.getRelatedQuestions(questionId);
        model.addAttribute("relatedQuestions", relatedQuestions);

        return "QuestionDetail";
    }
}
