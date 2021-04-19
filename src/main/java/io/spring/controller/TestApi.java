package io.spring.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonRootName;

import io.spring.dao.user.Test;
import io.spring.dao.user.TestRepository;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.ApiResponseMessageWithTuiGrid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Getter;
import lombok.NoArgsConstructor;


@RestController
@Api(value = "test api", tags = "test")
public class TestApi {
	private TestRepository testRepository;

	@Autowired
	public TestApi(TestRepository testRepository) {
		this.testRepository = testRepository;
	}

	@Autowired
	ApplicationContext context;

	@RequestMapping(path = "/test", method = POST)
	public ResponseEntity createTest(@Valid @RequestBody RegisterParam1 registerParam, BindingResult bindingResult) {

		Test t = new Test(registerParam.getId(), registerParam.getTest1(), registerParam.getTest2(),
				registerParam.getTest3(), registerParam.getTest4());

		testRepository.save(t);

		Optional<Test> r = testRepository.findById(t.getId());

		return ResponseEntity.status(201).body(new ApiResponseMessage<Optional<Test>>("SUCCES", "", r));
	}

	@ApiOperation(value = "testa", notes = "test 1111.")
	@RequestMapping(path = "/testa", method = POST)
	@ApiResponses({ @ApiResponse(code = 200, message = "ok!!"), @ApiResponse(code = 500, message = "error!!"),
			@ApiResponse(code = 404, message = "not found !!") })
	public ResponseEntity createTest1(@Valid @RequestBody Map<String, Object> param) {



		for (Map.Entry<String, Object> entry : param.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}


		Environment env = context.getEnvironment();

		String mode = env.getProperty("app.mode");

		return ResponseEntity.status(201).body(new ApiResponseMessage<String>("SUCCES", "", mode));

		// UserData userData = userQueryService.findById(user.getId()).get();
		// return ResponseEntity.status(201).body(userResponse(new
		// UserWithToken(userData, jwtService.toToken(user))));
	}


	@RequestMapping(path = "/tests", method = RequestMethod.GET)
	public ResponseEntity findTests() {

		Map<String, Object> m = new HashMap<String, Object>();

		List<Test> r = testRepository.findTests();

		m.put("contents", r);

		return ResponseEntity.ok(new ApiResponseMessageWithTuiGrid<Map<String, Object>>(true, "", m));

		// UserData userData = userQueryService.findById(user.getId()).get();
		// return ResponseEntity.status(201).body(userResponse(new
		// UserWithToken(userData, jwtService.toToken(user))));
	}

	/*
	 * private void checkInput(@Valid @RequestBody RegisterParam registerParam,
	 * BindingResult bindingResult) { if (bindingResult.hasErrors()) { throw new
	 * InvalidRequestException(bindingResult); } if
	 * (userRepository.findByUsername(registerParam.getUsername()).isPresent()) {
	 * bindingResult.rejectValue("username", "DUPLICATED", "duplicated username"); }
	 * 
	 * if (userRepository.findByEmail(registerParam.getEmail()).isPresent()) {
	 * bindingResult.rejectValue("email", "DUPLICATED", "duplicated email"); }
	 * 
	 * if (bindingResult.hasErrors()) { throw new
	 * InvalidRequestException(bindingResult); } }
	 */
	/*
	 * @RequestMapping(path = "/users/login", method = POST) public ResponseEntity
	 * userLogin(@Valid @RequestBody LoginParam loginParam, BindingResult
	 * bindingResult) { Optional<User> optional =
	 * userRepository.findByEmail(loginParam.getEmail()); if (optional.isPresent()
	 * && encryptService.check(loginParam.getPassword(),
	 * optional.get().getPassword())) { UserData userData =
	 * userQueryService.findById(optional.get().getId()).get();
	 * 
	 * Map<String, Object> o = userResponse(new UserWithToken(userData,
	 * jwtService.toToken(optional.get())));
	 * 
	 * return ResponseEntity.ok(new ApiResponseMessage<Map<String,
	 * Object>>("SUCCES", "", userResponse(new UserWithToken(userData,
	 * jwtService.toToken(optional.get()))))); } else {
	 * bindingResult.rejectValue("password", "INVALID",
	 * "invalid email or password"); throw new
	 * InvalidRequestException(bindingResult); } }
	 */
	/*
	 * private Map<String, Object> userResponse(UserWithToken userWithToken) {
	 * return new HashMap<String, Object>() { { put("user", userWithToken); } }; }
	 */
}

/*
 * @Getter
 * 
 * @JsonRootName("user")
 * 
 * @NoArgsConstructor class LoginParam {
 * 
 * @NotBlank(message = "can't be empty")
 * 
 * @Email(message = "should be an email") private String email;
 * 
 * @NotBlank(message = "can't be empty") private String password; }
 */
@Getter
@JsonRootName("user")
@NoArgsConstructor
class RegisterParam1 {
	private int id;
	@NotBlank(message = "can't be empty")
	private String test1;
	@NotBlank(message = "can't be empty")
	private String test2;
	@NotBlank(message = "can't be empty")
	private String test3;
	@NotBlank(message = "can't be empty")
	private String test4;
}
