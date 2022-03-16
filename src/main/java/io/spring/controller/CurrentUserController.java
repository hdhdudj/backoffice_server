package io.spring.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonRootName;

import io.spring.dao.user.User;
import io.spring.dao.user.UserRepository;
import io.spring.infrastructure.util.exception.InvalidRequestException;
import io.spring.model.UserData;
import io.spring.model.UserWithToken;
import io.spring.service.UserQueryService;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping(path = "/user")
public class CurrentUserController {
    private UserQueryService userQueryService;
    private UserRepository userRepository;


    @Autowired
	public CurrentUserController(UserQueryService userQueryService, UserRepository userRepository) {
        this.userQueryService = userQueryService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity currentUser(@AuthenticationPrincipal User currentUser,
			@RequestHeader(value = "Authorization") String authorization, HttpServletRequest req,
			HttpServletResponse res) {

		Cookie[] c = req.getCookies();

		if (c.length > 0) {
			System.out.println("쿠키있음");
			for (Cookie o : c) {
				System.out.println(o.getName());
				System.out.println(o.getValue());

			}

		} else {
			System.out.println("쿠키없음");
		}

		/*
		 * if (cookie != null) { System.out.println("쿠키있음"); System.out.println(cookie);
		 * } else { System.out.println("쿠키없음"); }
		 */

		if (currentUser == null) {
			System.out.println("사용자 없음");
		} else {
			System.out.println(currentUser);
		}

        UserData userData = userQueryService.findById(currentUser.getId()).get();
        //
        
		LocalDate now = LocalDate.now();

		// create a cookie
		Cookie cookie1 = new Cookie("aaa11", "bbb11-" + now.toString());

		// expires in 7 days
		cookie1.setMaxAge(1 * 24 * 60 * 60);

		// optional properties
		cookie1.setSecure(true);
		cookie1.setHttpOnly(true);
		cookie1.setPath("/");

		// add cookie to response
		res.addCookie(cookie1);

        return ResponseEntity.ok(userResponse(
				new UserWithToken(userData, authorization.split(" ")[1], "")
        ));
    }

    @PutMapping
    public ResponseEntity updateProfile(@AuthenticationPrincipal User currentUser,
                                        @RequestHeader("Authorization") String token,
                                        @Valid @RequestBody UpdateUserParam updateUserParam,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        checkUniquenessOfUsernameAndEmail(currentUser, updateUserParam, bindingResult);

        currentUser.update(
            updateUserParam.getEmail(),
            updateUserParam.getUsername(),
            updateUserParam.getPassword(),
            updateUserParam.getBio(),
            updateUserParam.getImage());
        userRepository.save(currentUser);
        UserData userData = userQueryService.findById(currentUser.getId()).get();
        return ResponseEntity.ok(userResponse(
				new UserWithToken(userData, token.split(" ")[1], "")
        ));
    }

    private void checkUniquenessOfUsernameAndEmail(User currentUser, UpdateUserParam updateUserParam, BindingResult bindingResult) {
        if (!"".equals(updateUserParam.getUsername())) {
            Optional<User> byUsername = userRepository.findByUsername(updateUserParam.getUsername());
            if (byUsername.isPresent() && !byUsername.get().equals(currentUser)) {
                bindingResult.rejectValue("username", "DUPLICATED", "username already exist");
            }
        }

        if (!"".equals(updateUserParam.getEmail())) {
            Optional<User> byEmail = userRepository.findByEmail(updateUserParam.getEmail());
            if (byEmail.isPresent() && !byEmail.get().equals(currentUser)) {
                bindingResult.rejectValue("email", "DUPLICATED", "email already exist");
            }
        }

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }

    private Map<String, Object> userResponse(UserWithToken userWithToken) {
        return new HashMap<String, Object>() {{
            put("user", userWithToken);
        }};
    }
}

@Getter
@JsonRootName("user")
@NoArgsConstructor
class UpdateUserParam {
    @Email(message = "should be an email")
    private String email = "";
    private String password = "";
    private String username = "";
    private String bio = "";
    private String image = "";
}
