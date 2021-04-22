package io.spring.service.goods;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.jparepos.goods.JpaItasrdRepository;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmmRepository;
import io.spring.jparepos.goods.JpaItvariRepository;
import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.entity.Itasrd;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.entity.Itvari;
import org.flywaydb.core.internal.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JpaGoodsService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String colorGb = "01";
    private final String sizeGb = "02";
    private final String threeStartCd = "001";
    private final String fourStartCd = "0001";

    private final String seqStr = "seq";

    @Autowired
    private JpaItasrtRepository jpaItasrtRepository;
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

    public List<Itasrt> findAll() {
        List<Itasrt> goods = new ArrayList<>();
        jpaItasrtRepository.findAll().forEach(e -> goods.add(e));
        return goods;
    }

    public Optional<Itasrt> findById(Long goodsId) {
        Optional<Itasrt> goods = jpaItasrtRepository.findById(goodsId);
        return goods;
    }

    public void deleteById(Long goodsId) {
        jpaItasrtRepository.deleteById(goodsId);
    }

    public long save(Itasrt goods) {
        HashMap<String, Object> arr = new HashMap<String, Object>();

        if(goods.getAssortId() == null || goods.getAssortId() == ""){
            arr.put("seqName", "seq_ITASRT");
            HashMap<String, Object> x1 = myBatisCommonDao.getSequence(arr);
            String assortId = StringUtils.leftPad(Long.toString((long)x1.get("nextval")), 9, '0');
            logger.debug("nextVal : ", assortId);
            goods.setAssortId(assortId);
            // 입력값에 assort_id가 없어서 새로 채번하는 경우 sequence_data table의 sequence_cur_value도 update 필요
        }
        jpaItasrtRepository.save(goods);

        return Long.parseLong(goods.getAssortId());
    }

    public void save(GoodsRequestData goodsRequestData) {
        HashMap<String, Object> arr = new HashMap<String, Object>();

        Itasrd itasrd = new Itasrd(goodsRequestData);
        String seq = myBatisGoodsDao.selectMaxSeqItasrd(goodsRequestData);
        if(seq == null){
            seq = fourStartCd;
        }
        else {
            seq = StringUtils.leftPad(Integer.toString((int)Double.parseDouble(seq)), 4, '0');
        }
        itasrd.setSeq(seq);

        jpaItasrdRepository.save(itasrd);
    }

    public void saveItvariList(GoodsRequestData goodsRequestData) {
//        Optional<List<String>> optionList1 = Optional.empty();
        List<GoodsRequestData.Attributes> attributes = goodsRequestData.getAttributes();
        for(GoodsRequestData.Attributes item : attributes){
            List<GoodsRequestData.SeqAndValue> colors = item.getColor();
            List<GoodsRequestData.SeqAndValue> sizes = item.getSize();
            if(colors != null){
                for (int j = 0; j < colors.size() ; j++) {
                    Itvari itvari = new Itvari(goodsRequestData);
                    String seq = colors.get(j).getSeq();
                    if(seq == null || seq.equals("")){
                        String maxSeq = Integer.toString((int)Double.parseDouble(myBatisGoodsDao.selectMaxSeqItvari(goodsRequestData)));
                        logger.debug(maxSeq);
                        String seqRes = maxSeq == null? threeStartCd : StringUtils.leftPad(maxSeq, 3, '0');
                        logger.debug(StringUtils.leftPad(maxSeq, 3, '0'));
                        seq = seqRes;
                    }
                    itvari.setSeq(seq);
                    itvari.setOptionNm(colors.get(j).getValue());
                    itvari.setOptionGb(colorGb);
                    jpaItvariRepository.save(itvari);
                }
            }
            else if(sizes != null){
                for (int i = 0; i < sizes.size() ; i++) {
                    Itvari itvari = new Itvari(goodsRequestData);
                    String seq = sizes.get(i).getSeq();
                    if(seq == null || seq.equals("")){
                        String maxSeq = Integer.toString((int)Double.parseDouble(myBatisGoodsDao.selectMaxSeqItvari(goodsRequestData)));
                        String seqRes = maxSeq == null? threeStartCd : StringUtils.leftPad(maxSeq, 3, '0');
                        seq = seqRes;
                    }
                    itvari.setSeq(seq);
                    itvari.setOptionNm(sizes.get(i).getValue());
                    itvari.setOptionGb(sizeGb);
                    jpaItvariRepository.save(itvari);
                }
            }
        }
    }

    public void saveItemList(GoodsRequestData goodsRequestData) {
        List<GoodsRequestData.Items> itemList = goodsRequestData.getItems();
        for (GoodsRequestData.Items item : itemList ) {
            Ititmm ititmm = new Ititmm(goodsRequestData);
            String color = item.getColor();
            String size = item.getSize();
            item.setAssortId(goodsRequestData.getAssortId());
            HashMap<String, Object> resMap;
            if(color != null){
                item.setOptionNm(color);
                resMap = myBatisGoodsDao.selectOneSeqOptionGb(item);
                ititmm.setVariationGb1(colorGb);
                ititmm.setVariationSeq1((String)resMap.get(seqStr));
            }
            if(size != null){
                item.setOptionNm(size);
                resMap = myBatisGoodsDao.selectOneSeqOptionGb(item);
                ititmm.setVariationGb2(sizeGb);
                ititmm.setVariationSeq2((String)resMap.get(seqStr));
            }
            String startItemId = myBatisGoodsDao.selectMaxItemIdItitmm(goodsRequestData);

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
            ititmm.setAddPrice(item.getAddPrice());
            jpaItitmmRepository.save(ititmm);
        }
    }

    public void updateById(Long goodsId, Itasrt goods) {
        Optional<Itasrt> e = jpaItasrtRepository.findById(goodsId);
        if (e.isPresent()) {
            e.get().setAssortId(goods.getAssortId());
            e.get().setAssortNm(goods.getAssortNm());
            jpaItasrtRepository.save(goods);
        }
    }
}
