package io.spring.dao.comment;

import io.spring.infrastructure.mybatis.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MyBatisCommentDaoImpl implements MyBatisCommentDao {
    private CommentMapper commentMapper;

    @Autowired
    public MyBatisCommentDaoImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public void save(Comment comment) {
        commentMapper.insert(comment);
    }

    @Override
    public Optional<Comment> findById(String articleId, String id) {
        return Optional.ofNullable(commentMapper.findById(articleId, id));
    }

    @Override
    public void remove(Comment comment) {
        commentMapper.delete(comment.getId());
    }
}
