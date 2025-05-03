package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.entity.UserTags;
import org.springframework.core.metrics.StartupStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTagsRepository extends JpaRepository<UserTags,Long> {

    @Query("SELECT ut.tag FROM UserTags ut WHERE ut.user.id = :id")
    List<Tag> findAllTagsByUserId(@Param("id") Long id);
}
