package io.spring.service;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.GoodsResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpaCommonService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JpaCommonService jpaCommonService;


}
