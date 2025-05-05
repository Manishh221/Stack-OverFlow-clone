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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
