package io.spring.service;

import io.spring.dao.user.User;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import io.spring.model.ProfileData;
import io.spring.model.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProfileQueryService {
    private final UserReadService userReadService;
    private final UserRelationshipQueryService userRelationshipQueryService;

    public Optional<ProfileData> findByUsername(String username, User currentUser) {
        UserData userData = userReadService.findByUsername(username);
        if (userData == null) {
            return Optional.empty();
        } else {
            ProfileData profileData = new ProfileData(
                userData.getId(),
                userData.getUsername(),
                userData.getBio(),
                userData.getImage(),
                userRelationshipQueryService.isUserFollowing(currentUser.getId(), userData.getId()));
            return Optional.of(profileData);
        }
    }
}
