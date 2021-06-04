package io.spring.service.goods;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.infrastructure.util.exception.ResourceNotFoundException;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.goods.*;
import io.spring.model.common.entity.SequenceData;
import io.spring.model.file.FileVo;
import io.spring.model.goods.entity.*;
import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.goods.response.GoodsInsertResponseData;
import io.spring.model.goods.response.GoodsSelectDetailResponseData;
import io.spring.model.goods.response.GoodsSelectListResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;

@Service
public class JpaGoodsService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @Autowired
    private JpaItasrtRepository jpaItasrtRepository;
    @Autowired
    private JpaItasrnRepository jpaItasrnRepository;
    @Autowired
    private JpaItvariRepository jpaItvariRepository;
//    @Autowired
//    private MyBatisCommonDao myBatisCommonDao;
//    @Autowired
//    private MyBatisGoodsDao myBatisGoodsDao;
    @Autowired
    private JpaItasrdRepository jpaItasrdRepository;
    @Autowired
    private JpaItitmmRepository jpaItitmmRepository;
    @Autowired
    private JpaItitmdRepository jpaItitmdRepository;
    @Autowired
    private JpaSequenceDataRepository jpaSequenceDataRepository;
    @Autowired
    private EntityManager em;
    
    @Autowired
    private JpaItaimgRepository jpaItaimgRepository;


    public List<Itasrt> findAll() {
        List<Itasrt> goods = new ArrayList<>();
        jpaItasrtRepository.findAll().forEach(e -> goods.add(e));
        return goods;
    }

    public Optional<Itasrt> findById(String goodsId) {
        Optional<Itasrt> goods = jpaItasrtRepository.findById(goodsId);
        return goods;
    }

    /**
     * goods 정보 insert 시퀀스 함수
     * Pecan 21-04-26
     * @param goodsInsertRequestData
     * @return GoodsResponseData
     */
    @Transactional
    public GoodsInsertResponseData sequenceInsertOrUpdateGoods(GoodsInsertRequestData goodsInsertRequestData){
        // itasrt?뿉 goods ?젙蹂? ???옣
        Itasrt itasrt = this.saveItasrt(goodsInsertRequestData);
        // itsrn?뿉 goods ?씠?젰 ???옣
        Itasrn itasrn = this.saveItasrn(goodsInsertRequestData);
        // itasrd?뿉 臾멸뎄 ???옣
        List<Itasrd> itasrd = this.saveItasrd(goodsInsertRequestData);
        // itvari?뿉 assort_id蹂? ?샃?뀡?슂?냼 ???옣(?깋?긽, ?궗?씠利?)
        List<Itvari> itvariList = this.saveItvariList(goodsInsertRequestData);
        // ititmm?뿉 assort_id蹂? item ???옣
        List<Ititmm> ititmmList = this.saveItemList(goodsInsertRequestData);
        // ititmd?뿉 item ?씠?젰 ???옣
        List<Ititmd> ititmdList = this.saveItemHistoryList(goodsInsertRequestData, ititmmList);

        List<GoodsInsertResponseData.Attributes> attributesList = makeGoodsResponseAttributes(itvariList);
        List<GoodsInsertResponseData.Items> itemsList = makeGoodsResponseItems(ititmmList);
        return makeGoodsInsertResponseData(goodsInsertRequestData, attributesList, itemsList);
    }

    private List<GoodsInsertResponseData.Attributes> makeGoodsResponseAttributes(List<Itvari> itvariList){
        return null;
    }

    private List<GoodsInsertResponseData.Items> makeGoodsResponseItems(List<Ititmm> ititmm){
        return null;
    }

    private GoodsInsertResponseData makeGoodsInsertResponseData(GoodsInsertRequestData goodsInsertRequestData, List<GoodsInsertResponseData.Attributes> attributesList, List<GoodsInsertResponseData.Items> itemsList){
        GoodsInsertResponseData goodsInsertResponseData = GoodsInsertResponseData.builder().goodsInsertRequestData(goodsInsertRequestData)
                .attributesList(attributesList).itemsList(itemsList).build();
        return goodsInsertResponseData;
    }

    public void deleteById(String goodsId) {
        jpaItasrtRepository.deleteById(goodsId);
    }

    /**
     * 21-04-27 Pecan
     * 물품 정보 저장 insert, update
     * @param goodsInsertRequestData
     * @return Itasrt Object
     */
    private Itasrt saveItasrt(GoodsInsertRequestData goodsInsertRequestData) {
        Itasrt itasrt = jpaItasrtRepository.findById(goodsInsertRequestData.getAssortId()).orElseGet(() -> new Itasrt(goodsInsertRequestData));
//        itasrt.setUpdDt(new Date());
        itasrt.setAssortNm(goodsInsertRequestData.getAssortNm());
        itasrt.setAssortColor(goodsInsertRequestData.getAssortColor());
        itasrt.setBrandId(goodsInsertRequestData.getBrandId());
        itasrt.setOrigin(goodsInsertRequestData.getOrigin());
        itasrt.setManufactureNm(goodsInsertRequestData.getManufactureNm());
        itasrt.setAssortModel(goodsInsertRequestData.getAssortModel());
        itasrt.setShortageYn(goodsInsertRequestData.getShortageYn());
        itasrt.setLocalPrice(goodsInsertRequestData.getLocalPrice());
        itasrt.setDeliPrice(goodsInsertRequestData.getDeliPrice());
        itasrt.setMargin(goodsInsertRequestData.getMargin());
        itasrt.setMdRrp(goodsInsertRequestData.getMdRrp());
        itasrt.setMdYear(goodsInsertRequestData.getMdYear());
        itasrt.setMdTax(goodsInsertRequestData.getMdTax());
        itasrt.setMdVatrate(goodsInsertRequestData.getMdVatrate());
        itasrt.setMdDiscountRate(goodsInsertRequestData.getMdDiscountRate());
        itasrt.setMdGoodsVatrate(goodsInsertRequestData.getMdGoodsVatrate());
        itasrt.setBuyWhere(goodsInsertRequestData.getBuyWhere());
        itasrt.setMdMargin(goodsInsertRequestData.getMdMargin());
        itasrt.setBuyExchangeRate(goodsInsertRequestData.getBuyExchangeRate());
        jpaItasrtRepository.save(itasrt);
        return itasrt;
    }

    /**
     * 21-04-28 Peca
     * 물품 정보 이력 insert, update
     * @param goodsInsertRequestData
     * @return Itasrn Object
     */
    private Itasrn saveItasrn(GoodsInsertRequestData goodsInsertRequestData){
//        ItasrnId itasrnId = new ItasrnId(goodsRequestData);
        Date effEndDt = null;
        try
        {
            effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        Itasrn itasrn = jpaItasrnRepository.findByAssortIdAndEffEndDt(goodsInsertRequestData.getAssortId(), effEndDt);
        if(itasrn == null){ // insert
            itasrn = new Itasrn(goodsInsertRequestData);
        }
        else{ // update
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, -1);
            itasrn.setEffEndDt(cal.getTime());
            // update 후 새 이력 insert
            Itasrn newItasrn = new Itasrn(itasrn);
            jpaItasrnRepository.save(newItasrn);
        }
        itasrn.setLocalSale(goodsInsertRequestData.getLocalSale());
        itasrn.setShortageYn(goodsInsertRequestData.getShortageYn());
        jpaItasrnRepository.save(itasrn);
        return itasrn;
    }

    /**
     * 21-04-28 Pecan
     * 메모(긴 글, 짧은 글) insert, update
     * @param goodsInsertRequestData
     * @return List<Itasrd>
     */
    private List<Itasrd> saveItasrd(GoodsInsertRequestData goodsInsertRequestData) {
        List<GoodsInsertRequestData.Description> descriptionList = goodsInsertRequestData.getDescription();
        List<Itasrd> itasrdList = new ArrayList<>();
        for (int i = 0; i < descriptionList.size() ; i++) {
            Itasrd itasrd = new Itasrd(goodsInsertRequestData);
            String seq = descriptionList.get(i).getSeq();
            if(seq == null || seq.trim().equals("")){ // insert
                seq = jpaItasrdRepository.findMaxSeqByAssortId(goodsInsertRequestData.getAssortId());
                if (seq == null || seq.trim().equals("")) { // insert -> 빈 테이블
                    seq = StringFactory.getFourStartCd();//fourStartCd;
                }
                else{ // insert -> 찬 테이블
                    seq = Utilities.plusOne(seq, 4);
                }
                itasrd.setSeq(seq);
            }
            else{ // update
                itasrd = jpaItasrdRepository.findByAssortIdAndSeq(goodsInsertRequestData.getAssortId(), seq);
            }
            itasrd.setOrdDetCd(descriptionList.get(i).getOrdDetCd());
            itasrd.setMemo(descriptionList.get(i).getMemo());
            itasrd.setTextHtmlGb(descriptionList.get(i).getTextHtmlGb());
            jpaItasrdRepository.save(itasrd);
            itasrdList.add(itasrd);
        }
        return itasrdList;
    }

    /**
     * 21-04-28 Pecan
     * 옵션 정보 insert, update
     * @param goodsInsertRequestData
     * @return List<Itvari>
     */
    private List<Itvari> saveItvariList(GoodsInsertRequestData goodsInsertRequestData) {
        List<GoodsInsertRequestData.Attributes> attributes = goodsInsertRequestData.getAttributes();
        List<Itvari> itvariList = new ArrayList<>();

        for(GoodsInsertRequestData.Attributes attribute : attributes){
            String seq = attribute.getSeq();
            Itvari itvari = new Itvari(goodsInsertRequestData);
            itvari.setAssortId(goodsInsertRequestData.getAssortId());
            if(seq == null || seq.trim().equals("")){ // seq가 존재하지 않는 경우 == 새로운 itvari INSERT -> seq max 값 따와야 함
                seq = jpaItvariRepository.findMaxSeqByAssortId(goodsInsertRequestData.getAssortId());
                if(seq == null){ // max값이 없음 -> 해당 assort id에서 첫 insert
                    seq = StringFactory.getFourStartCd();//fourStartCd;
                }
                else{ // max값 따옴 -> seq++
                    seq = Utilities.plusOne(seq, 4);
                }
                itvari.setSeq(seq);
            }
            else{ // 존재하는 경우 : itvari 객체가 존재함이 보장됨 -> update
                itvari = jpaItvariRepository.findByAssortIdAndSeq(goodsInsertRequestData.getAssortId(), seq);
            }
            itvari.setOptionNm(attribute.getValue());
            itvari.setOptionGb(attribute.getVariationGb());
            itvari.setVariationGb(attribute.getVariationGb());
            itvariList.add(itvari);
            jpaItvariRepository.save(itvari);
        }
        return itvariList;
    }

    /**
     * 21-04-28 Pecan
     * 아이템 정보 insert, update
     * @param goodsInsertRequestData
     * @return List<Ititmm>
     */
    private List<Ititmm> saveItemList(GoodsInsertRequestData goodsInsertRequestData) {
        List<GoodsInsertRequestData.Items> itemList = goodsInsertRequestData.getItems();
        List<Ititmm> ititmmList = new ArrayList<>();
        for(GoodsInsertRequestData.Items item : itemList){
            String itemId = item.getItemId(); // item id瑜? 媛앹껜媛? 媛뽮퀬 ?엳?쑝硫? 洹멸쾬?쓣 ?씠?슜
            Ititmm ititmm = new Ititmm(goodsInsertRequestData.getAssortId(), item);
            if(itemId == null || itemId.trim().equals("")){ // 媛앹껜?뿉 item id媛? ?뾾?쑝硫? jpa?뿉?꽌 max媛믪쓣 媛??졇?샂
                itemId = jpaItitmmRepository.findMaxItemIdByAssortId(goodsInsertRequestData.getAssortId());
                if(itemId == null || itemId.trim().equals("")){ // jpa?뿉?꽌 max媛믪쓣 媛??졇?솕?뒗?뜲 null?씠硫? ?빐?떦 assort id?뿉 item id媛? 議댁옱?븯吏? ?븡?쑝誘?濡? 珥덇린媛?(0001)?쓣 ?꽕?젙
                    itemId = StringFactory.getFourStartCd();
                }
                else { // jpa?뿉?꽌 max媛믪쓣 媛??졇?삩 寃쎌슦 1?쓣 ?뜑?븳 ?썑 item id濡? ?궪?쓬
                    itemId = Utilities.plusOne(itemId, 4);
                }
                ititmm.setItemId(itemId);
            }
            else{ // 媛앹껜?뿉 item id媛? ?엳?쑝硫? ?빐?떦 媛앹껜媛? ?씠誘? 議댁옱?븯誘?濡? 媛앹껜瑜? 媛??졇?샂 (update)
                ititmm = jpaItitmmRepository.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(), itemId);
            }
            String[] optionNmList = item.getValue().split(StringFactory.getSplitGb());
            // itvari?뿉?꽌 ?샃?뀡 ?삎吏? 李얠븘?삤湲?
            for(String optionNm : optionNmList){
                Itvari op = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), optionNm);
                String opGb = op.getOptionGb();
                if(opGb.equals(StringFactory.getGbOne())){ // optionGb?씠 01?씤 寃쎌슦
                    ititmm.setVariationGb1(opGb);
                    ititmm.setVariationSeq1(op.getSeq());
                }
                else if(opGb.equals(StringFactory.getGbTwo())){ // optionGb?씠 02?씤 寃쎌슦
                    ititmm.setVariationGb2(opGb);
                    ititmm.setVariationSeq2(op.getSeq());
                }
            }
            ititmm.setAddPrice(item.getAddPrice());
            ititmm.setShortYn(item.getShortYn());
            jpaItitmmRepository.save(ititmm);
            ititmmList.add(ititmm);
        }
        return ititmmList;
    }

    /**
     * 21-04-28 Pecan
     * ?븘?씠?뀥 ?젙蹂? ?씠?젰 insert, update
     * @param goodsInsertRequestData
     * @param ititmmList
     * @return List<Ititmd>
     */
    private List<Ititmd> saveItemHistoryList(GoodsInsertRequestData goodsInsertRequestData, List<Ititmm> ititmmList) {
        List<Ititmd> ititmdList = new ArrayList<>();
        Date effEndDt = null;
        for (int i = 0; i < ititmmList.size() ; i++) {
            try
            {
                effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 留덉?留? ?궇吏?(?뾾?쓣 寃쎌슦 9999-12-31 23:59:59?)
            }
            catch (Exception e){
                logger.debug(e.getMessage());
            }
            Ititmd ititmd = jpaItitmdRepository.findByAssortIdAndItemIdAndEffEndDt(goodsInsertRequestData.getAssortId(), ititmmList.get(i).getItemId() , effEndDt);
            if(ititmd == null){ // insert
                ititmd = new Ititmd(ititmmList.get(i));
            }
            else{ // update
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.SECOND, -1);
                ititmd.setEffEndDt(cal.getTime());
                // update ?썑 ?깉 ?씠?젰 insert
                Ititmd newItitmd = new Ititmd(ititmd);
                jpaItitmdRepository.save(newItitmd);
//            saveItasrn(goodsRequestData);
            }
            ititmd.setShortYn(ititmmList.get(i).getShortYn());
            jpaItitmdRepository.save(ititmd);
        }

        return ititmdList;
    }

    public void updateById(String goodsId, Itasrt goods) {
        Optional<Itasrt> e = jpaItasrtRepository.findById(goodsId);
        if (e.isPresent()) {
            e.get().setAssortId(goods.getAssortId());
            e.get().setAssortNm(goods.getAssortNm());
            jpaItasrtRepository.save(goods);
        }
    }

    /**
     * 21-04-29 Pecan
     * assortId瑜? ?넻?빐 detail ?럹?씠吏?瑜? 援ъ꽦?븯?뒗 ?젙蹂대?? 諛섑솚?븯?뒗 ?븿?닔
     * @param assrotId
     * @return GoodsResponseData
     */
    public GoodsSelectDetailResponseData getGoodsDetailPage(String assrotId) {
       // Itasrt itasrt = jpaItasrtRepository.findById(assrotId).orElseGet(() -> null);
    	
    	
    	 Itasrt itasrt = jpaItasrtRepository.findById(assrotId).orElseThrow(() -> new ResourceNotFoundException());
    	
        GoodsSelectDetailResponseData goodsSelectDetailResponseData = new GoodsSelectDetailResponseData(itasrt);
        List<GoodsSelectDetailResponseData.Description> descriptions = makeDescriptions(itasrt.getItasrdList());
        List<GoodsSelectDetailResponseData.Attributes> attributesList = makeAttributesList(itasrt.getItvariList());
        List<GoodsSelectDetailResponseData.Items> itemsList = makeItemsList(itasrt.getItitmmList());
        goodsSelectDetailResponseData.setDescription(descriptions);
        goodsSelectDetailResponseData.setAttributes(attributesList);
        goodsSelectDetailResponseData.setItems(itemsList);
        return goodsSelectDetailResponseData;
    }
    // ititmm -> items ?삎?깭濡? 諛붽퓭二쇰뒗 ?븿?닔
    private List<GoodsSelectDetailResponseData.Items> makeItemsList(List<Ititmm> ititmmList) {
        List<GoodsSelectDetailResponseData.Items> itemsList = new ArrayList<>();
        for(Ititmm ititmm : ititmmList){
            GoodsSelectDetailResponseData.Items item = new GoodsSelectDetailResponseData.Items();
            item.setItemId(ititmm.getItemId());
            
            
            String option2NM="";
            if(ititmm.getItvari2()!=null) {
            	option2NM = "^|^"+ititmm.getItvari2().getOptionNm();
            }else {
            	option2NM ="";
            }
            
            
            item.setValue(ititmm.getItvari1().getOptionNm()+option2NM);
            item.setAddPrice(ititmm.getAddPrice());
            item.setShortYn(ititmm.getShortYn());
            itemsList.add(item);
        }
        return itemsList;
    }

    // itvari -> attributes ?삎?깭濡? 諛붽퓭二쇰뒗 ?븿?닔
    private List<GoodsSelectDetailResponseData.Attributes> makeAttributesList(List<Itvari> itvariList) {
        List<GoodsSelectDetailResponseData.Attributes> attributesList = new ArrayList<>();
        for(Itvari itvari : itvariList){
            GoodsSelectDetailResponseData.Attributes attr = new GoodsSelectDetailResponseData.Attributes();
            attr.setSeq(itvari.getSeq());
            attr.setValue(itvari.getOptionNm());
            attr.setVariationGb(itvari.getOptionGb());
            attributesList.add(attr);
        }
        return attributesList;
    }

    // itasrd -> description ?삎?깭濡? 諛붽퓭二쇰뒗 ?븿?닔
    private List<GoodsSelectDetailResponseData.Description> makeDescriptions(List<Itasrd> itasrdList) {
        List<GoodsSelectDetailResponseData.Description> descriptionList = new ArrayList<>();
        for(Itasrd itasrd : itasrdList){
            GoodsSelectDetailResponseData.Description desc = new GoodsSelectDetailResponseData.Description();
            desc.setSeq(itasrd.getSeq());
            desc.setOrdDetCd(itasrd.getOrdDetCd());
            desc.setTextHtmlGb(itasrd.getTextHtmlGb());
            desc.setMemo(itasrd.getMemo());
            descriptionList.add(desc);
        }
        return descriptionList;
    }

    /**
     * 21-05-10 Pecan
     * brandId, dispCategoryId, regDt, shortageYn, (?씠?긽 itasrt) dispCategoryId(itcatg), brandId(itbrnd) 濡? list 紐⑸줉 媛??졇?삤?뒗 ?븿?닔
     * @param shortageYn, RegDtBegin, regDtEnd
     * @return GoodsSelectListResponseData
     */
    public GoodsSelectListResponseData getGoodsList(String shortageYn, Date regDtBegin, Date regDtEnd) {
        TypedQuery<Itasrt> query =
                em.createQuery("select t from Itasrt t " +
                                "join fetch t.itbrnd b " +
                                "join fetch t.itcatg c " +
                                "where t.regDt " +
                                "between ?1 " +
                                "and ?2 " +
                                "and t.shortageYn = ?3 "
                        , Itasrt.class);
        query.setParameter(1, regDtBegin)
                .setParameter(2, regDtEnd)
                .setParameter(3, shortageYn);
        List<Itasrt> itasrtList = query.getResultList();
        List<GoodsSelectListResponseData.Goods> goodsList = new ArrayList<>();
        for(Itasrt itasrt : itasrtList){
            GoodsSelectListResponseData.Goods goods = new GoodsSelectListResponseData.Goods(itasrt);
            goodsList.add(goods);
        }
        GoodsSelectListResponseData goodsSelectListResponseData = new GoodsSelectListResponseData(goodsList);
        return goodsSelectListResponseData;
    }

    
    
    
    
