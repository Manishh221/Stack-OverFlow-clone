package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<Users, Long>{
//    to find by email
    Users findByEmail(String email);

//    find user on login
    Users findByUsername(String username);

    @Query("SELECT u FROM Users u WHERE lower(u.username) LIKE lower(concat('%', :keyword, '%'))")
    Page<Users> searchUsers(@Param("keyword") String keyword, Pageable pageable);

}
