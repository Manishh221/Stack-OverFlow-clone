package io.mountblue.StackOverflow.services.UserService;


import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public Users createNewUser(Users theUser) {
     Users newUser = new Users();

        return null;
    }

    @Override
    public Users findUserById(Long userId) {
        return null;
    }

    @Override
    public Users updateUserById(Long userId, Users theUser) {
        return null;
    }

    @Override
    public void deleteUserById(Long userId) {

    }

    @Override
    public List<Users> findAllUsers() {
        return List.of();
    }

    @Override
    public Users findUserByusername(String username) {
        return null;
    }
}
