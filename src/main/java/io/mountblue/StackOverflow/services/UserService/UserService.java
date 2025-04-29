package io.mountblue.StackOverflow.services.UserService;


import io.mountblue.StackOverflow.entity.Users;
import org.apache.catalina.User;

import java.util.List;

public interface UserService {

    // Register New User
    void createNewUser(Users theUser);

}
