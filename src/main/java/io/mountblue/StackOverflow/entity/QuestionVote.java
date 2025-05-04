package io.mountblue.StackOverflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "questionvote")
@Getter
@Setter
public class QuestionVote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "question_id",referencedColumnName = "id")
    private Question question;

    @Column(nullable = false)
    private boolean upvote;

    @Column(nullable = false)
    private boolean downvote;

}