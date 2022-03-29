package io.spring.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.naver.RestClient;
import io.spring.model.napi.KeywordTool;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/napi")
//@RequiredArgsConstructor
@Slf4j
public class NapiController {

	@GetMapping(path = "/keyword")
	public ResponseEntity getNaverAdKeyword(@RequestParam @Nullable String keyword) {
		
		String responseBody = "";
		Map<String, Object> ret = new HashMap<>();
		List<KeywordTool> list = new ArrayList<KeywordTool>();

		JSONObject rr = null;

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

			rr = new JSONObject(responseBody);

			String l = rr.get("keywordList").toString();

			Gson gson = new Gson();

			
			// JSONObject jsonObject = new JSONObject(responseBody);
			// KeywordTool[] arr = gson.fromJson(responseBody, KeywordTool[].class);

			// KeywordTool[] array = gson.fromJson(responseBody, KeywordTool[].class);
//			list = Arrays.asList(array);

			list = gson.fromJson(l, new TypeToken<List<KeywordTool>>() {
			}.getType());


			ret.put("keywordList", list);

		} catch (Exception e) {
			e.printStackTrace();
			ApiResponseMessage res = new ApiResponseMessage("ERROR", "ERROR", "");
			return ResponseEntity.ok(res);
		}


		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
				ret);
		return ResponseEntity.ok(res);
	}

}
