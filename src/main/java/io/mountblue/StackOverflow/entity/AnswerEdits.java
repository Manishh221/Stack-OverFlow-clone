package io.mountblue.StackOverflow.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "answeredit")
@Getter
@Setter
public class AnswerEdits {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false)
    private Answer answer;

    @ManyToOne(optional=false)
    private Users user;

    @Column(columnDefinition = "TEXT", nullable=false)
    private String content;

    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
