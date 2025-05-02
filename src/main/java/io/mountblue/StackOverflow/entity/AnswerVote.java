package io.mountblue.StackOverflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "answervote")
@Getter
@Setter
public class AnswerVote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "answer_id",referencedColumnName = "id")
    private Answer answer;

    @Column(nullable = false)
    private boolean upvote;

    @Column(nullable = false)
    private boolean downvote;

}