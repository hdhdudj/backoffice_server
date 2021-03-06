package io.spring.controller;

import io.spring.infrastructure.util.exception.ResourceNotFoundException;
import io.spring.model.ProfileData;
import io.spring.service.ProfileQueryService;
import io.spring.dao.user.FollowRelation;
import io.spring.dao.user.User;
import io.spring.dao.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping(path = "profiles/{username}")
public class ProfileController {
    private ProfileQueryService profileQueryService;
    private UserRepository userRepository;

    @Autowired
    public ProfileController(ProfileQueryService profileQueryService, UserRepository userRepository) {
        this.profileQueryService = profileQueryService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity getProfile(@PathVariable("username") String username,
                                     @AuthenticationPrincipal User user) {
        return profileQueryService.findByUsername(username, user)
            .map(this::profileResponse)
            .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping(path = "follow")
    public ResponseEntity follow(@PathVariable("username") String username,
                                 @AuthenticationPrincipal User user) {
        return userRepository.findByUsername(username).map(target -> {
            FollowRelation followRelation = new FollowRelation(user.getId(), target.getId());
            userRepository.saveRelation(followRelation);
            return profileResponse(profileQueryService.findByUsername(username, user).get());
        }).orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping(path = "follow")
    public ResponseEntity unfollow(@PathVariable("username") String username,
                                   @AuthenticationPrincipal User user) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User target = userOptional.get();
            return userRepository.findRelation(user.getId(), target.getId())
                .map(relation -> {
                    userRepository.removeRelation(relation);
                    return profileResponse(profileQueryService.findByUsername(username, user).get());
                }).orElseThrow(ResourceNotFoundException::new);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    private ResponseEntity profileResponse(ProfileData profile) {
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("profile", profile);
        }});
    }
}
