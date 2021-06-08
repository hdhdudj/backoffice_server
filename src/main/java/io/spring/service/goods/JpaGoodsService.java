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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaGoodsService {
    private final JpaItasrtRepository jpaItasrtRepository;
    private final JpaItasrnRepository jpaItasrnRepository;
    private final JpaItvariRepository jpaItvariRepository;
//    private MyBatisCommonDao myBatisCommonDao;
//    private MyBatisGoodsDao myBatisGoodsDao;
    private final JpaItasrdRepository jpaItasrdRepository;
    private final JpaItitmmRepository jpaItitmmRepository;
    private final JpaItitmdRepository jpaItitmdRepository;
    private final JpaItaimgRepository jpaItaimgRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final EntityManager em;
    


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

        // itaimg에 assortId 업데이트 시켜주기
        this.updateItaimgAssortId(goodsInsertRequestData, itasrt.getAssortId());

        List<GoodsInsertResponseData.Attributes> attributesList = makeGoodsResponseAttributes(itvariList);
        List<GoodsInsertResponseData.Items> itemsList = makeGoodsResponseItems(ititmmList);
        return makeGoodsInsertResponseData(goodsInsertRequestData, attributesList, itemsList);
    }

    private void updateItaimgAssortId(GoodsInsertRequestData goodsInsertRequestData, String assortId) {
        List<GoodsInsertRequestData.UploadMainImage> uploadMainImageList = goodsInsertRequestData.getUploadMainImage();
        List<GoodsInsertRequestData.UploadAddImage> uploadAddImageList = goodsInsertRequestData.getUploadAddImage();
        for(GoodsInsertRequestData.UploadMainImage uploadMainImage : uploadMainImageList){
            Itaimg itaimg = jpaItaimgRepository.findById(uploadMainImage.getUid()).orElseGet(() -> null);
            itaimg.setAssortId(assortId);
        }
        for(GoodsInsertRequestData.UploadAddImage uploadAddImage : uploadAddImageList){
            Itaimg itaimg = jpaItaimgRepository.findById(uploadAddImage.getUid()).orElseGet(() -> null);
            itaimg.setAssortId(assortId);
        }
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
            log.debug(e.getMessage());
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
            // 옵션1 관련값 찾아넣기
            Itvari op1 = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue1());
            if(op1 != null){
                ititmm.setVariationGb1(op1.getOptionGb());
                ititmm.setVariationSeq1(op1.getSeq());
            }
            // 옵션2 관련값 찾아넣기
            Itvari op2 = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue2());
            if(op2 != null){
                ititmm.setVariationGb2(op2.getOptionGb());
                ititmm.setVariationSeq2(op2.getSeq());
            }
//            String[] optionNmList = item.getValue().split(StringFactory.getSplitGb());
//            // itvari에서 옵션 형질 찾아오기
//            for(String optionNm : optionNmList){
//                Itvari op = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), optionNm);
//                String opGb = op.getOptionGb();
//                if(opGb.equals(StringFactory.getGbOne())){ // optionGb이 01인 경우
//                    ititmm.setVariationGb1(opGb);
//                    ititmm.setVariationSeq1(op.getSeq());
//                }
//                else if(opGb.equals(StringFactory.getGbTwo())){ // optionGb이 02인 경우
//                    ititmm.setVariationGb2(opGb);
//                    ititmm.setVariationSeq2(op.getSeq());
//                }
//            }
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
                effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
            }
            catch (Exception e){
                log.debug(e.getMessage());
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
     * @param assortId
     * @return GoodsResponseData
     */
    public GoodsSelectDetailResponseData getGoodsDetailPage(String assortId) {
        Itasrt itasrt = jpaItasrtRepository.findById(assortId).orElseThrow(() -> new ResourceNotFoundException());
    	
        GoodsSelectDetailResponseData goodsSelectDetailResponseData = new GoodsSelectDetailResponseData(itasrt);
        List<GoodsSelectDetailResponseData.Description> descriptions = makeDescriptions(itasrt.getItasrdList());
        List<GoodsSelectDetailResponseData.Attributes> attributesList = makeAttributesList(itasrt.getItvariList());
        List<GoodsSelectDetailResponseData.Items> itemsList = makeItemsList(itasrt.getItitmmList());
        goodsSelectDetailResponseData.setDescription(descriptions);
        goodsSelectDetailResponseData.setAttributes(attributesList);
        goodsSelectDetailResponseData.setItems(itemsList);
        return goodsSelectDetailResponseData;
    }
    // ititmm -> items 형태로 바꿔주는 함수
    private List<GoodsSelectDetailResponseData.Items> makeItemsList(List<Ititmm> ititmmList) {
        List<GoodsSelectDetailResponseData.Items> itemsList = new ArrayList<>();
        for(Ititmm ititmm : ititmmList){
            GoodsSelectDetailResponseData.Items item = new GoodsSelectDetailResponseData.Items();
            item.setItemId(ititmm.getItemId());
            Itvari op1 = jpaItvariRepository.findByAssortIdAndSeq(ititmm.getAssortId(), ititmm.getVariationSeq1());
            item.setVariationValue1(op1.getOptionNm());
            item.setVariationSeq1(op1.getSeq());
            if(ititmm.getVariationSeq2() != null){
                Itvari op2 = jpaItvariRepository.findByAssortIdAndSeq(ititmm.getAssortId(), ititmm.getVariationSeq2());
                item.setVariationSeq2(op2.getSeq());
                item.setVariationValue2(op2.getOptionNm());
            }
            item.setAddPrice(ititmm.getAddPrice());
            item.setShortYn(ititmm.getShortYn());
            itemsList.add(item);
        }
        return itemsList;
    }

    // itvari -> attributes 형태로 바꿔주는 함수
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

    // itasrd -> description 형태로 바꿔주는 함수
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
     * brandId, dispCategoryId, regDt, shortageYn, (이상 itasrt) dispCategoryId(itcatg), brandId(itbrnd) 로 list 목록 가져오는 함수
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
