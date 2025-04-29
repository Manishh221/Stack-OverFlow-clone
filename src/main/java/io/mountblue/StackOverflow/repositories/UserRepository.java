package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<Users, Long>{
//    to find by email
    Users findByEmail(String email);

//    find user on login
    Users findByUsername(String username);
}
