package io.spring.service.goods;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.goods.MyBatisGoodsDao;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class JpaGoodsService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String colorGb = "01";
    private final String sizeGb = "02";
    private final String threeStartCd = "001";
    private final String fourStartCd = "0001";
    private final String nineStartCd = "000000001";




    @Autowired
    private JpaItasrtRepository jpaItasrtRepository;
    @Autowired
    private JpaItasrnRepository jpaItasrnRepository;
    @Autowired
    private JpaItvariRepository jpaItvariRepository;
    @Autowired
    private MyBatisCommonDao myBatisCommonDao;
    @Autowired
    private MyBatisGoodsDao myBatisGoodsDao;
    @Autowired
    private JpaItasrdRepository jpaItasrdRepository;
    @Autowired
    private JpaItitmmRepository jpaItitmmRepository;
    @Autowired
    private JpaSequenceDataRepository jpaSequenceDataRepository;

    public List<Itasrt> findAll() {
        List<Itasrt> goods = new ArrayList<>();
        jpaItasrtRepository.findAll().forEach(e -> goods.add(e));
        return goods;
    }

    public Optional<Itasrt> findById(Long goodsId) {
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
    public GoodsResponseData sequenceInsertGoods(GoodsRequestData goodsRequestData){
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

    public void deleteById(Long goodsId) {
        jpaItasrtRepository.deleteById(goodsId);
    }

//    @Transactional
    private Itasrt saveItasrt(GoodsRequestData goodsRequestData) {
        Itasrt itasrt = new Itasrt(goodsRequestData);
//        HashMap<String, Object> arr = new HashMap<String, Object>();
//
//        String assortId = goodsRequestData.getAssortId();
//        if(assortId == null || assortId.trim().equals("")){ // 입력값에 assort id가 없는 경우 (신규입력)
//            arr.put(seqNameStr, seqItasrtStr);
//            HashMap<String, Object> x1 = myBatisCommonDao.getSequence(arr); // max + 1 해서 옴
//            logger.debug("nextVal : ", x1.get(nextvalStr));
//            assortId = StringUtils.leftPad(Long.toString((long)x1.get(nextvalStr)), 9, '0');
//            itasrt.setAssortId(assortId);
//            goodsRequestData.setAssortId(assortId);
//        }
//        else{ // 입력값에 assort id가 있는 경우 (기존 정보 수정)
//            // 입력값에 assort_id가 없어서 새로 채번하는 경우 sequence_data table의 sequence_cur_value도 update 필요 (필요없는 기능, 삭제)
//            Optional<SequenceData> returnSequenceData = jpaSequenceDataRepository.findById(seqItasrtStr);
//            SequenceData sequenceData = returnSequenceData.get();
//            sequenceData.setSequenceCurValue(assortId);
////            jpaSequenceDataRepository.save(sequenceData);
//        }

        jpaItasrtRepository.save(itasrt);

        return itasrt;
    }

    private Itasrn saveItasrn(GoodsRequestData goodsRequestData){
        Itasrn itasrn = new Itasrn(goodsRequestData);
        jpaItasrnRepository.save(itasrn);
        return itasrn;
    }

    private Itasrd saveItasrd(GoodsRequestData goodsRequestData) {
        HashMap<String, Object> arr = new HashMap<String, Object>();

        Itasrd itasrd = new Itasrd(goodsRequestData);
        String seq = plusOne(jpaItasrdRepository.findMaxSeqByAssortId(goodsRequestData.getAssortId())); //myBatisGoodsDao.selectMaxSeqItasrd(goodsRequestData);
        if(seq == null){
            seq = fourStartCd;
        }
        else {
            seq = StringUtils.leftPad(Integer.toString((int)Double.parseDouble(seq)), 4, '0');
        }
        itasrd.setSeq(seq);

        jpaItasrdRepository.save(itasrd);

        return itasrd;
    }

    private List<Itvari> saveItvariList(GoodsRequestData goodsRequestData) {
//        Optional<List<String>> optionList1 = Optional.empty();
        List<GoodsRequestData.Attributes> attributes = goodsRequestData.getAttributes();
        List<Itvari> itvariList = new ArrayList<>();
        for(GoodsRequestData.Attributes item : attributes){
            List<GoodsRequestData.SeqAndValue> colors = item.getColor();
            List<GoodsRequestData.SeqAndValue> sizes = item.getSize();
            if(colors != null){
                for (int j = 0; j < colors.size() ; j++) {
                    Itvari itvari = new Itvari(goodsRequestData);
                    String seq = colors.get(j).getSeq();
                    if(seq == null || seq.equals("")){
                        String maxSeq = plusOne(jpaItvariRepository.findMaxSeqByAssortId(goodsRequestData.getAssortId())); //myBatisGoodsDao.selectMaxSeqItvari(goodsRequestData)
//                        System.out.println("------------------"+ maxSeq);
                        if(maxSeq != null){
                            maxSeq = Long.toString((long)Double.parseDouble(plusOne(jpaItvariRepository.findMaxSeqByAssortId(goodsRequestData.getAssortId())))); //myBatisGoodsDao.selectMaxSeqItvari(goodsRequestData)
                        }
                        logger.debug(maxSeq);
                        String seqRes = maxSeq == null? threeStartCd : StringUtils.leftPad(maxSeq, 3, '0');
                        logger.debug(StringUtils.leftPad(seqRes, 3, '0'));
                        seq = seqRes;
                    }
                    itvari.setSeq(seq);
                    itvari.setOptionNm(colors.get(j).getValue());
                    itvari.setOptionGb(colorGb);
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
            Ititmm ititmm = new Ititmm(goodsRequestData, item);
            String color = item.getColor();
            String size = item.getSize();
            item.setAssortId(goodsRequestData.getAssortId());
            if(color != null){ // color 요소가 있는 경우
                item.setOptionNm(color);
//                System.out.println(item.getAssortId() " " item.getOptionNm() + " " + );
                Itvari itvari = jpaItvariRepository.findByAssortIdAndOptionNm(goodsRequestData.getAssortId(), item.getOptionNm());//myBatisGoodsDao.selectOneSeqOptionGb(item);
                ititmm.setVariationGb1(colorGb);
                ititmm.setVariationSeq1((String)itvari.getSeq());
            }
            if(size != null){ // size 요소가 있는 경우
                item.setOptionNm(size);
                Itvari itvari = jpaItvariRepository.findByAssortIdAndOptionNm(goodsRequestData.getAssortId(), item.getOptionNm());//myBatisGoodsDao.selectOneSeqOptionGb(item);
                ititmm.setVariationGb2(sizeGb);
                ititmm.setVariationSeq2((String)itvari.getSeq());
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
//            ititmm.setMaxCnt("111");
            ititmm.setItemId(startItemId);
//            ititmm.setAddPrice(item.getAddPrice());
            jpaItitmmRepository.save(ititmm);
            itemsList.add(ititmm);
        }

        return itemsList;
    }

    private List<Ititmd> saveItemOptionList(GoodsRequestData goodsRequestData, List<Ititmm> ititmmList) {
        List<Ititmd> ititmdList = new ArrayList<>();
        for (Ititmm item: ititmmList) {
            Ititmd ititmd = new Ititmd(goodsRequestData, item);
            ititmdList.add(ititmd);
        }
        return ititmdList;
    }

    public void updateById(Long goodsId, Itasrt goods) {
        Optional<Itasrt> e = jpaItasrtRepository.findById(goodsId);
        if (e.isPresent()) {
            e.get().setAssortId(goods.getAssortId());
            e.get().setAssortNm(goods.getAssortNm());
            jpaItasrtRepository.save(goods);
        }
    }

    // table 초기화용 함수(test할 때 편하려고..)
    public void initTables(){
        jpaItasrdRepository.deleteAll();
        jpaItasrtRepository.deleteAll();
        jpaItasrnRepository.deleteAll();
        jpaItitmmRepository.deleteAll();
        jpaItvariRepository.deleteAll();
        Optional<SequenceData> op = jpaSequenceDataRepository.findById("seq_ITASRT");
        SequenceData seq = op.get();
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
