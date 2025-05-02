package io.mountblue.StackOverflow.dto;

import java.time.LocalDateTime;

public class TagWithCountDTO {
    private Long id;
    private String name;
    private Long questionCount;
    private LocalDateTime createdAt;

    public TagWithCountDTO(Long id, String name, Long questionCount,LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.questionCount = questionCount;
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Long questionCount) {
        this.questionCount = questionCount;
    }
}
