package io.mountblue.StackOverflow.services.UserService;


import io.mountblue.StackOverflow.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    // Register New User
    void createNewUser(Users theUser);

    Users findUser(Long id);

    Page<Users> findPaginatedUsers(Pageable pageable);

    void deleteUser(Long id);

    Users loadUserByEmail(String email);
}
