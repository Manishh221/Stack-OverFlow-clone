package io.mountblue.StackOverflow.dto;

import io.mountblue.StackOverflow.entity.Tag;
import io.mountblue.StackOverflow.entity.Users;

import java.util.List;

public class UserWithTags {
    private Users user;
    private List<Tag> tags;

    public UserWithTags(Users user, List<Tag> tags) {
        this.user = user;
        this.tags = tags;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
