package io.spring.dao.article;

import java.util.Optional;

public interface MyBatisArticleDao {

    void save(Article article);

    Optional<Article> findById(String id);

    Optional<Article> findBySlug(String slug);


    void remove(Article article);
}
