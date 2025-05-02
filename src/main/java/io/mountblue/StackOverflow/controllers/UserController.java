package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.dto.QuestionResponseDto;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.security.UserInfo;
import io.mountblue.StackOverflow.services.UserService.QuestionService;
import io.mountblue.StackOverflow.services.UserService.UserService;
import io.mountblue.StackOverflow.services.UserService.UsersServiceDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private QuestionService questionService;

    public UserController(UserService userService,PasswordEncoder passwordEncoder,QuestionService questionService) {
        this.userService = userService;
        this.passwordEncoder=passwordEncoder;
        this.questionService=questionService;
    }

    @GetMapping("/signup")
    public String showSignup(Model model){
        model.addAttribute("user",new Users());
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
        public String updateUser(@ModelAttribute("user") Users user, Principal principal){
            Users existingUser = userService.findUser(user.getId());
            if(existingUser==null){
                throw new RuntimeException("User not found");
            }
        if (principal != null) {
            String email = principal.getName();
            Users loggedInUser = userService.loadUserByEmail(email);
            if((Objects.equals(loggedInUser.getRole(), "ADMIN")) || (Objects.equals(loggedInUser.getEmail(), user.getEmail()))){
                user.setUpdatedAt(LocalDateTime.now());
                userService.createNewUser(user);

            }
        }
        return "redirect:/user/" + user.getId();
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
    public String home(Model model){
        Page<QuestionResponseDto> questions =questionService.findAllQuestions(0);
        System.out.println("the questions is "+questions.getContent()+" THe content is "+questions.getContent().get(0).getVotes());
//        model.addAttribute("user",userInfo.getUser());
//        @AuthenticationPrincipal UserInfo userInfo ,
        model.addAttribute("questions",questions);
//        if(userInfo.getUser() != null){
//
//        }
        return "Home";
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable Long id, Model model,@RequestParam(value = "profiletab") String profileTab,
                          @RequestParam(value = "activitytab",defaultValue = "question") String activityTab) {
        Users user = userService.findUser(id);
        model.addAttribute("user", user);
        model.addAttribute("profiletab",profileTab);
        model.addAttribute("activitytab",activityTab);
        return "UserProfile";
    }

    @GetMapping("/users")
    public String getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reputation"));
        Page<Users> usersPage = userService.findPaginatedUsers(pageable);

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());

        return "UsersList";
    }



}
