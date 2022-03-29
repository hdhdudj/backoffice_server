package io.spring.dao.article;

import io.spring.infrastructure.mybatis.mapper.ArticleMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class MyBatisArticleDaoImpl implements MyBatisArticleDao {
    private ArticleMapper articleMapper;

    public MyBatisArticleDaoImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    @Transactional
    public void save(Article article) {
        if (articleMapper.findById(article.getId()) == null) {
            createNew(article);
        } else {
            articleMapper.update(article);
        }
    }

    private void createNew(Article article) {
        for (Tag tag : article.getTags()) {
            Tag targetTag = Optional.ofNullable(articleMapper.findTag(tag.getName())).orElseGet(() -> {
                articleMapper.insertTag(tag);
                return tag;
            });
            articleMapper.insertArticleTagRelation(article.getId(), targetTag.getId());
        }
        articleMapper.insert(article);
    }

    @Override
    public Optional<Article> findById(String id) {
        return Optional.ofNullable(articleMapper.findById(id));
    }

    @Override
    public Optional<Article> findBySlug(String slug) {
        return Optional.ofNullable(articleMapper.findBySlug(slug));
    }

    @Override
    public void remove(Article article) {
        articleMapper.delete(article.getId());
    }
}
