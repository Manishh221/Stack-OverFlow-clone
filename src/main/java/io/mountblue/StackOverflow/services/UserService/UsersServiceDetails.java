package io.mountblue.StackOverflow.services.UserService;

import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.UserRepository;
import io.mountblue.StackOverflow.security.UserInfo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UsersServiceDetails implements UserDetailsService {

    private final UserRepository userRepository;

    public UsersServiceDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Email is: " + email);

        Users theUser = userRepository.findByEmail(email);
        if(theUser == null){
            throw  new UsernameNotFoundException("user not found");
        }

        return new UserInfo(theUser);
    }
}

