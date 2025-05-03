package io.mountblue.StackOverflow.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tags")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserTags {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "tag_id",referencedColumnName = "tagId")
    private Tag tag;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Override
    public String toString() {
        return "UserTags{" +
                "user=" + user +
                ", tag=" + tag +
                ", createdAt=" + createdAt +
                '}';
    }
}
