package io.mountblue.StackOverflow.specification;

import io.mountblue.StackOverflow.entity.Question;
import io.mountblue.StackOverflow.entity.QuestionTag;
import io.mountblue.StackOverflow.entity.Users;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;

public class QuestionSpecification {

    public static Specification<Question> hasTag(String tagName) {
        return (root, query, cb) -> {
            if (tagName == null || tagName.isBlank()) return null;
            Join<Question, QuestionTag> tags = root.join("questionTags", JoinType.INNER);
            return cb.equal(cb.lower(tags.get("tag").get("tagName")), tagName.toLowerCase(Locale.ROOT));
        };
    }

    public static Specification<Question> hasUser(String userName) {
        return (root, query, cb) -> {
            if (userName == null || userName.isBlank()) return null;
            Join<Question, Users> user = root.join("user", JoinType.INNER);
            return cb.equal(cb.lower(user.get("username")), userName.toLowerCase(Locale.ROOT));
        };
    }

    public static Specification<Question> hasTitleContaining(String word) {
        return (root, query, cb) -> {
            if (word == null || word.isBlank()) return null;
            return cb.like(cb.lower(root.get("title")), "%" + word.toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Question> hasAcceptedAnswer() {
        return (root, query, cb) -> {
            // Assuming accepted answers are marked in the Answer entity
            return cb.isTrue(root.join("answerList", JoinType.LEFT).get("accepted"));
        };
    }

    public static Specification<Question> hasNoAnswers() {
        return (root, query, cb) -> cb.isEmpty(root.get("answerList"));
    }

    public static Specification<Question> hasNoAcceptedAnswer() {
        return (root, query, cb) -> {
            Join<Question, ?> answers = root.join("answerList", JoinType.LEFT);
            return cb.isFalse(answers.get("accepted"));
        };
    }

    public static Specification<Question> isDaysOld(int days) {
        return (root, query, cb) -> {
            if (days <= 0) return null;
            return cb.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    java.time.LocalDateTime.now().minusDays(days)
            );
        };
    }

    public static Specification<Question> hasAnyTag(List<String> tagNames) {
        return (root, query, cb) -> {
            if (tagNames == null || tagNames.isEmpty()) return null;
            Join<Question, QuestionTag> tags = root.join("questionTags", JoinType.INNER);
            CriteriaBuilder.In<String> inClause = cb.in(cb.lower(tags.get("tag").get("tagName")));
            tagNames.forEach(tag -> inClause.value(tag.toLowerCase(Locale.ROOT)));
            return inClause;
        };
    }

}
