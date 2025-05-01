package io.mountblue.StackOverflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @Column(name = "comment", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "answer_id",nullable = false)
    private Answer answer;
}
