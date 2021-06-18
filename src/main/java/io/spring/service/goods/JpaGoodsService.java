package io.spring.service.goods;

import io.spring.dao.goods.MyBatisGoodsDao;
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
import io.spring.service.file.FileService;
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
    private final MyBatisGoodsDao myBatisGoodsDao;
    private final JpaItasrdRepository jpaItasrdRepository;
    private final JpaItitmmRepository jpaItitmmRepository;
    private final JpaItitmdRepository jpaItitmdRepository;
    private final JpaItaimgRepository jpaItaimgRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;

    private final JpaTmmapiRepository jpaTmmapiRepository;
    private final JpaTmitemRepository jpaTmitemRepository;

    private final FileService fileService;

    private final EntityManager em;




    private List<Itasrt> findAllItasrt() {
        List<Itasrt> goods = new ArrayList<>();
        jpaItasrtRepository.findAll().forEach(e -> goods.add(e));
        return goods;
    }

    private List<Itvari> findAllItvari() {
        List<Itvari> goods = jpaItvariRepository.findAll();
        jpaItvariRepository.flush();
        return goods;
    }

    private List<Ititmd> findAllItitmd() {
        List<Ititmd> goods = jpaItitmdRepository.findAll();
        jpaItitmdRepository.flush();
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
        // tmmapi에 저장
        this.saveTmmapi(itasrt);
        // itasrn에 goods 이력 저장
        Itasrn itasrn = this.saveItasrn(goodsInsertRequestData);
        // itasrd에 문구 저장
        List<Itasrd> itasrd = this.saveItasrd(goodsInsertRequestData);
        // itvari에 assort_id별 옵션요소 저장(색상, 사이즈)
        List<Itvari> itvariList = this.saveItvariList(goodsInsertRequestData);
        // ititmm에 assort_id별 item 저장
        List<Ititmm> ititmmList = this.saveItemList(goodsInsertRequestData, itvariList);
        // tmitem에 저장
        this.saveTmitem(ititmmList);
        // ititmd에 item 이력 저장
        List<Ititmd> ititmdList = this.saveItemHistoryList(goodsInsertRequestData, ititmmList);

        // itaimg에 assortId 업데이트 시켜주기
        this.updateItaimgAssortId(goodsInsertRequestData, itasrt.getAssortId());

        List<GoodsInsertResponseData.Attributes> attributesList = makeGoodsResponseAttributes(itvariList);
        List<GoodsInsertResponseData.Items> itemsList = makeGoodsResponseItems(ititmmList);
        return makeGoodsInsertResponseData(goodsInsertRequestData, attributesList, itemsList);
    }

    /**
     * Pecan
     * tmitem : insert, update 공용 함수
      */
    private void saveTmitem(List<Ititmm> ititmmList) {
        for(Ititmm ititmm : ititmmList){
            Tmitem tmitem = jpaTmitemRepository.findByChannelGbAndAssortIdAndItemId(StringFactory.getGbOne(), ititmm.getAssortId(), ititmm.getItemId())
                    .orElseGet(() -> new Tmitem(ititmm)); // channelGb 01 하드코딩

            tmitem.setShortYn(ititmm.getShortYn());
            tmitem.setVariationGb1(ititmm.getVariationGb1());
            tmitem.setVariationGb2(ititmm.getVariationGb2());
            tmitem.setVariationSeq1(ititmm.getVariationSeq1());
            tmitem.setVariationSeq2(ititmm.getVariationSeq2());
            tmitem.setOptionPrice(ititmm.getAddPrice());
//            jpaTmitemRepository.save(tmitem);
            em.persist(tmitem);
        }
    }

    /**
     * Pecan
     * tmmapi : insert, update 공용 함수
     * @param itasrt
     */
    private void saveTmmapi(Itasrt itasrt){
        Tmmapi tmmapi = jpaTmmapiRepository.findByChannelGbAndAssortId(StringFactory.getGbOne(), itasrt.getAssortId()).orElseGet(() -> null);
        if(tmmapi == null){ // insert
            tmmapi = new Tmmapi(itasrt); // channelGb 01 하드코딩
            tmmapi.setUploadType(StringFactory.getGbOne()); // 01 : 신규, 02 : 신규아님(수정)
        }
        else{ // update
            tmmapi.setUploadType(StringFactory.getGbTwo()); // 01 : 신규, 02 : 신규아님(수정)
        }
        tmmapi.setJoinStatus(StringFactory.getGbTwo()); // 01 : 고도몰 반영 성공, 02 : 아직 고도몰에 미반영 혹은 반영 실패 (01로 바꾸는 건 batch에서)
        tmmapi.setUploadYn(StringFactory.getGbTwo()); // 01 : 업로드 완료, 02 : 업로드 미완료
        tmmapi.setAssortNm(itasrt.getAssortNm());
        tmmapi.setStandardPrice(itasrt.getLocalPrice());
        tmmapi.setSalePrice(itasrt.getLocalSale());
        tmmapi.setShortageYn(itasrt.getShortageYn());
//        jpaTmmapiRepository.save(tmmapi);
        em.persist(tmmapi);
    }

    /**
     * Pecan
     * itaimg에 생성한 assortId 심어주는 함수
     * @param goodsInsertRequestData
     * @param assortId
     */
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

		itasrt.setDispCategoryId(goodsInsertRequestData.getDispCategoryId());

        itasrt.setBrandId(goodsInsertRequestData.getBrandId());

        itasrt.setOrigin(goodsInsertRequestData.getOrigin());

        itasrt.setManufactureNm(goodsInsertRequestData.getManufactureNm());
        itasrt.setAssortModel(goodsInsertRequestData.getAssortModel());

		itasrt.setOptionGbName(goodsInsertRequestData.getOptionGbName());
		itasrt.setTaxGb(goodsInsertRequestData.getTaxGb());
		itasrt.setAssortState(goodsInsertRequestData.getAssortState());
        itasrt.setShortageYn(goodsInsertRequestData.getShortageYn());

        itasrt.setLocalPrice(goodsInsertRequestData.getLocalPrice());
		itasrt.setLocalSale(goodsInsertRequestData.getLocalSale());
        itasrt.setDeliPrice(goodsInsertRequestData.getDeliPrice());

        itasrt.setMargin(goodsInsertRequestData.getMargin());

        itasrt.setMdRrp(goodsInsertRequestData.getMdRrp());
        itasrt.setMdYear(goodsInsertRequestData.getMdYear());
        itasrt.setMdVatrate(goodsInsertRequestData.getMdVatrate());
        itasrt.setMdDiscountRate(goodsInsertRequestData.getMdDiscountRate());
        itasrt.setMdGoodsVatrate(goodsInsertRequestData.getMdGoodsVatrate());
        itasrt.setBuyWhere(goodsInsertRequestData.getBuyWhere());
		itasrt.setBuySupplyDiscount(goodsInsertRequestData.getBuySupplyDiscount());
		itasrt.setBuyExchangeRate(goodsInsertRequestData.getBuyExchangeRate());
		itasrt.setBuyRrpIncrement(goodsInsertRequestData.getBuyRrpIncrement());

		itasrt.setSellStaDt(goodsInsertRequestData.getSellStaDt());
		itasrt.setSellEndDt(goodsInsertRequestData.getSellEndDt());

		itasrt.setAsWidth(goodsInsertRequestData.getAsWidth());
		itasrt.setAsLength(goodsInsertRequestData.getAsLength());
		itasrt.setAsHeight(goodsInsertRequestData.getAsHeight());
		itasrt.setWeight(goodsInsertRequestData.getWeight());

		itasrt.setAssortGb(goodsInsertRequestData.getAssortGb());

		itasrt.setMdTax(goodsInsertRequestData.getMdTax());

		itasrt.setMdMargin(goodsInsertRequestData.getMdMargin());

		itasrt.setMdGoodsVatrate(goodsInsertRequestData.getMdGoodsVatrate());

		itasrt.setBuyTax(goodsInsertRequestData.getBuyTax());
        // 옵션과 옵션에 따른 아이템들의 존재 여부. 미존재시 단품 옵션 1개, 단품 옵션 내역을 가진 아이템 1개가 생성돼야 함.
		itasrt.setOptionUseYn(goodsInsertRequestData.getOptionUseYn());

