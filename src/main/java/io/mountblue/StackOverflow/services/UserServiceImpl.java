package io.mountblue.StackOverflow.services;


import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.UserRepository;
import io.mountblue.StackOverflow.utils.GravatarUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final GravatarUtil gravatarUtil;
    private final UserRepository userRepository;

    public UserServiceImpl(GravatarUtil gravatarUtil, UserRepository userRepository) {
        this.gravatarUtil = gravatarUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void createNewUser(Users theUser) {
        if (theUser.getEmail() != null && (theUser.getAvatar() == null || theUser.getAvatar().isEmpty())) {
            String gravatarUrl = gravatarUtil.getGravatar160pxUrl(theUser.getEmail());
            theUser.setAvatar(gravatarUrl);
        }
        userRepository.save(theUser);
    }

    @Override
    public Users findUser(Long id) {
        Optional<Users> user = userRepository.findById(id);
        if(user.isPresent()){
            return user.get();
        }else{
            throw new RuntimeException("User not Found");
        }
    }

    public Users loadUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Page<Users> findPaginatedUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public Page<Users> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable);
    }

}
