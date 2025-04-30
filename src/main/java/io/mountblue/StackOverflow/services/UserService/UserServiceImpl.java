package io.mountblue.StackOverflow.services.UserService;


import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createNewUser(Users theUser) {
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

}