//        jpaItasrtRepository.save(itasrt);
        em.persist(itasrt);
        return itasrt;
    }

    /**
     * 21-04-28 Pecan
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
//        jpaItasrnRepository.save(itasrn);
        em.persist(itasrn);
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
//            jpaItasrdRepository.save(itasrd);
            em.persist(itasrd);
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
        if(goodsInsertRequestData.getOptionUseYn().equals(StringFactory.getGbTwo())){ // optionUseYn이 02, 즉 단품인 경우
            return saveSingleOption(goodsInsertRequestData); // 단품 옵션 1개를 저장하는 함수
        }
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
//            jpaItvariRepository.save(itvari);
            em.persist(itvari);
        }
        return itvariList;
    }

    /**
     * 21-06-11 Pecan
     * 단품 옵션 1개를 저장하는 함수
     * @param goodsInsertRequestData
     * @return
     */
    private List<Itvari> saveSingleOption(GoodsInsertRequestData goodsInsertRequestData) {
        List<Itvari> itvariList = new ArrayList<>();
        Itvari itvari = jpaItvariRepository.findByAssortIdAndSeq(goodsInsertRequestData.getAssortId(), StringFactory.getFourStartCd());
        if(itvari == null){
            itvari = new Itvari(goodsInsertRequestData);
            itvari.setSeq(StringFactory.getFourStartCd()); // 0001
            itvari.setOptionGb(StringFactory.getGbOne()); // 01
            itvari.setImgYn(StringFactory.getGbTwo()); // 02
            itvari.setOptionNm(StringFactory.getStrSingleGoods()); // 단품
            itvari.setVariationGb(StringFactory.getGbOne()); // 01
//        jpaItvariRepository.save(itvari);
            em.persist(itvari);
        }
        itvariList.add(itvari);
        return itvariList;
    }

    /**
     * 21-04-28 Pecan
     * 아이템 정보 insert, update
     * @param goodsInsertRequestData
     * @return List<Ititmm>
     */
    private List<Ititmm> saveItemList(GoodsInsertRequestData goodsInsertRequestData, List<Itvari> itvariList) {
        if(goodsInsertRequestData.getOptionUseYn().equals(StringFactory.getGbTwo())){
            return saveSingleItem(goodsInsertRequestData);
        }
//        List<Itvari> itvariList = findAllItvari();
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
//            Itvari op1 = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue1());
            Itvari op1 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getOptionNm().equals(item.getVariationValue1()))
                    .collect(Utilities.toSingleton());
            if(op1 != null){
                ititmm.setVariationGb1(op1.getOptionGb());
                ititmm.setVariationSeq1(op1.getSeq());
            }
            // 옵션2 관련값 찾아넣기
            Itvari op2 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getOptionNm().equals(item.getVariationValue2()))
                    .collect(Utilities.toSingleton());
