package io.spring.controller;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.spring.infrastructure.util.exception.InvalidRequestException;
import io.spring.infrastructure.util.exception.NoAuthorizationException;
import io.spring.infrastructure.util.exception.ResourceNotFoundException;
import io.spring.dao.service.AuthorizationService;
import io.spring.model.CommentData;
import io.spring.service.CommentQueryService;
import io.spring.dao.article.Article;
import io.spring.dao.article.ArticleDao;
import io.spring.dao.comment.Comment;
import io.spring.dao.comment.CommentRepository;
import io.spring.dao.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/articles/{slug}/comments")
public class CommentsController {
    private ArticleDao articleRepository;
    private CommentRepository commentRepository;
    private CommentQueryService commentQueryService;

    @Autowired
    public CommentsController(ArticleDao articleRepository,
                              CommentRepository commentRepository,
                              CommentQueryService commentQueryService) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.commentQueryService = commentQueryService;
    }

    @PostMapping
    public ResponseEntity<?> createComment(@PathVariable("slug") String slug,
                                                     @AuthenticationPrincipal User user,
                                                     @Valid @RequestBody NewCommentParam newCommentParam,
                                                     BindingResult bindingResult) {
        Article article = findArticle(slug);
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        Comment comment = new Comment(newCommentParam.getBody(), user.getId(), article.getId());
        commentRepository.save(comment);
        return ResponseEntity.status(201).body(commentResponse(commentQueryService.findById(comment.getId(), user).get()));
    }

    @GetMapping
    public ResponseEntity getComments(@PathVariable("slug") String slug,
                                      @AuthenticationPrincipal User user) {
        Article article = findArticle(slug);
        List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), user);
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("comments", comments);
        }});
    }

    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteComment(@PathVariable("slug") String slug,
                                        @PathVariable("id") String commentId,
                                        @AuthenticationPrincipal User user) {
        Article article = findArticle(slug);
        return commentRepository.findById(article.getId(), commentId).map(comment -> {
            if (!AuthorizationService.canWriteComment(user, article, comment)) {
                throw new NoAuthorizationException();
            }
            commentRepository.remove(comment);
            return ResponseEntity.noContent().build();
        }).orElseThrow(ResourceNotFoundException::new);
    }

    private Article findArticle(String slug) {
        return articleRepository.findBySlug(slug).map(article -> article).orElseThrow(ResourceNotFoundException::new);
    }

    private Map<String, Object> commentResponse(CommentData commentData) {
        return new HashMap<String, Object>() {{
            put("comment", commentData);
        }};
    }
}

@Getter
@NoArgsConstructor
@JsonRootName("comment")
class NewCommentParam {
    @NotBlank(message = "can't be empty")
    private String body;
}
