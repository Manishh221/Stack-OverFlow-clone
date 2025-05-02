package io.mountblue.StackOverflow.repositories;

import io.mountblue.StackOverflow.dto.TagWithCountDTO;
import io.mountblue.StackOverflow.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTagName(String tagName);

    @Query("SELECT new io.mountblue.StackOverflow.dto.TagWithCountDTO(t.tagId, t.tagName, COUNT(qt), t.createdAt) " +
            "FROM Tag t LEFT JOIN QuestionTag qt ON t.tagId = qt.tag.tagId " +
            "WHERE LOWER(t.tagName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "GROUP BY t.tagId, t.tagName, t.createdAt")
    Page<TagWithCountDTO> searchTagWithQuestionCount(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new io.mountblue.StackOverflow.dto.TagWithCountDTO(t.tagId, t.tagName, COUNT(qt), t.createdAt) " +
            "FROM Tag t LEFT JOIN QuestionTag qt ON t.tagId = qt.tag.tagId " +
            "WHERE LOWER(t.tagName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "GROUP BY t.tagId, t.tagName, t.createdAt " +
            "ORDER BY COUNT(qt) DESC")
    Page<TagWithCountDTO> searchTagWithQuestionCountSorted(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT new io.mountblue.StackOverflow.dto.TagWithCountDTO(t.tagId, t.tagName, COUNT(qt), t.createdAt) " +
            "FROM Tag t LEFT JOIN QuestionTag qt ON t.tagId = qt.tag.tagId " +
            "GROUP BY t.tagId, t.tagName, t.createdAt " +
            "ORDER BY COUNT(qt) DESC")
    Page<TagWithCountDTO> findAllTagsWithQuestionCount(Pageable pageable);
}
