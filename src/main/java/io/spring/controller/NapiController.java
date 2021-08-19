package io.spring.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.naver.RestClient;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/napi")
//@RequiredArgsConstructor
@Slf4j
public class NapiController {

	@GetMapping(path = "/keyword")
	public ResponseEntity getNaverAdKeyword(@RequestParam @Nullable String keyword) {
		
		String responseBody = "";
		Map<String, Object> map = new HashMap<>();
		try {
			// Properties properties = PropertiesLoader.fromResource("sample.properties");
			String baseUrl = "https://api.naver.com";
			String apiKey = "0100000000407d6b6114937663dc1bbc7470fef6226f101045338598213960b4470481e189";
			String secretKey = "AQAAAABAfWthFJN2Y9wbvHRw/vYiDC/vzFJQ2jh41dg92VDs/A==";
			long customerId = 1109163;

			RestClient rest = RestClient.of(baseUrl, apiKey, secretKey);

			HttpRequest request = rest.get("/keywordstool", customerId).queryString("hintKeywords", keyword)
					.queryString("showDetail", 1);

			HttpResponse<String> r = request.asString();

			responseBody = r.getBody();


			// map.put("msg", responseBody);

		} catch (Exception e) {
			e.printStackTrace();
		}


		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
				responseBody);
		return ResponseEntity.ok(res);
	}

}
