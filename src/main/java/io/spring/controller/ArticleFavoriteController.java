package io.spring.controller;

import io.spring.infrastructure.util.exception.ResourceNotFoundException;
import io.spring.model.ArticleData;
import io.spring.service.ArticleQueryService;
import io.spring.dao.article.Article;
import io.spring.dao.article.MyBatisArticleDao;
import io.spring.dao.favorite.ArticleFavorite;
import io.spring.dao.favorite.MyBatisArticleFavoriteDao;
import io.spring.dao.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(path = "articles/{slug}/favorite")
public class ArticleFavoriteController {
    private MyBatisArticleFavoriteDao myBatisArticleFavoriteDao;
    private MyBatisArticleDao articleRepository;
    private ArticleQueryService articleQueryService;

    @Autowired
    public ArticleFavoriteController(MyBatisArticleFavoriteDao myBatisArticleFavoriteDao,
                                     MyBatisArticleDao articleRepository,
                                     ArticleQueryService articleQueryService) {
        this.myBatisArticleFavoriteDao = myBatisArticleFavoriteDao;
        this.articleRepository = articleRepository;
        this.articleQueryService = articleQueryService;
    }

    @PostMapping
    public ResponseEntity favoriteArticle(@PathVariable("slug") String slug,
                                          @AuthenticationPrincipal User user) {
        Article article = getArticle(slug);
        ArticleFavorite articleFavorite = new ArticleFavorite(article.getId(), user.getId());
        myBatisArticleFavoriteDao.save(articleFavorite);
        return responseArticleData(articleQueryService.findBySlug(slug, user).get());
    }

    @DeleteMapping
    public ResponseEntity unfavoriteArticle(@PathVariable("slug") String slug,
                                            @AuthenticationPrincipal User user) {
        Article article = getArticle(slug);
        myBatisArticleFavoriteDao.find(article.getId(), user.getId()).ifPresent(favorite -> {
            myBatisArticleFavoriteDao.remove(favorite);
        });
        return responseArticleData(articleQueryService.findBySlug(slug, user).get());
    }

    private ResponseEntity<HashMap<String, Object>> responseArticleData(final ArticleData articleData) {
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("article", articleData);
        }});
    }

    private Article getArticle(String slug) {
        return articleRepository.findBySlug(slug).map(article -> article)
            .orElseThrow(ResourceNotFoundException::new);
    }
}
