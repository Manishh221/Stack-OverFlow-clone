package io.mountblue.StackOverflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title", nullable = false)
    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9 ?!.]{10,100}$",
            message = "Title must be 10-100 characters and contain only letters, numbers, spaces, ?, !, or ."
    )
    private String title;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "Description cannot be empty")
    @Pattern(
            regexp = "^[\\s\\S]{30,1000}$",
            message = "Description must be 30-1000 characters."
    )
    private String description;

    @Column(name = "upvote")
    private int upvote;

    @Column(name = "downvote")
    private int downvote;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private Users user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answerList;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionTag> questionTags;
}
