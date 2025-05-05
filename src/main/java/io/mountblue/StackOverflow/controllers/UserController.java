package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.dto.QuestionResponseDto;
import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.entity.UserTags;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.UserTagsRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.services.*;
import io.mountblue.StackOverflow.services.QuestionService;
import io.mountblue.StackOverflow.services.UserService;
import org.apache.catalina.User;
import org.springframework.core.metrics.StartupStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private QuestionService questionService;
    private UserTagsRepository userTagsRepository;
    private CloudinaryService cloudinaryService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder,
                          QuestionService questionService, UserTagsRepository userTagsRepository, CloudinaryService cloudinaryService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.questionService = questionService;
        this.userTagsRepository = userTagsRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/signup")
    public String showSignup(Model model) {
        model.addAttribute("user", new Users());
        return "Signup";
    }

    @PostMapping("/adduser")
    public String addUser(@ModelAttribute("user") Users user){
        user.setCreatedAt(LocalDateTime.now());
        List<String> splitList = List.of(user.getEmail().split("@"));
        user.setUsername(splitList.get(0));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.createNewUser(user);
        return "redirect:/login";
    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute("user") Users user,
                             @RequestParam("profileImage") MultipartFile profileImage,
                             @AuthenticationPrincipal UserInfo userInfo) {
        if (userInfo == null) {
            return "/login";
        }
        Users existingUser = userService.findUser(user.getId());
            if (userInfo.getUser().getRole().equals("ADMIN") || existingUser.getEmail().equals(userInfo.getUser().getEmail())) {
                if (profileImage != null && !profileImage.isEmpty()) {
                    try {
                        String imageUrl = cloudinaryService.uploadFile(profileImage, "stackoverflow");
                        user.setAvatar(imageUrl);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload image", e);
                    }
                } else {
                    user.setAvatar(userInfo.getUser().getAvatar());
                }
                user.setUpdatedAt(LocalDateTime.now());
                System.out.println("user about : "+ user.getAbout());
                userService.createNewUser(user);
            }
        return "redirect:/user/" + user.getId() + "?profiletab=profile";
    }


    @PostMapping("/deleteUser/{id}")
        public String deleteUser(@PathVariable Long id){
            userService.deleteUser(id);
            return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "Login";
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserInfo userInfo, Model model) {
        Page<QuestionResponseDto> questions = questionService.findAllQuestions(0);
        List<QuestionResponseDto> questionList = questions.getContent();

        System.out.println("The questions are: " + questionList);

        if (!questionList.isEmpty()) {
            System.out.println("The first question's vote count is: " + questionList.get(0).getVotes());
        }

        if (userInfo != null && userInfo.getUser() != null) {
            model.addAttribute("user", userInfo.getUser());
        }

        model.addAttribute("questions", questions);

        return "Home";
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable Long id, Model model,@RequestParam(value = "profiletab") String profileTab,
                          @RequestParam(value = "activitytab",defaultValue = "question") String activityTab,
                          @RequestParam(value = "settingtab",defaultValue = "editProfile") String settingTab) {
        Users user = userService.findUser(id);
        if (user.getAbout().equals("<p><br></p>")) {
            user.setAbout("");
        }

        List<Tag> userAllTags = userTagsRepository.findAllTagsByUserId(id);
        System.out.println("all users tags are: " + userAllTags);

        model.addAttribute("user", user);
        model.addAttribute("profiletab",profileTab);
        model.addAttribute("activitytab",activityTab);
        model.addAttribute("settingtab",settingTab);
        return "UserProfile";
    }

    @GetMapping("/users")
    public String getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "reputation") String sort,
            Model model) {

        Sort sorting = Sort.by(Sort.Direction.DESC, sort.equals("new") ? "createdAt" : "reputation");
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Users> usersPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            usersPage = userService.searchUsers(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            usersPage = userService.findPaginatedUsers(pageable);
        }

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("sort", sort);

        return "UsersList";
    }





}
