package io.spring.controller;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.spring.infrastructure.util.exception.NoAuthorizationException;
import io.spring.infrastructure.util.exception.ResourceNotFoundException;
import io.spring.service.AuthorizationService;
import io.spring.model.ArticleData;
import io.spring.service.ArticleQueryService;
import io.spring.dao.article.MyBatisArticleDao;
import io.spring.dao.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/articles/{slug}")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleQueryService articleQueryService;
    private final MyBatisArticleDao articleRepository;

    @GetMapping
    public ResponseEntity<?> article(@PathVariable("slug") String slug,
                                     @AuthenticationPrincipal User user) {
        return articleQueryService.findBySlug(slug, user)
            .map(articleData -> ResponseEntity.ok(articleResponse(articleData)))
            .orElseThrow(ResourceNotFoundException::new);
    }

    @PutMapping
    public ResponseEntity<?> updateArticle(@PathVariable("slug") String slug,
                                           @AuthenticationPrincipal User user,
                                           @Valid @RequestBody UpdateArticleParam updateArticleParam) {
        return articleRepository.findBySlug(slug).map(article -> {
            if (!AuthorizationService.canWriteArticle(user, article)) {
                throw new NoAuthorizationException();
                
                
              
            }
            article.update(
                updateArticleParam.getTitle(),
                updateArticleParam.getDescription(),
                updateArticleParam.getBody());
            articleRepository.save(article);
            return ResponseEntity.ok(articleResponse(articleQueryService.findBySlug(slug, user).get()));
        }).orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping
    public ResponseEntity deleteArticle(@PathVariable("slug") String slug,
                                        @AuthenticationPrincipal User user) {
        return articleRepository.findBySlug(slug).map(article -> {
            if (!AuthorizationService.canWriteArticle(user, article)) {
                throw new NoAuthorizationException();
            }
            articleRepository.remove(article);
            return ResponseEntity.noContent().build();
        }).orElseThrow(ResourceNotFoundException::new);
    }

    private Map<String, Object> articleResponse(ArticleData articleData) {
        return new HashMap<String, Object>() {{
            put("article", articleData);
        }};
    }
}

@Getter
@NoArgsConstructor
@JsonRootName("article")
class UpdateArticleParam {
    private String title = "";
    private String body = "";
    private String description = "";
}
