package io.spring.service;

import io.spring.dao.article.Article;
import io.spring.dao.comment.Comment;
import io.spring.dao.user.User;

public class AuthorizationService {
    public static boolean canWriteArticle(User user, Article article) {
        return user.getId().equals(article.getUserId());
    }

    public static boolean canWriteComment(User user, Article article, Comment comment) {
        return user.getId().equals(article.getUserId()) || user.getId().equals(comment.getUserId());
    }
}
