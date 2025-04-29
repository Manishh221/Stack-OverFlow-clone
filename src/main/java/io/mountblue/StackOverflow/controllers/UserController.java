package io.mountblue.StackOverflow.controllers;

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
        List<String> splitList = user.getEmail().split('@');
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
