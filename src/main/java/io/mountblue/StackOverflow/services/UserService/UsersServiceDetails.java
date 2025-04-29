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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Username is: " + username);

        Users theUser = userRepository.findByUsername(username);
        if (Objects.isNull(theUser)) {
            throw new UsernameNotFoundException("User not Found:");
        }

        return new UserInfo(theUser);
    }
}

