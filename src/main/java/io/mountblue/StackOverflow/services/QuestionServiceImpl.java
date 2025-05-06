package io.mountblue.StackOverflow.services;

import io.mountblue.StackOverflow.dto.QuestionResponseDto;
import io.mountblue.StackOverflow.entity.*;
import io.mountblue.StackOverflow.repositories.QuestionRepository;
import io.mountblue.StackOverflow.repositories.QuestionTagRepository;
import io.mountblue.StackOverflow.repositories.TagRepository;
import io.mountblue.StackOverflow.repositories.UserTagsRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.specification.QuestionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private TagRepository tagRepository;
    private QuestionRepository questionRepository;
    private QuestionTagRepository questionTagRepository;
    private UserTagsRepository userTagsRepository;
    private TagService tagService;
    private QuestionTagServiceimpl questionTagService;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository, QuestionTagServiceimpl questionTagService,
                               TagRepository tagRepository, QuestionTagRepository questionTagRepository, UserTagsRepository userTagsRepository, TagService tagService) {
        this.questionRepository = questionRepository;
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
        this.userTagsRepository = userTagsRepository;
        this.tagService = tagService;
        this.questionTagService = questionTagService;
    }

    //  --------------  delete the question--------------------------------------
    @Override
    public void deleteQuestionById(Long id) {
        questionRepository.deleteById(id);
    }

    //    ---------------------get question by id------------------------------------
    @Override
    public Question findQuestionById(Long id) {
        return questionRepository.findById(id).get();
    }

    //  ---------------  create new  Question--------------------------------------
    @Override
    public Question createNewQuestion(Question theQuestion, List<String> tagNames) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        Users user = userInfo.getUser();
        theQuestion.setUser(user);
        Question savedQuestion = questionRepository.save(theQuestion);

        Set<QuestionTag> questionTagSet = new HashSet<>();

        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByTagName(tagName.trim())
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setTagName(tagName.trim());
                        newTag.setCreatedAt(LocalDateTime.now());
                        return tagRepository.save(newTag);
                    });

            UserTags userTags = new UserTags();
            userTags.setUser(user);
            userTags.setTag(tag);
            userTagsRepository.save(userTags);
            QuestionTag questionTag = new QuestionTag();
            questionTag.setQuestion(savedQuestion);
            questionTag.setTag(tag);
            questionTag.setCreatedAt(LocalDateTime.now());
            questionTagSet.add(questionTag);
        }

        questionTagRepository.saveAll(questionTagSet);
        savedQuestion.setQuestionTags(questionTagSet);

        return questionRepository.save(savedQuestion);
    }

    // -------------------------update existing question:---------------------------------------
    @Override
    @Transactional
    public Question updateQuestion(Question updatedQuestion, List<String> newTagNames) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        Users user = userInfo.getUser();

        Question existingQuestion = questionRepository.findById(updatedQuestion.getId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        existingQuestion.setTitle(updatedQuestion.getTitle());
        existingQuestion.setDescription(updatedQuestion.getDescription());
        existingQuestion.setUpdatedAt(LocalDateTime.now());

        questionRepository.save(existingQuestion);

        List<QuestionTag> currentQuestionTags = questionTagRepository.findByQuestionId(existingQuestion.getId());
        Set<String> currentTagNames = new HashSet<>();
        for (QuestionTag qt : currentQuestionTags) {
            currentTagNames.add(qt.getTag().getTagName().toLowerCase());
        }

        Set<Tag> newTags = new HashSet<>();
        Set<String> newNames = new HashSet<>();
        if (newTagNames != null) {
            for (String tagName : newTagNames) {
                String name = tagName.trim().toLowerCase();
                if (!name.isEmpty()) {
                    Tag tag = tagService.findOrCreateByTagName(name);
                    newTags.add(tag);
                    newNames.add(name);
                }
            }
        }

        for (QuestionTag qt : currentQuestionTags) {
            String currentName = qt.getTag().getTagName().toLowerCase();
            if (!newNames.contains(currentName)) {
                questionTagService.deleteQuestionTag(qt);
            }
        }

        for (Tag tag : newTags) {
            String tagName = tag.getTagName().toLowerCase();
            if (!currentTagNames.contains(tagName)) {
                QuestionTag newQuestionTag = new QuestionTag();
                newQuestionTag.setTag(tag);
                newQuestionTag.setQuestion(existingQuestion); // ✅ Now it's managed
                newQuestionTag.setCreatedAt(LocalDateTime.now());
                questionTagService.saveQuestionTag(newQuestionTag);

                if (!userTagsRepository.existsByUserAndTag(user, tag)) {
                    UserTags userTag = new UserTags();
                    userTag.setUser(user);
                    userTag.setTag(tag);
                    userTagsRepository.save(userTag);
                }
            }
        }

        return existingQuestion;
    }


    //--------------------------find all the questions by page number------------------------------------------
    @Override
    public Page<QuestionResponseDto> findAllQuestions(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("updatedAt").descending());
        Page<Question> questionPage = questionRepository.findAll(pageable);

        return questionPage.map(this::getAllQUestionData);
    }

    public QuestionResponseDto getAllQUestionData(Question question) {
        List<QuestionTag> questionTag = questionTagRepository.findByQuestionId(question.getId());
        List<String> tags = new ArrayList<>();
        for (QuestionTag questionTag1 : questionTag) {
            tags.add(questionTag1.getTag().getTagName());
        }
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(question.getUpdatedAt(), now);
        long minutes = duration.toMinutes();
        String timeAgo;

        if (minutes < 1) {
            timeAgo = "just now";
        } else if (minutes < 60) {
            timeAgo = minutes + " minute(s) ago";
        } else if (minutes < 1440) {
            timeAgo = (minutes / 60) + " hour(s) ago";
        } else {
            timeAgo = (minutes / 1440) + " day(s) ago";
        }
        QuestionResponseDto questionResponseDto = new QuestionResponseDto();
        questionResponseDto.setTitle(question.getTitle());
        questionResponseDto.setId(question.getId());
        questionResponseDto.setDescription(question.getDescription());
        questionResponseDto.setTags(tags);
        questionResponseDto.setVotes(question.getUpvote() - question.getDownvote());
        questionResponseDto.setAuthor(question.getUser().getUsername());
        questionResponseDto.setAnswers(question.getAnswerList().size());
        questionResponseDto.setTimeAgo(timeAgo);

        return questionResponseDto;
    }

    public Page<Question> advancedSearch(
            String tag,
            String user,
            String title,
            boolean accepted,
            boolean unanswered,
            boolean noAcceptedAnswer,
            boolean noAnswer,
            Integer daysOld,
            List<String> tags,
            Pageable pageable
    ) {
        Specification<Question> spec = Specification.where(null);

        if (tag != null && !tag.isBlank()) {
            spec = spec.and(QuestionSpecification.hasTag(tag));
        }

        if (tags != null && !tags.isEmpty()) {
            spec = spec.and(QuestionSpecification.hasAnyTag(tags));
        }

        if (user != null && !user.isBlank()) {
            spec = spec.and(QuestionSpecification.hasUser(user));
        }

        if (title != null && !title.isBlank()) {
            spec = spec.and(QuestionSpecification.hasTitleContaining(title));
        }

        if (accepted) {
            spec = spec.and(QuestionSpecification.hasAcceptedAnswer());
        }

        if (unanswered) {
            spec = spec.and(QuestionSpecification.hasNoAnswers());
        }

        if (noAcceptedAnswer) {
            spec = spec.and(QuestionSpecification.hasNoAcceptedAnswer());
        }
        if (noAnswer) {
            spec = spec.and(QuestionSpecification.hasNoAnswers());
        }

        if (daysOld != null && daysOld > 0) {
            spec = spec.and(QuestionSpecification.isDaysOld(daysOld));
        }

        return questionRepository.findAll(spec, pageable);
    }

    public List<Question> getRelatedQuestions(Long questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isEmpty()) {
            throw new RuntimeException("Question not found");
        }

        Question question = optionalQuestion.get();
        Set<Tag> tags = question.getQuestionTags()
                .stream()
                .map(QuestionTag::getTag)
                .collect(Collectors.toSet());

        if (tags.isEmpty()) return new ArrayList<>();

        if (tags.isEmpty()) return new ArrayList<>();

        return questionRepository.findRelatedQuestionsByTags(tags, questionId);
    }

    @Override
    public Page<Question> searchQuestions(String tag, String user, String title, boolean accepted, boolean unanswered, Pageable pageable) {
        Specification<Question> spec = Specification.where(null);

        if (tag != null) spec = spec.and(QuestionSpecification.hasTag(tag));
        if (user != null) spec = spec.and(QuestionSpecification.hasUser(user));
        if (title != null) spec = spec.and(QuestionSpecification.hasTitleContaining(title));
        if (accepted) spec = spec.and(QuestionSpecification.hasAcceptedAnswer());
        if (unanswered) spec = spec.and(QuestionSpecification.hasNoAnswers());

        return questionRepository.findAll(spec, pageable);
    }


}


