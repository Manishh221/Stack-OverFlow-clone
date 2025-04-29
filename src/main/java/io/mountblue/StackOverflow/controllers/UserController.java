package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.services.UserService.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;

public class UserController {
    private UserService userService;
    private BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService,BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder=passwordEncoder;
    }

    @GetMapping("/signup")
    public String showSignup(Model model){
        model.addAttribute("user",new Users());
        return "Signup";
    }

    @PostMapping("/adduser")
    public String addUser(@ModelAttribute("user") Users user){
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        List<String> splitList = List.of(user.getEmail().split("@"));
        user.setUserName(splitList.get(0));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.createNewUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "Login";
    }

}
