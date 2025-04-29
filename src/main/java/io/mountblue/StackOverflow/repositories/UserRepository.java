package io.mountblue.StackOverflow.repositories;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository  extends JpaRepository<User, Integer>{

//    find user by email/username.
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Users findByUsername(@Param("username") String username);

}
