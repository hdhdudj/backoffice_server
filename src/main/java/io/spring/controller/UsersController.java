package io.spring.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonRootName;

import io.spring.infrastructure.util.exception.InvalidRequestException;
import io.spring.service.UserQueryService;
import io.spring.model.UserData;
import io.spring.model.UserWithToken;
import io.spring.service.JwtService;
import io.spring.dao.user.EncryptService;
import io.spring.dao.user.User;
import io.spring.dao.user.UserRepository;
import io.spring.infrastructure.util.ApiResponseMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;


@RestController
public class UsersController {
	private UserRepository userRepository;
	private UserQueryService userQueryService;
	private String defaultImage;
	private EncryptService encryptService;
	private JwtService jwtService;

	@Autowired
	public UsersController(UserRepository userRepository, UserQueryService userQueryService, EncryptService encryptService,
						   @Value("${image.default}") String defaultImage, JwtService jwtService) {
		this.userRepository = userRepository;
		this.userQueryService = userQueryService;
		this.encryptService = encryptService;
		this.defaultImage = defaultImage;
		this.jwtService = jwtService;
	}


	@RequestMapping(path = "/users", method = POST)
	public ResponseEntity createUser(@Valid @RequestBody RegisterParam registerParam, BindingResult bindingResult) {
		checkInput(registerParam, bindingResult);

		System.out.println(defaultImage);

		User user = new User(registerParam.getEmail(), registerParam.getUsername(),
				encryptService.encrypt(registerParam.getPassword()), "", defaultImage);
		userRepository.save(user);
		UserData userData = userQueryService.findById(user.getId()).get();
		return ResponseEntity.status(201).body(userResponse(new UserWithToken(userData, jwtService.toToken(user))));
	}

	private void checkInput(@Valid @RequestBody RegisterParam registerParam, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		if (userRepository.findByUsername(registerParam.getUsername()).isPresent()) {
			bindingResult.rejectValue("username", "DUPLICATED", "duplicated username");
		}

		if (userRepository.findByEmail(registerParam.getEmail()).isPresent()) {
			bindingResult.rejectValue("email", "DUPLICATED", "duplicated email");
		}

		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
	}

	@RequestMapping(path = "/users/login", method = POST)
	public ResponseEntity userLogin(@Valid @RequestBody LoginParam loginParam, BindingResult bindingResult) {
		Optional<User> optional = userRepository.findByEmail(loginParam.getEmail());
		if (optional.isPresent() && encryptService.check(loginParam.getPassword(), optional.get().getPassword())) {
			UserData userData = userQueryService.findById(optional.get().getId()).get();

			Map<String, Object> o = userResponse(new UserWithToken(userData, jwtService.toToken(optional.get())));

			return ResponseEntity.ok(new ApiResponseMessage<Map<String, Object>>("SUCCES", "",
					userResponse(new UserWithToken(userData, jwtService.toToken(optional.get())))));
		} else {
			bindingResult.rejectValue("password", "INVALID", "invalid email or password");
			throw new InvalidRequestException(bindingResult);
		}
	}

	private Map<String, Object> userResponse(UserWithToken userWithToken) {
		return new HashMap<String, Object>() {
			{
				put("user", userWithToken);
			}
		};
	}
}

@Getter
@JsonRootName("user")
@NoArgsConstructor
class LoginParam {
	@NotBlank(message = "can't be empty")
	@Email(message = "should be an email")
	private String email;
	@NotBlank(message = "can't be empty")
	private String password;
}

@Getter
@JsonRootName("user")
@NoArgsConstructor
class RegisterParam {
	@NotBlank(message = "can't be empty")
	@Email(message = "should be an email")
	private String email;
	@NotBlank(message = "can't be empty")
	private String username;
	@NotBlank(message = "can't be empty")
	private String password;
}