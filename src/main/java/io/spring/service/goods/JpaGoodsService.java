package io.spring.service.goods;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.goods.*;
import io.spring.model.common.entity.SequenceData;
import io.spring.model.goods.entity.*;
import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.goods.response.GoodsGetDetailResponseData;
import io.spring.model.goods.response.GoodsInsertResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
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
        // itasrt에 goods 정보 저장
        Itasrt itasrt = this.saveItasrt(goodsInsertRequestData);
        // itsrn에 goods 이력 저장
        Itasrn itasrn = this.saveItasrn(goodsInsertRequestData);
        // itasrd에 문구 저장
        List<Itasrd> itasrd = this.saveItasrd(goodsInsertRequestData);
        // itvari에 assort_id별 옵션요소 저장(색상, 사이즈)
        List<Itvari> itvariList = this.saveItvariList(goodsInsertRequestData);
        // ititmm에 assort_id별 item 저장
        List<Ititmm> ititmmList = this.saveItemList(goodsInsertRequestData);
        // ititmd에 item 이력 저장
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
            effEndDt = new SimpleDateFormat(StringFactory.getDateFormat()).parse(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
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
            String itemId = item.getItemId(); // item id를 객체가 갖고 있으면 그것을 이용
            Ititmm ititmm = new Ititmm(goodsInsertRequestData.getAssortId(), item);
            if(itemId == null || itemId.trim().equals("")){ // 객체에 item id가 없으면 jpa에서 max값을 가져옴
                itemId = jpaItitmmRepository.findMaxItemIdByAssortId(goodsInsertRequestData.getAssortId());
                if(itemId == null || itemId.trim().equals("")){ // jpa에서 max값을 가져왔는데 null이면 해당 assort id에 item id가 존재하지 않으므로 초기값(0001)을 설정
                    itemId = StringFactory.getFourStartCd();
                }
                else { // jpa에서 max값을 가져온 경우 1을 더한 후 item id로 삼음
                    itemId = Utilities.plusOne(itemId, 4);
                }
                ititmm.setItemId(itemId);
            }
            else{ // 객체에 item id가 있으면 해당 객체가 이미 존재하므로 객체를 가져옴 (update)
                ititmm = jpaItitmmRepository.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(), itemId);
            }
            String[] optionNmList = item.getValue().split(StringFactory.getSplitGb());
            // itvari에서 옵션 형질 찾아오기
            for(String optionNm : optionNmList){
                Itvari op = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), optionNm);
                String opGb = op.getOptionGb();
                if(opGb.equals(StringFactory.getGbOne())){ // optionGb이 01인 경우
                    ititmm.setVariationGb1(opGb);
                    ititmm.setVariationSeq1(op.getSeq());
                }
                else if(opGb.equals(StringFactory.getGbTwo())){ // optionGb이 02인 경우
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
     * 아이템 정보 이력 insert, update
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
                effEndDt = new SimpleDateFormat(StringFactory.getDateFormat()).parse(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
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
                // update 후 새 이력 insert
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
     * assortId를 통해 detail 페이지를 구성하는 정보를 반환하는 함수
     * @param assrotId
     * @return GoodsResponseData
     */
    public GoodsGetDetailResponseData getGoodsDetailPage(String assrotId) {
        Itasrt itasrt = jpaItasrtRepository.findById(assrotId).orElseGet(() -> null);
        GoodsGetDetailResponseData goodsGetDetailResponseData = new GoodsGetDetailResponseData(itasrt);
        List<GoodsGetDetailResponseData.Description> descriptions = makeDescriptions(itasrt.getItasrdList());
        List<GoodsGetDetailResponseData.Attributes> attributesList = makeAttributesList(itasrt.getItvariList());
        List<GoodsGetDetailResponseData.Items> itemsList = makeItemsList(itasrt.getItitmmList());
        goodsGetDetailResponseData.setDescription(descriptions);
        goodsGetDetailResponseData.setAttributes(attributesList);
        goodsGetDetailResponseData.setItems(itemsList);
        return goodsGetDetailResponseData;
    }
    // ititmm -> items 형태로 바꿔주는 함수
    private List<GoodsGetDetailResponseData.Items> makeItemsList(List<Ititmm> ititmmList) {
        List<GoodsGetDetailResponseData.Items> itemsList = new ArrayList<>();
        for(Ititmm ititmm : ititmmList){
            GoodsGetDetailResponseData.Items item = new GoodsGetDetailResponseData.Items();
            item.setItemId(ititmm.getItemId());
            item.setValue(ititmm.getItvari1().getOptionNm()+"^|^"+ititmm.getItvari2().getOptionNm());
            item.setAddPrice(ititmm.getAddPrice());
            item.setShortYn(ititmm.getShortYn());
            itemsList.add(item);
        }
        return itemsList;
    }

    // itvari -> attributes 형태로 바꿔주는 함수
    private List<GoodsGetDetailResponseData.Attributes> makeAttributesList(List<Itvari> itvariList) {
        List<GoodsGetDetailResponseData.Attributes> attributesList = new ArrayList<>();
        for(Itvari itvari : itvariList){
            GoodsGetDetailResponseData.Attributes attr = new GoodsGetDetailResponseData.Attributes();
            attr.setSeq(itvari.getSeq());
            attr.setValue(itvari.getOptionNm());
            attr.setVariationGb(itvari.getOptionGb());
            attributesList.add(attr);
        }
        return attributesList;
    }

    // itasrd -> description 형태로 바꿔주는 함수
    private List<GoodsGetDetailResponseData.Description> makeDescriptions(List<Itasrd> itasrdList) {
        List<GoodsGetDetailResponseData.Description> descriptionList = new ArrayList<>();
        for(Itasrd itasrd : itasrdList){
            GoodsGetDetailResponseData.Description desc = new GoodsGetDetailResponseData.Description();
            desc.setSeq(itasrd.getSeq());
            desc.setOrdDetCd(itasrd.getOrdDetCd());
            desc.setTextHtmlGb(itasrd.getTextHtmlGb());
            desc.setMemo(itasrd.getMemo());
            descriptionList.add(desc);
        }
        return descriptionList;
    }

    /**
     * 21-04-29 Pecan
     * brandId, dispCategoryId, regDt, shortageYn, (이상 itasrt) dispCategoryId(itcatg), brandId(itbrnd) 로 list 목록 가져오는 함수
     * @param shortageYn, RegDtBegin, regDtEnd
     * @return GoodsResponseData
     */
    public GoodsInsertResponseData getGoodsList(String shortageYn, Date regDtBegin, Date regDtEnd) {
        List<Object[]> goodsList = jpaItasrtRepository.getGoodsList(shortageYn, regDtBegin, regDtEnd);
        for (Object[] goods : goodsList){
            for (int i = 0; i < goods.length; i++) {
                System.out.print(" " + goods[i] + " ");
            }

            System.out.println("");
        }
        GoodsInsertResponseData goodsInsertResponseData = null;//makeGoodsSelectListResponseData(goodsList);
        return goodsInsertResponseData;
    }

//    private GoodsInsertResponseData makeGoodsSelectListResponseData(List<Itasrt> goodsList) {
//        return null;
//    }

    /**
     * Table 초기화 함수
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
