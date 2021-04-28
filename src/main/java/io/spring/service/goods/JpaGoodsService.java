package io.spring.service.goods;

import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.goods.*;
import io.spring.model.common.entity.SequenceData;
import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.GoodsResponseData;
import io.spring.model.goods.entity.*;
import org.flywaydb.core.internal.util.StringUtils;
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

    private final String threeStartCd = "001";
    private final String fourStartCd = "0001";
//    private final String nineStartCd = "000000001";
    private final String gbOne = "01";
    private final String gbTwo = "02";
    private final String splitGb = "\\^\\|\\^";

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
     * @param goodsRequestData
     * @return GoodsResponseData
     */
    @Transactional
    public GoodsResponseData sequenceInsertOrUpdateGoods(GoodsRequestData goodsRequestData){
        // itasrt에 goods 정보 저장
        Itasrt itasrt = this.saveItasrt(goodsRequestData);
        // itsrn에 goods 이력 저장
        Itasrn itasrn = this.saveItasrn(goodsRequestData);
        // itasrd에 문구 저장
        List<Itasrd> itasrd = this.saveItasrd(goodsRequestData);
        // itvari에 assort_id별 옵션요소 저장(색상, 사이즈)
        List<Itvari> itvariList = this.saveItvariList(goodsRequestData);
        // ititmm에 assort_id별 item 저장
        List<Ititmm> ititmmList = this.saveItemList(goodsRequestData);
        // ititmd에 item 이력 저장
        List<Ititmd> ititmdList = this.saveItemOptionList(goodsRequestData, ititmmList);

        List<GoodsResponseData.Attributes> attributesList = makeGoodsResponseAttributes(goodsRequestData.getAssortId(), itvariList);
        List<GoodsResponseData.Items> itemsList = makeGoodsResponseItems(goodsRequestData.getAssortId(), ititmmList);
        return makeGoodsResponseData(goodsRequestData, attributesList, itemsList);
    }

    private List<GoodsResponseData.Attributes> makeGoodsResponseAttributes(String assortId, List<Itvari> itvariList){
        return null;
    }

    private List<GoodsResponseData.Items> makeGoodsResponseItems(String assortId, List<Ititmm> ititmm){
        return null;
    }

    private GoodsResponseData makeGoodsResponseData(GoodsRequestData goodsRequestData, List<GoodsResponseData.Attributes> attributesList, List<GoodsResponseData.Items> itemsList){
        GoodsResponseData goodsResponseData = GoodsResponseData.builder().goodsRequestData(goodsRequestData)
                .attributesList(attributesList).itemsList(itemsList).build();

        return goodsResponseData;
    }

    public void deleteById(String goodsId) {
        jpaItasrtRepository.deleteById(goodsId);
    }

