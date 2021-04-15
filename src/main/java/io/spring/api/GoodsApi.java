package io.spring.api;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.core.common.CommonRepository;
import io.spring.core.order.OrderRepository;
import io.spring.infrastructure.util.ApiResponseMessage;

@RestController
@RequestMapping(value = "/goods")
public class GoodsApi {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
}