//            Itvari op2 = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue2());
            if(op2 != null){
                ititmm.setVariationGb2(op2.getOptionGb());
                ititmm.setVariationSeq2(op2.getSeq());
            }
            ititmm.setAddPrice(item.getAddPrice());
            ititmm.setShortYn(item.getShortYn());
//            jpaItitmmRepository.save(ititmm);
//            System.out.println("===== : " + ititmm.toString());
            em.persist(ititmm);
            ititmmList.add(ititmm);
        }
        return ititmmList;
    }

//    private List<Ititmm> saveItemList(GoodsInsertRequestData goodsInsertRequestData) {
//        if(goodsInsertRequestData.getOptionUseYn().equals(StringFactory.getGbTwo())){
//            return saveSingleItem(goodsInsertRequestData);
//        }
//        List<GoodsInsertRequestData.Items> itemList = goodsInsertRequestData.getItems();
//        List<Ititmm> ititmmList = new ArrayList<>();
//        for(GoodsInsertRequestData.Items item : itemList){
//            String itemId = item.getItemId(); // item id를 객체가 갖고 있으면 그것을 이용
//            Ititmm ititmm = new Ititmm(goodsInsertRequestData.getAssortId(), item);
//            if(itemId == null || itemId.trim().equals("")){ // 객체에 item id가 없으면 jpa에서 max값을 가져옴
//                itemId = jpaItitmmRepository.findMaxItemIdByAssortId(goodsInsertRequestData.getAssortId());
//                if(itemId == null || itemId.trim().equals("")){ // jpa에서 max값을 가져왔는데 null이면 해당 assort id에 item id가 존재하지 않으므로 초기값(0001)을 설정
//                    itemId = StringFactory.getFourStartCd();
//                }
//                else { // jpa에서 max값을 가져온 경우 1을 더한 후 item id로 삼음
//                    itemId = Utilities.plusOne(itemId, 4);
//                }
//                ititmm.setItemId(itemId);
//            }
//            else{ // 객체에 item id가 있으면 해당 객체가 이미 존재하므로 객체를 가져옴 (update)
//                ititmm = jpaItitmmRepository.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(), itemId);
//            }
//            System.out.println("1 : "+System.currentTimeMillis());
//            // 옵션1 관련값 찾아넣기
//            HashMap<String, Object> op1 = myBatisGoodsDao.selectOneSeqOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue1());//jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue1());
//            if(op1 != null){
//                ititmm.setVariationGb1((String)op1.get("optionGb"));
//                ititmm.setVariationSeq1((String)op1.get("seq"));
//            }
//            System.out.println("2 : "+System.currentTimeMillis());
//            // 옵션2 관련값 찾아넣기
//            HashMap<String, Object> op2 = myBatisGoodsDao.selectOneSeqOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue2()); //Itvari op2 = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue2());
//            if(op2 != null){
//                ititmm.setVariationGb2((String)op2.get("optionGb"));
//                ititmm.setVariationSeq2((String)op2.get("seq"));
//            }
//            System.out.println("3 : "+System.currentTimeMillis());
////            String[] optionNmList = item.getValue().split(StringFactory.getSplitGb());
////            // itvari에서 옵션 형질 찾아오기
////            for(String optionNm : optionNmList){
////                Itvari op = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), optionNm);
////                String opGb = op.getOptionGb();
////                if(opGb.equals(StringFactory.getGbOne())){ // optionGb이 01인 경우
////                    ititmm.setVariationGb1(opGb);
////                    ititmm.setVariationSeq1(op.getSeq());
////                }
////                else if(opGb.equals(StringFactory.getGbTwo())){ // optionGb이 02인 경우
////                    ititmm.setVariationGb2(opGb);
////                    ititmm.setVariationSeq2(op.getSeq());
////                }
////            }
//            ititmm.setAddPrice(item.getAddPrice());
//            ititmm.setShortYn(item.getShortYn());
//            jpaItitmmRepository.save(ititmm);
//            ititmmList.add(ititmm);
//        }
//        return ititmmList;
//    }

    /**
     * 21-06-11 Pecan
     * 단품 옵션을 가진 아이템 1개를 저장하는 함수
     * @param goodsInsertRequestData
     * @return
     */
    private List<Ititmm> saveSingleItem(GoodsInsertRequestData goodsInsertRequestData) {
        List<Ititmm> ititmmList = new ArrayList<>();
        Ititmm ititmm = jpaItitmmRepository.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(), StringFactory.getFourStartCd());
        if(ititmm == null){
            ititmm = new Ititmm(goodsInsertRequestData);
            ititmm.setItemId(StringFactory.getFourStartCd()); // 0001
            ititmm.setVariationGb1(StringFactory.getGbOne()); // 01
            ititmm.setVariationSeq1(StringFactory.getFourStartCd()); // 0001
    //        jpaItitmmRepository.save(ititmm);
            em.persist(ititmm);
        }
        ititmmList.add(ititmm);

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
        Date effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        List<Ititmd> allItitmdList = jpaItitmdRepository.findAll();
        for (Ititmm ititmm : ititmmList) {
            Ititmd ititmd = allItitmdList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
            && x.getItemId().equals(ititmm.getItemId()) && x.getEffEndDt().equals(effEndDt)).collect(Utilities.toSingleton());
