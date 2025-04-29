package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<Users, Long>{

//    find user by email/username.
    @Query("SELECT u FROM Users u WHERE u.email = :email")
    Users findByUsername(@Param("email") String email);

}