//    private GoodsInsertResponseData makeGoodsSelectListResponseData(List<Itasrt> goodsList) {
//        return null;
//    }
    
    @Transactional
    public Itaimg saveItaimg(String imageGb,FileVo f) {
    	Itaimg ii =new Itaimg(imageGb,f); 
    	  jpaItaimgRepository.save(ii);
    	
    	return ii;
    	
    }
    
   
    public Itaimg getItaimg(Long uid) {
    	Itaimg r = jpaItaimgRepository.findById(uid) .orElse(null);
    	
    	
    	return r;
    	
    }
    
    @Transactional
    public void deleteItaimg(Itaimg ii) {
    	
     jpaItaimgRepository.delete(ii);
    	
    	
    
    	
    }
     

    /**
     * Table 珥덇린?솕 ?븿?닔
     */
    public void initTables(){
        Optional<SequenceData> op = jpaSequenceDataRepository.findById("seq_ITASRT");
        SequenceData seq = op.get();
        jpaItasrtRepository.deleteAll();
        jpaItasrdRepository.deleteAll();
        jpaItasrnRepository.deleteAll();
        jpaItitmmRepository.deleteAll();
        jpaItitmdRepository.deleteAll();
        jpaItvariRepository.deleteAll();
        seq.setSequenceCurValue(StringFactory.getStrZero());
        jpaSequenceDataRepository.save(seq);
    }

}
