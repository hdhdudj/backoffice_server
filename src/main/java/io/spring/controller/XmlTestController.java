package io.spring.controller;

import io.spring.jparepos.goods.JpaXmlTestRepository;
import io.spring.model.goods.entity.XmlTest;
import io.spring.model.stock.reponse.GoodsStockXml;
import io.spring.model.stock.request.GoodsStockRequest;
import io.spring.service.stock.JpaStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value="/xml")
@RequiredArgsConstructor
public class XmlTestController {
    private final JpaXmlTestRepository jpaXmlTestRepository;
    private final JpaStockService jpaStockService;

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


//    @GetMapping(value = "/godo/goods/stock", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @PostMapping(value = "/godo/goods/stock", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @ResponseBody
//    public GoodsStockXml godoGoodsStock(@RequestParam("goodsNo") String goodsNo,@RequestParam("optionFl") String optionFl,@RequestParam("totalStock") Long totalStock){
    public GoodsStockXml godoGoodsStock(@RequestBody GoodsStockRequest goodsStockRequest){
//        XmlTest xmlTest = jpaXmlTestRepository.findById(assortId).orElseGet(() -> null);
//        GoodsStockXml g = new GoodsStockXml(goodsNo, optionFl, totalStock);
        GoodsStockXml g = new GoodsStockXml(goodsStockRequest);
//        String xml = null;
//        if(xmlTest != null){
//            xml = xmlTest.getXml();
//        }
        return g;
    }
}
