package io.spring.dao.user;

import io.spring.infrastructure.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MyBatisUserDaoImpl implements UserRepository {
    private final UserMapper userMapper;

    @Autowired
    public MyBatisUserDaoImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void save(User user) {
        if (userMapper.findById(user.getId()) == null) {
            userMapper.insert(user);
        } else {
            userMapper.update(user);
        }
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(userMapper.findById(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMapper.findByUsername(username));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMapper.findByEmail(email));
    }

    @Override
    public void saveRelation(FollowRelation followRelation) {
        if (!findRelation(followRelation.getUserId(), followRelation.getTargetId()).isPresent()) {
            userMapper.saveRelation(followRelation);
        }
    }

    @Override
    public Optional<FollowRelation> findRelation(String userId, String targetId) {
        return Optional.ofNullable(userMapper.findRelation(userId, targetId));
    }

    @Override
    public void removeRelation(FollowRelation followRelation) {
        userMapper.deleteRelation(followRelation);
    }
}
