package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.services.UserService.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService,PasswordEncoder passwordEncoder) {
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
        List<String> splitList = List.of(user.getEmail().split("@"));
        user.setUsername(splitList.get(0));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.createNewUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "Login";
    }

//    @GetMapping("/")
//    public String home(){
//        return "Home";
//    }

}