//    @Transactional
    private Itasrt saveItasrt(GoodsRequestData goodsRequestData) {
        Itasrt itasrt = jpaItasrtRepository.findById(goodsRequestData.getAssortId()).orElseGet(() -> new Itasrt(goodsRequestData));
//        itasrt.setUpdDt(new Date());
        itasrt.setAssortNm(goodsRequestData.getAssortNm());
        itasrt.setAssortColor(goodsRequestData.getAssortColor());
        itasrt.setBrandId(goodsRequestData.getBrandId());
        itasrt.setOrigin(goodsRequestData.getOrigin());
        itasrt.setManufactureNm(goodsRequestData.getManufactureNm());
        itasrt.setAssortModel(goodsRequestData.getAssortModel());
        itasrt.setShortageYn(goodsRequestData.getShortageYn());
        itasrt.setLocalPrice(goodsRequestData.getLocalPrice());
        itasrt.setDeliPrice(goodsRequestData.getDeliPrice());
        itasrt.setMargin(goodsRequestData.getMargin());
        itasrt.setMdRrp(goodsRequestData.getMdRrp());
        itasrt.setMdYear(goodsRequestData.getMdYear());
        itasrt.setMdTax(goodsRequestData.getMdTax());
        itasrt.setMdVatrate(goodsRequestData.getMdVatrate());
        itasrt.setMdDiscountRate(goodsRequestData.getMdDiscountRate());
        itasrt.setMdGoodsVatrate(goodsRequestData.getMdGoodsVatrate());
        itasrt.setBuyWhere(goodsRequestData.getBuyWhere());
        itasrt.setMdMargin(goodsRequestData.getMdMargin());
        itasrt.setBuyExchangeRate(goodsRequestData.getBuyExchangeRate());
        jpaItasrtRepository.save(itasrt);
        return itasrt;
    }

    private Itasrn saveItasrn(GoodsRequestData goodsRequestData){
//        ItasrnId itasrnId = new ItasrnId(goodsRequestData);
        Date effEndDt = null;
        try
        {
            effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59"); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        Itasrn itasrn = jpaItasrnRepository.findByAssortIdAndEffEndDt(goodsRequestData.getAssortId(), effEndDt);
        if(itasrn == null){ // insert
            itasrn = new Itasrn(goodsRequestData);
        }
        else{ // update
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, -1);
            itasrn.setEffEndDt(cal.getTime());
//            saveItasrn(goodsRequestData);
        }
        itasrn.setLocalSale(goodsRequestData.getLocalSale());
        itasrn.setShortageYn(goodsRequestData.getShortageYn());
        jpaItasrnRepository.save(itasrn);
        return itasrn;
    }

    // 메모 저장 테이블
    private List<Itasrd> saveItasrd(GoodsRequestData goodsRequestData) {
        List<GoodsRequestData.Description> descriptionList = goodsRequestData.getDescription();
        List<Itasrd> itasrdList = new ArrayList<>();
        for (int i = 0; i < descriptionList.size() ; i++) {
            Itasrd itasrd = new Itasrd(goodsRequestData);
            String seq = (jpaItasrdRepository.findMaxSeqByAssortId(goodsRequestData.getAssortId()));
            if(seq == null || seq.trim().equals("")){
                seq = fourStartCd;
            }
            else{
                seq = plusOne(seq, 4);
            }
            itasrd.setSeq(seq);
            itasrd.setOrdDetCd(descriptionList.get(i).getOrdDetCd());
            itasrd.setMemo(descriptionList.get(i).getMemo());
            itasrd.setTextHtmlGb(descriptionList.get(i).getTextHtmlGb());
            jpaItasrdRepository.save(itasrd);
            itasrdList.add(itasrd);
        }
        return itasrdList;
    }

    private List<Itvari> saveItvariList(GoodsRequestData goodsRequestData) {
        List<GoodsRequestData.Attributes> attributes = goodsRequestData.getAttributes();
        List<Itvari> itvariList = new ArrayList<>();

        for(GoodsRequestData.Attributes attribute : attributes){
            String seq = (attribute.getSeq());
            if(seq == null || seq.trim().equals("")){ // seq가 존재하지 않는 경우 == 새로운 itvari INSERT -> seq max 값 따와야 함
                String maxSeq = plusOne(jpaItvariRepository.findMaxSeqByAssortId(goodsRequestData.getAssortId()), 3);
                seq = maxSeq;
            }
            Itvari itvari = new Itvari(goodsRequestData);
            itvari.setAssortId(goodsRequestData.getAssortId());
            itvari.setSeq(seq);
            itvari.setOptionNm(attribute.getValue());
            itvari.setOptionGb(attribute.getVariationGb());
            itvari.setVariationGb(attribute.getVariationGb());
            itvariList.add(itvari);
            jpaItvariRepository.save(itvari);
        }
        return itvariList;
    }

    private List<Ititmm> saveItemList(GoodsRequestData goodsRequestData) {
        List<GoodsRequestData.Items> itemList = goodsRequestData.getItems();
        List<Ititmm> ititmmList = new ArrayList<>();
        for(GoodsRequestData.Items item : itemList){
            String itemId = item.getItemId(); // item id를 객체가 갖고 있으면 그것을 이용
            if(itemId == null || itemId.trim().equals("")){ // 객체에 item id가 없으면 jpa에서 max값을 가져옴
                itemId = jpaItitmmRepository.findMaxItemIdByAssortId(goodsRequestData.getAssortId());
            }
            if(itemId == null || itemId.trim().equals("")){ // jpa에서 max값을 가져왔는데 null이면 해당 assort id에 item id가 존재하지 않으므로 초기값(0001)을 설정
                itemId = fourStartCd;
            }
            else { // jpa에서 max값을 가져온 경우 1을 더한 후 item id로 삼음
                itemId = plusOne(jpaItitmmRepository.findMaxItemIdByAssortId(goodsRequestData.getAssortId()), 4);
            }
            String[] optionNmList = item.getValue().split(splitGb);
            Ititmm ititmm = new Ititmm(goodsRequestData.getAssortId(), item);
            // itvari에서 옵션 형질 찾아오기
            for(String optionNm : optionNmList){
                Itvari op = jpaItvariRepository.findByAssortIdAndOptionNm(goodsRequestData.getAssortId(), optionNm);
                String opGb = op.getOptionGb();
                if(opGb.equals(gbOne)){ // optionGb이 01인 경우 
                    ititmm.setVariationGb1(opGb);
                    ititmm.setVariationSeq1(op.getSeq());
                }
                else if(opGb.equals(gbTwo)){ // optionGb이 02인 경우
                    ititmm.setVariationGb2(opGb);
                    ititmm.setVariationSeq2(op.getSeq());
                }
            }
            ititmm.setItemId(itemId);
            ititmm.setAddPrice(item.getAddPrice());
            ititmm.setShortYn(item.getShortYn());
            jpaItitmmRepository.save(ititmm);
            ititmmList.add(ititmm);
        }
        return ititmmList;
    }

    private List<Ititmd> saveItemOptionList(GoodsRequestData goodsRequestData, List<Ititmm> ititmmList) {
        List<Ititmd> ititmdList = new ArrayList<>();
        for (Ititmm item: ititmmList) {
            Ititmd ititmd = jpaItitmdRepository.findByItemId(item.getItemId());//.orElseGet(()->null);//new Ititmd(goodsRequestData, item);
            if(ititmd == null){
                ititmd = new Ititmd(item);
            }
            else{
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.SECOND, -1);
                ititmd.setEffEndDt(cal.getTime());
            }
            ititmd.setItemId(item.getItemId());
//            ititmd.setEffStaDt(new Date()); // 임시로..
//            ititmd.setEffEndDt(new Date());
            ititmd.setShortYn(item.getShortYn());
            ititmd.setUpdDt(new Date());
            ititmdList.add(ititmd);
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

    // table 초기화용 함수(test할 때 편하려고..)
    public void initTables(){
        Optional<SequenceData> op = jpaSequenceDataRepository.findById("seq_ITASRT");
        SequenceData seq = op.get();
        jpaItasrtRepository.deleteAll();
        jpaItasrdRepository.deleteAll();
        jpaItasrnRepository.deleteAll();
        jpaItitmmRepository.deleteAll();
        jpaItitmdRepository.deleteAll();
        jpaItvariRepository.deleteAll();
        seq.setSequenceCurValue("0");
        jpaSequenceDataRepository.save(seq);
    }

    // 유틸 함수 : "009"를 받아 정수화해서 1을 더한 후 "010"으로 return
    private String plusOne(String calcNeedStringNumber, int length){ // 들어온 string의 숫자는 정수여야 함
        if(calcNeedStringNumber == null){
            return null;
        }
        String calcRes = StringUtils.leftPad(Long.toString((long)Double.parseDouble(calcNeedStringNumber) + 1), length, '0');
        return calcRes;
    }
}
