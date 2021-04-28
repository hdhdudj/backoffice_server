package io.spring.service.goods;

import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.goods.*;
import io.spring.model.common.entity.SequenceData;
import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.GoodsResponseData;
import io.spring.model.goods.entity.*;
import io.spring.model.goods.idclass.ItitmdId;
import io.spring.model.goods.idclass.ItitmmId;
import io.spring.model.goods.idclass.ItvariId;
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

    private final String colorGb = "01";
    private final String sizeGb = "02";
    private final String threeStartCd = "001";
    private final String fourStartCd = "0001";
//    private final String nineStartCd = "000000001";
    private final String gbOne = "01";
    private final String gbTwo = "02";




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
        // itasrd에 연관 정보 저장
        Itasrd itasrd = this.saveItasrd(goodsRequestData);
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
    private Itasrd saveItasrd(GoodsRequestData goodsRequestData) {
        Itasrd itasrd = jpaItasrdRepository.findByAssortId(goodsRequestData.getAssortId());
        if(itasrd == null){itasrd = new Itasrd(goodsRequestData);}
        String seq = plusOne(jpaItasrdRepository.findMaxSeqByAssortId(goodsRequestData.getAssortId())); //myBatisGoodsDao.selectMaxSeqItasrd(goodsRequestData);
        if(seq == null){
            seq = fourStartCd;
        }
        else {
            seq = StringUtils.leftPad(Integer.toString((int)Double.parseDouble(seq)), 4, '0');
        }
        itasrd.setSeq(seq);
        if(goodsRequestData.getShortDesc() != null && !goodsRequestData.getShortDesc().getValue().trim().equals("")){
            itasrd.setMemo(goodsRequestData.getShortDesc().getValue().trim());
            itasrd.setOrdDetCd(gbOne);
            itasrd.setTextHtmlGb(gbTwo);
            jpaItasrdRepository.save(itasrd);
            itasrd = new Itasrd(goodsRequestData);
            itasrd.setSeq(StringUtils.leftPad(plusOne(seq), 4 ,'0'));
        }
        if(goodsRequestData.getLongDesc() != null && !goodsRequestData.getLongDesc().getValue().trim().equals("")){
            itasrd.setMemo(goodsRequestData.getLongDesc().getValue().trim());
            itasrd.setOrdDetCd(gbTwo);
            itasrd.setTextHtmlGb(gbOne);
            jpaItasrdRepository.save(itasrd);
        }

        return itasrd;
    }

    private List<Itvari> saveItvariList(GoodsRequestData goodsRequestData) {
        List<GoodsRequestData.Attributes> attributes = goodsRequestData.getAttributes();
        List<Itvari> itvariList = new ArrayList<>();
        for(GoodsRequestData.Attributes item : attributes){
            List<GoodsRequestData.SeqAndValue> colors = item.getColor();
            List<GoodsRequestData.SeqAndValue> sizes = item.getSize();
            if(colors != null){
                for (int j = 0; j < colors.size() ; j++) {
                    String seq = colors.get(j).getSeq();
                    if(seq == null || seq.equals("")){
                        String maxSeq = plusOne(jpaItvariRepository.findMaxSeqByAssortId(goodsRequestData.getAssortId())); //myBatisGoodsDao.selectMaxSeqItvari(goodsRequestData)
                        if(maxSeq != null){
                            maxSeq = Long.toString((long)Double.parseDouble(plusOne(jpaItvariRepository.findMaxSeqByAssortId(goodsRequestData.getAssortId())))); //myBatisGoodsDao.selectMaxSeqItvari(goodsRequestData)
                        }
                        logger.debug(maxSeq);
                        String seqRes = maxSeq == null? threeStartCd : StringUtils.leftPad(maxSeq, 3, '0');
                        logger.debug(StringUtils.leftPad(seqRes, 3, '0'));
                        seq = seqRes;
                    }
                    Itvari itvari = jpaItvariRepository.findById(new ItvariId(goodsRequestData.getAssortId(), seq)).orElseGet(()->new Itvari(goodsRequestData));

                    itvari.setSeq(seq);
                    itvari.setOptionNm(colors.get(j).getValue());
                    itvari.setOptionGb(colorGb);
                    itvari.setUpdDt(new Date());
                    jpaItvariRepository.save(itvari);
                    itvariList.add(itvari);
                }
            }
            else if(sizes != null){
                for (int i = 0; i < sizes.size() ; i++) {
                    Itvari itvari = new Itvari(goodsRequestData);
                    String seq = sizes.get(i).getSeq();
                    if(seq == null || seq.equals("")){
                        String maxSeq = Long.toString((long)Double.parseDouble(plusOne(jpaItvariRepository.findMaxSeqByAssortId(goodsRequestData.getAssortId()))));//myBatisGoodsDao.selectMaxSeqItvari(goodsRequestData)
                        String seqRes = maxSeq == null? threeStartCd : StringUtils.leftPad(maxSeq, 3, '0');
                        seq = seqRes;
                    }
                    itvari.setSeq(seq);
                    itvari.setOptionNm(sizes.get(i).getValue());
                    itvari.setOptionGb(sizeGb);
                    itvari.setUpdDt(new Date());
                    jpaItvariRepository.save(itvari);
                    itvariList.add(itvari);
                }
            }
        }

        return itvariList;
    }

    private List<Ititmm> saveItemList(GoodsRequestData goodsRequestData) {
        List<GoodsRequestData.Items> itemList = goodsRequestData.getItems();
        List<Ititmm> itemsList = new ArrayList<>();
        for (GoodsRequestData.Items item : itemList) {
            String color = item.getColor();
            String size = item.getSize();
            item.setAssortId(goodsRequestData.getAssortId());
            Ititmm ititmm = null;
            if(color != null){ // color 요소가 있는 경우
                item.setOptionNm(color);
                Itvari itvari = jpaItvariRepository.findByAssortIdAndOptionNm(goodsRequestData.getAssortId(), item.getOptionNm());//myBatisGoodsDao.selectOneSeqOptionGb(item);
                ititmm = jpaItitmmRepository.findById(new ItitmmId(goodsRequestData.getAssortId(), itvari.getSeq())).orElseGet(()->new Ititmm(goodsRequestData.getAssortId(), item));
                ititmm.setVariationGb1(colorGb);
                ititmm.setVariationSeq1((String)itvari.getSeq());
            }
            if(size != null){ // size 요소가 있는 경우
                item.setOptionNm(size);
                Itvari itvari = jpaItvariRepository.findByAssortIdAndOptionNm(goodsRequestData.getAssortId(), item.getOptionNm());//myBatisGoodsDao.selectOneSeqOptionGb(item);
                ititmm = jpaItitmmRepository.findById(new ItitmmId(goodsRequestData.getAssortId(), itvari.getSeq())).orElseGet(()->new Ititmm(goodsRequestData.getAssortId(), item));
                ititmm.setVariationGb2(sizeGb);
                ititmm.setVariationSeq2(itvari.getSeq());
            }
//            System.out.println("ㅡㅡㅡㅡㅡㅡ"+jpaItitmmRepository.findMaxItemIdByAssortId(item.getAssortId()));
            String startItemId = plusOne(jpaItitmmRepository.findMaxItemIdByAssortId(goodsRequestData.getAssortId()));//myBatisGoodsDao.selectMaxItemIdItitmm(goodsRequestData);

            if(startItemId == null || startItemId.equals("")){
                startItemId = fourStartCd;
            }
            else{
                String maxSeq = Integer.toString((int)Double.parseDouble(startItemId));
                String seqRes = maxSeq == null? threeStartCd : StringUtils.leftPad(maxSeq, 4, '0');
                startItemId = seqRes;
            }
            if(ititmm == null){
                ititmm = new Ititmm(goodsRequestData.getAssortId(), item);
            }
            ititmm.setItemId(startItemId);
            ititmm.setUpdDt(new Date());
            jpaItitmmRepository.save(ititmm);
            itemsList.add(ititmm);
        }

        return itemsList;
    }

    private List<Ititmd> saveItemOptionList(GoodsRequestData goodsRequestData, List<Ititmm> ititmmList) {
        List<Ititmd> ititmdList = new ArrayList<>();
        for (Ititmm item: ititmmList) {
            Ititmd ititmd = jpaItitmdRepository.findById(new ItitmdId(goodsRequestData, item.getItemId())).orElseGet(()->null);//new Ititmd(goodsRequestData, item);
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
    private String plusOne(String calcNeedStringNumber){ // 들어온 string의 숫자는 정수여야 함
        if(calcNeedStringNumber == null){
            return null;
        }
        String calcRes = StringUtils.leftPad(Long.toString((long)Double.parseDouble(calcNeedStringNumber) + 1), 4, '0');
        return calcRes;
    }
}
