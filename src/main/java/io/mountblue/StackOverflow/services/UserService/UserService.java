package io.mountblue.StackOverflow.services.UserService;


import io.mountblue.StackOverflow.entity.Users;
import org.apache.catalina.User;

import java.util.List;

public interface UserService {

    // Register New User
    Users createNewUser(Users theUser);

//    get user by user id
    Users findUserById(Long userId);

    // Update User Profile
    Users updateUserById(Long userId, Users theUser);

    //  Delete User
    void deleteUserById(Long userId);

    //  Get All Users
    List<Users> findAllUsers();

    //  Search User by Username (Email)
    Users findUserByusername(String email);

}
