package io.spring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpApiService {
    public int get(String requestURL) {
        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpGet getRequest = new HttpGet(requestURL); //GET 메소드 URL 생성
            getRequest.addHeader("x-api-key", ""); //KEY 입력

            HttpResponse response = client.execute(getRequest);

            //Response 출력
            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                System.out.println(body);
                return 200;
            } else {
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
                log.debug("response is error : " + response.getStatusLine().getStatusCode());
                return -1;
            }
        } catch (Exception e){
            System.err.println(e.toString());
            log.debug(e.toString());
        }
        return -1;
    }

    public int post(String requestURL, Map<String, String> headerMap, String json) {
        try {
            int maxTimeOut = 1000;
            RequestConfig reqConfig = RequestConfig.custom().setConnectTimeout(maxTimeOut).setConnectionRequestTimeout(maxTimeOut).setSocketTimeout(maxTimeOut).build();
            CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(reqConfig).build(); // HttpClient 생성
            HttpPost postRequest = new HttpPost(requestURL); //GET 메소드 URL 생성

            for(String key : headerMap.keySet()){
                postRequest.addHeader(key, headerMap.get(key));
            }
            StringEntity stringEntity = new StringEntity(json);
            postRequest.setEntity(stringEntity);
            HttpResponse response = client.execute(postRequest);

            //Response 출력
            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                System.out.println(body);
                return 200;
            } else {
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
                log.debug("response is error : " + response.getStatusLine().getStatusCode());
                return -1;
            }
        } catch (Exception e){
            System.err.println(e.toString());
            log.debug(e.toString());
        }
        return -1;
    }
}