//            Ititmd ititmd = jpaItitmdRepository.findByAssortIdAndItemIdAndEffEndDt(goodsInsertRequestData.getAssortId(), ititmm.getItemId() , effEndDt);
            if(ititmd == null){ // insert
                ititmd = new Ititmd(ititmm);
            }
            else{ // update
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.SECOND, -1);
                ititmd.setEffEndDt(cal.getTime());
                // update 후 새 이력 insert
                Ititmd newItitmd = new Ititmd(ititmd);
//                jpaItitmdRepository.save(newItitmd);
                em.persist(newItitmd);
//            saveItasrn(goodsRequestData);
            }
            ititmd.setShortYn(ititmm.getShortYn());
//            jpaItitmdRepository.save(ititmd);
            em.persist(ititmd);
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
    	
//		System.out.println(itasrt);

        GoodsSelectDetailResponseData goodsSelectDetailResponseData = new GoodsSelectDetailResponseData(itasrt);

		// 카테고리벨류

        List<GoodsSelectDetailResponseData.Description> descriptions = makeDescriptions(itasrt.getItasrdList());
        List<GoodsSelectDetailResponseData.Attributes> attributesList = makeAttributesList(itasrt.getItvariList());
        List<GoodsSelectDetailResponseData.Items> itemsList = makeItemsList(itasrt.getItitmmList());
        List<GoodsSelectDetailResponseData.UploadMainImage> uploadMainImageList = makeUploadMainImageList(itasrt.getItaimg());
        List<GoodsSelectDetailResponseData.UploadAddImage> uploadAddImageList = makeUploadAddImageList(itasrt.getItaimg());
        goodsSelectDetailResponseData.setDescription(descriptions);
        goodsSelectDetailResponseData.setAttributes(attributesList);
        goodsSelectDetailResponseData.setItems(itemsList);
        goodsSelectDetailResponseData.setUploadMainImage(uploadMainImageList);
        goodsSelectDetailResponseData.setUploadAddImage(uploadAddImageList);
        return goodsSelectDetailResponseData;
    }

    private List<GoodsSelectDetailResponseData.UploadAddImage> makeUploadAddImageList(List<Itaimg> itaimgList) {
        List<GoodsSelectDetailResponseData.UploadAddImage> uploadAddImageList = new ArrayList<>();
        for(Itaimg itaimg : itaimgList){
            if(itaimg.getImageGb().equals(StringFactory.getGbTwo())) {
                GoodsSelectDetailResponseData.UploadAddImage uploadAddImage = new GoodsSelectDetailResponseData.UploadAddImage(itaimg);
                uploadAddImageList.add(uploadAddImage);
            }
        }
        return uploadAddImageList;
    }

    private List<GoodsSelectDetailResponseData.UploadMainImage> makeUploadMainImageList(List<Itaimg> itaimgList) {
        List<GoodsSelectDetailResponseData.UploadMainImage> uploadMainImageList = new ArrayList<>();
        for(Itaimg itaimg : itaimgList){
            if(itaimg.getImageGb().equals(StringFactory.getGbOne())){
                GoodsSelectDetailResponseData.UploadMainImage uploadMainImage = new GoodsSelectDetailResponseData.UploadMainImage(itaimg);
                uploadMainImageList.add(uploadMainImage);
            }
        }
        return uploadMainImageList;
    }

    // ititmm -> items 형태로 바꿔주는 함수
    private List<GoodsSelectDetailResponseData.Items> makeItemsList(List<Ititmm> ititmmList) {
        List<GoodsSelectDetailResponseData.Items> itemsList = new ArrayList<>();
        for(Ititmm ititmm : ititmmList){
            GoodsSelectDetailResponseData.Items item = new GoodsSelectDetailResponseData.Items();
            item.setItemId(ititmm.getItemId());
            Itvari op1 = ititmm.getItvari1();//jpaItvariRepository.findByAssortIdAndSeq(ititmm.getAssortId(), ititmm.getVariationSeq1());
			item.setValue1(op1.getOptionNm());
			item.setSeq1(op1.getSeq());
			item.setStatus1(StringFactory.getStrR()); // r 하드코딩
            if(ititmm.getVariationSeq2() != null){
                Itvari op2 = ititmm.getItvari2();//jpaItvariRepository.findByAssortIdAndSeq(ititmm.getAssortId(), ititmm.getVariationSeq2());
				item.setSeq2(op2.getSeq());
				item.setValue2(op2.getOptionNm());
				item.setStatus2(StringFactory.getStrR()); // r 하드코딩
            }
            item.setAddPrice(ititmm.getAddPrice());
			item.setShortageYn(ititmm.getShortYn());
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
                                "left join fetch t.itbrnd b " +
                                "left join fetch t.itcatg c " +
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
