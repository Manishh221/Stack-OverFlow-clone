package io.mountblue.StackOverflow.services.UserService;


import org.apache.catalina.User;

import java.util.List;

public interface UserService {

    // Register New User
    Users createNewUser(Users theUser);

//    get user by user id
    Users findUserById(int userId);

    // Update User Profile
    Users updateUserById(int userId, Users theUser);

    //  Delete User
    void deleteUserById(int userId);

    //  Get All Users
    List<Users> findAllUsers();

    //  Search User by Username (Email)
    Users findUserByusername(String username);

}
