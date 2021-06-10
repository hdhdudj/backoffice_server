package io.spring.controller;

import io.spring.jparepos.goods.JpaXmlTestRepository;
import io.spring.model.goods.entity.XmlTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value="/xml")
@RequiredArgsConstructor
public class XmlTestController {
    private final JpaXmlTestRepository jpaXmlTestRepository;

    @GetMapping("/goodsinsert")
    @ResponseBody
    public String getGoodsInsertXml(@RequestParam("assortId") String assortId){
        XmlTest xmlTest = jpaXmlTestRepository.findById(assortId).orElseGet(() -> null);
        String xml = null;
        if(xmlTest != null){
            xml = xmlTest.getXml();
        }
        return xml;
    }
}
