package io.spring.service.goods;

import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.infrastructure.mapstruct.GoodsSelectDetailResponseDataMapper;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.category.JpaIfCategoryRepository;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.goods.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final JpaIfBrandRepository jpaIfBrandRepository;
    private final JpaIfCategoryRepository jpaIfCategoryRepository;

    private final JpaTmmapiRepository jpaTmmapiRepository;
    private final JpaTmitemRepository jpaTmitemRepository;

    private final FileService fileService;

    private final EntityManager em;

    private final GoodsSelectDetailResponseDataMapper goodsSelectDetailResponseDataMapper;

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

        List<GoodsInsertResponseData.Attributes> attributesList = this.makeGoodsResponseAttributes(itvariList);
        List<GoodsInsertResponseData.Items> itemsList = this.makeGoodsResponseItems(ititmmList, itvariList);
        return this.makeGoodsInsertResponseData(goodsInsertRequestData, attributesList, itemsList);
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
            tmitem.setVariationGb2(ititmm.getVariationGb3());
            tmitem.setVariationSeq1(ititmm.getVariationSeq1());
            tmitem.setVariationSeq2(ititmm.getVariationSeq2());
            tmitem.setVariationSeq2(ititmm.getVariationSeq3());

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
            jpaItaimgRepository.save(itaimg);
        }
        for(GoodsInsertRequestData.UploadAddImage uploadAddImage : uploadAddImageList){
            Itaimg itaimg = jpaItaimgRepository.findById(uploadAddImage.getUid()).orElseGet(() -> null);
            itaimg.setAssortId(assortId);
            jpaItaimgRepository.save(itaimg);
        }
    }

    private List<GoodsInsertResponseData.Attributes> makeGoodsResponseAttributes(List<Itvari> itvariList){
        List<GoodsInsertRequestData.Attributes> attributesList = new ArrayList<>();
        for(Itvari i : itvariList){
            GoodsInsertRequestData.Attributes a = new GoodsInsertRequestData.Attributes(i);
            attributesList.add(a);
        }
        return null;
    }

    private List<GoodsInsertResponseData.Items> makeGoodsResponseItems(List<Ititmm> ititmmList, List<Itvari> itvariList){
        List<GoodsInsertResponseData.Items> itemsList = new ArrayList<>();
        for(Ititmm ititmm : ititmmList){
            GoodsInsertResponseData.Items items = new GoodsInsertResponseData.Items(ititmm);
            items.setVariationValue1(itvariList.stream().filter(x-> ititmm.getVariationSeq1().equals(x.getSeq())).collect(Collectors.toList()).get(0).getOptionNm());
            items.setVariationValue2(ititmm.getVariationSeq2() == null ? "" : itvariList.stream().filter(x-> ititmm.getVariationSeq2().equals(x.getSeq())).collect(Collectors.toList()).get(0).getOptionNm());
            items.setVariationValue3(ititmm.getVariationSeq3() == null ? "" : itvariList.stream().filter(x-> ititmm.getVariationSeq3().equals(x.getSeq())).collect(Collectors.toList()).get(0).getOptionNm());
            itemsList.add(items);
        }
        return itemsList;
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
        itasrt.setAssortColor(goodsInsertRequestData.getAssortColor() == null || goodsInsertRequestData.getAssortColor().trim().equals("")? null : goodsInsertRequestData.getAssortColor());

		itasrt.setDispCategoryId(goodsInsertRequestData.getDispCategoryId() == null || goodsInsertRequestData.getDispCategoryId().trim().equals("")? null : goodsInsertRequestData.getDispCategoryId());
        itasrt.setCategoryId(this.getGodoCateCd(goodsInsertRequestData.getDispCategoryId()));

        itasrt.setBrandId(goodsInsertRequestData.getBrandId() == null || goodsInsertRequestData.getBrandId().trim().equals("")? null : goodsInsertRequestData.getBrandId());

        itasrt.setOrigin(goodsInsertRequestData.getOrigin());

        itasrt.setManufactureNm(goodsInsertRequestData.getManufactureNm() == null || goodsInsertRequestData.getManufactureNm().trim().equals("")? null : goodsInsertRequestData.getManufactureNm());
        itasrt.setAssortModel(goodsInsertRequestData.getAssortModel() == null || goodsInsertRequestData.getAssortModel().trim().equals("")? null : goodsInsertRequestData.getAssortModel());
        itasrt.setVendorId(goodsInsertRequestData.getVendorId() == null || goodsInsertRequestData.getVendorId().trim().equals("")? null : goodsInsertRequestData.getVendorId());

		itasrt.setOptionGbName(goodsInsertRequestData.getOptionGbName());
		itasrt.setTaxGb(goodsInsertRequestData.getTaxGb());
		itasrt.setAssortState(goodsInsertRequestData.getAssortState());
        itasrt.setShortageYn(goodsInsertRequestData.getShortageYn());

        itasrt.setLocalPrice(goodsInsertRequestData.getLocalPrice() == null || goodsInsertRequestData.getLocalPrice().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalPrice()));
		itasrt.setLocalSale(goodsInsertRequestData.getLocalSale() == null || goodsInsertRequestData.getLocalSale().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalSale()));
        itasrt.setDeliPrice(goodsInsertRequestData.getDeliPrice() == null || goodsInsertRequestData.getDeliPrice().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getDeliPrice()));

        itasrt.setMargin(goodsInsertRequestData.getMargin() == null || goodsInsertRequestData.getMargin().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMargin()));

        itasrt.setMdRrp(goodsInsertRequestData.getMdRrp() == null || goodsInsertRequestData.getMdRrp().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdRrp()));
        itasrt.setMdYear(goodsInsertRequestData.getMdYear());
        itasrt.setMdVatrate(goodsInsertRequestData.getMdVatrate() == null || goodsInsertRequestData.getMdVatrate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdVatrate()));
        itasrt.setMdDiscountRate(goodsInsertRequestData.getMdDiscountRate() == null || goodsInsertRequestData.getMdDiscountRate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdDiscountRate()));
        itasrt.setMdGoodsVatrate(goodsInsertRequestData.getMdGoodsVatrate() == null || goodsInsertRequestData.getMdGoodsVatrate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdGoodsVatrate()));
        itasrt.setBuyWhere(goodsInsertRequestData.getBuyWhere());
		itasrt.setBuySupplyDiscount(goodsInsertRequestData.getBuySupplyDiscount() == null || goodsInsertRequestData.getBuySupplyDiscount().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuySupplyDiscount()));
		itasrt.setBuyExchangeRate(goodsInsertRequestData.getBuyExchangeRate() == null || goodsInsertRequestData.getBuyExchangeRate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuyExchangeRate()));
		itasrt.setBuyRrpIncrement(goodsInsertRequestData.getBuyRrpIncrement() == null || goodsInsertRequestData.getBuyRrpIncrement().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuyRrpIncrement()));

		itasrt.setSellStaDt(goodsInsertRequestData.getSellStaDt());
		itasrt.setSellEndDt(goodsInsertRequestData.getSellEndDt());

		itasrt.setAsWidth(goodsInsertRequestData.getAsWidth() == null || goodsInsertRequestData.getAsWidth().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsWidth()));
		itasrt.setAsLength(goodsInsertRequestData.getAsLength() == null || goodsInsertRequestData.getAsLength().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsLength()));
		itasrt.setAsHeight(goodsInsertRequestData.getAsHeight() == null || goodsInsertRequestData.getAsHeight().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsHeight()));
		itasrt.setWeight(goodsInsertRequestData.getWeight() == null || goodsInsertRequestData.getWeight().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getWeight()));

		itasrt.setAssortGb(goodsInsertRequestData.getAssortGb());

		itasrt.setMdTax(goodsInsertRequestData.getMdTax());

		itasrt.setMdMargin(goodsInsertRequestData.getMdMargin() == null || goodsInsertRequestData.getMdMargin().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdMargin()));

		itasrt.setMdGoodsVatrate(goodsInsertRequestData.getMdGoodsVatrate() == null || goodsInsertRequestData.getMdGoodsVatrate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdGoodsVatrate()));

		itasrt.setBuyTax(goodsInsertRequestData.getBuyTax());
        // 옵션과 옵션에 따른 아이템들의 존재 여부. 미존재시 단품 옵션 1개, 단품 옵션 내역을 가진 아이템 1개가 생성돼야 함.
		itasrt.setOptionUseYn(goodsInsertRequestData.getOptionUseYn());

//        jpaItasrtRepository.save(itasrt);
        em.persist(itasrt);
        return itasrt;
    }

    // 우리 카테고리로 고도몰 카테고리코드 가져오기
    private String getGodoCateCd(String cateId){
        System.out.println("+++++ cateId : " + cateId);
        String cateCd = null;
        IfCategory ifCategory = jpaIfCategoryRepository.findByChannelGbAndCategoryId(StringFactory.getGbOne(), cateId);
        if (ifCategory == null) {
            log.debug("category code is not exist.");
            return cateCd;
        }
        cateCd = ifCategory.getChannelCategoryId();

        System.out.println("+++++ cateCd : " + cateCd);
        return cateCd;
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
        itasrn.setLocalSale(goodsInsertRequestData.getLocalSale() == null || goodsInsertRequestData.getLocalSale().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalSale()));
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
        List<Itasrd> itasrdList1 = jpaItasrdRepository.findByAssortId(goodsInsertRequestData.getAssortId());//new Itasrd(goodsInsertRequestData);
        for (int i = 0; i < descriptionList.size() ; i++) {
            GoodsInsertRequestData.Description description = descriptionList.get(i);
            List<Itasrd> itasrdList2 = itasrdList1.stream().filter(x->x.getOrdDetCd().equals(description.getOrdDetCd())).collect(Collectors.toList());
            Itasrd itasrd = itasrdList2.size() > 0? itasrdList2.get(0) : null;
            String seq = descriptionList.get(i).getSeq();
//            if(seq == null || seq.trim().equals("")){ // insert
            if(itasrd == null){ // insert
                itasrd = new Itasrd(goodsInsertRequestData, description);
//                if (seq == null || seq.trim().equals("")) { // insert -> 빈 테이블
//                    seq = StringFactory.getFourStartCd();//fourStartCd;
//                }
//                else{ // insert -> 찬 테이블
//                    seq = Utilities.plusOne(seq, 4);
//                }
                if(description.getOrdDetCd().equals(StringFactory.getGbOne())){
                    seq = StringFactory.getFourStartCd(); // 0001
                }
                else if(description.getOrdDetCd().equals(StringFactory.getGbTwo())){
                    seq = StringFactory.getFourSecondCd(); // 0002
                }
                itasrd.setSeq(seq);
            }
            else{ // update
//                itasrd = jpaItasrdRepository.findByAssortIdAndSeq(goodsInsertRequestData.getAssortId(), seq);
                itasrd.setOrdDetCd(descriptionList.get(i).getOrdDetCd());
                itasrd.setMemo(descriptionList.get(i).getMemo());
                itasrd.setTextHtmlGb(descriptionList.get(i).getTextHtmlGb());
            }
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
        List<Itvari> existItvariList = jpaItvariRepository.findByAssortId(goodsInsertRequestData.getAssortId());

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
     * seq의 최댓값을 반환하는 함수
     */
//    private <T> long calcMaxAvailableQty(List<T> list) {
//        long maxShipIndicateQty = -1;
//        for(T t : list){
//            long shipIndicateQty = t.getShipIndicateQty() == null ? 0l : t.getShipIndicateQty();
//            long qty = t.getQty() == null ? 0l : t.getQty();
//            long availableQty = qty - shipIndicateQty;
//            if(availableQty > maxShipIndicateQty){
//                maxShipIndicateQty = availableQty;
//            }
//        }
//        return maxShipIndicateQty;
//    }

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
            Itvari op1 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getOptionNm().equals(item.getVariationValue1()))
                    .collect(Utilities.toSingleton());
            if(op1 != null){
                ititmm.setVariationGb1(op1.getOptionGb());
                ititmm.setVariationSeq1(op1.getSeq());
            }
            // 옵션2 관련값 찾아넣기
            Itvari op2 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getOptionNm().equals(item.getVariationValue2()))
                    .collect(Utilities.toSingleton());
            if(op2 != null){
                ititmm.setVariationGb2(op2.getOptionGb());
                ititmm.setVariationSeq2(op2.getSeq());
            }
            // 옵션3 관련값 찾아넣기
            Itvari op3 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getOptionNm().equals(item.getVariationValue3()))
                    .collect(Utilities.toSingleton());
            if(op3 != null){
                ititmm.setVariationGb3(op3.getOptionGb());
                ititmm.setVariationSeq3(op3.getSeq());
            }
            ititmm.setAddPrice(item.getAddPrice() == null || item.getAddPrice().trim().equals("")? null : Float.parseFloat(item.getAddPrice()));
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
        Itasrt itasrt = em.createQuery("select distinct(i) from Itasrt i " +
                "left outer join fetch i.cmvdmr cv " +
                "left outer join fetch i.ifBrand ib " +
                "left outer join fetch i.itvariList ivList " +
                "where i.assortId=?1", Itasrt.class).setParameter(1,assortId).getSingleResult();//jpaItasrtRepository.findById(assortId).orElseThrow(() -> new ResourceNotFoundException());
    	
//		System.out.println(itasrt);
        GoodsSelectDetailResponseData goodsSelectDetailResponseData = new GoodsSelectDetailResponseData(itasrt);

		// 카테고리벨류
        IfBrand ifBrand;
        if(itasrt.getBrandId() != null && !itasrt.getBrandId().trim().equals("")){
            ifBrand = itasrt.getIfBrand();//jpaIfBrandRepository.findByChannelGbAndBrandId(StringFactory.getGbOne(),itasrt.getBrandId());
            goodsSelectDetailResponseData.setBrandNm(ifBrand == null? null : ifBrand.getBrandNm());
        }
        List<GoodsSelectDetailResponseData.Description> descriptions = this.makeDescriptions(itasrt.getItasrdList());
        List<GoodsSelectDetailResponseData.Attributes> attributesList = this.makeAttributesList(itasrt.getItvariList());
        List<GoodsSelectDetailResponseData.Items> itemsList = this.makeItemsList(itasrt.getItitmmList());
        List<GoodsSelectDetailResponseData.UploadMainImage> uploadMainImageList = this.makeUploadMainImageList(itasrt.getItaimg());
        List<GoodsSelectDetailResponseData.UploadAddImage> uploadAddImageList = this.makeUploadAddImageList(itasrt.getItaimg());
        goodsSelectDetailResponseData.setDescription(descriptions);
        goodsSelectDetailResponseData.setAttributes(attributesList);
        goodsSelectDetailResponseData.setItems(itemsList);
        goodsSelectDetailResponseData.setUploadMainImage(uploadMainImageList);
        goodsSelectDetailResponseData.setUploadAddImage(uploadAddImageList);
        goodsSelectDetailResponseData.setDeleteImage(new ArrayList<>());
        goodsSelectDetailResponseData = goodsSelectDetailResponseDataMapper.nullToEmpty(goodsSelectDetailResponseData);
        return goodsSelectDetailResponseData;
    }

    private List<GoodsSelectDetailResponseData.UploadAddImage> makeUploadAddImageList(List<Itaimg> itaimgList) {
        List<GoodsSelectDetailResponseData.UploadAddImage> uploadAddImageList = new ArrayList<>();
        if(itaimgList == null){
            log.debug("itasrt.itaimgList가 존재하지 않습니다.");
            return uploadAddImageList;
        }
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
        if(itaimgList == null){
            log.debug("itasrt.itaimgList가 존재하지 않습니다.");
            return uploadMainImageList;
        }
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
        if(ititmmList == null){
            log.debug("itasrt.ititmmList가 존재하지 않습니다.");
            return itemsList;
        }
        for(Ititmm ititmm : ititmmList){
            GoodsSelectDetailResponseData.Items item = new GoodsSelectDetailResponseData.Items();
            item.setItemId(ititmm.getItemId());
            Itvari op1 = ititmm.getItvari1();//jpaItvariRepository.findByAssortIdAndSeq(ititmm.getAssortId(), ititmm.getVariationSeq1());
            String optionNm = op1 == null? null : op1.getOptionNm();
            String seq = op1 == null? null : op1.getSeq();
			item.setValue1(optionNm);
			item.setSeq1(seq);
			item.setStatus1(StringFactory.getStrR()); // r 하드코딩
            if(ititmm.getVariationSeq2() != null){
                Itvari op2 = ititmm.getItvari2();//jpaItvariRepository.findByAssortIdAndSeq(ititmm.getAssortId(), ititmm.getVariationSeq2());
                optionNm = op2 == null? null : op2.getOptionNm();
                seq = op2 == null? null : op2.getSeq();
				item.setSeq2(optionNm);
				item.setValue2(seq);
				item.setStatus2(StringFactory.getStrR()); // r 하드코딩
            }
            if(ititmm.getVariationSeq3() != null){
                Itvari op3 = ititmm.getItvari3();//jpaItvariRepository.findByAssortIdAndSeq(ititmm.getAssortId(), ititmm.getVariationSeq2());
                optionNm = op3 == null? null : op3.getOptionNm();
                seq = op3 == null? null : op3.getSeq();
                item.setSeq3(optionNm);
                item.setValue3(seq);
                item.setStatus3(StringFactory.getStrR()); // r 하드코딩
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
        if(itvariList == null){
            log.debug("itasrt.itvariList가 존재하지 않습니다.");
            return attributesList;
        }
        for(Itvari itvari : itvariList){
            GoodsSelectDetailResponseData.Attributes attr = new GoodsSelectDetailResponseData.Attributes(itvari);
            attributesList.add(attr);
        }
        return attributesList;
    }

    // itasrd -> description 형태로 바꿔주는 함수
    private List<GoodsSelectDetailResponseData.Description> makeDescriptions(List<Itasrd> itasrdList) {
        List<GoodsSelectDetailResponseData.Description> descriptionList = new ArrayList<>();
        if(itasrdList == null){
            log.debug("itasrt.itasrdList가 존재하지 않습니다.");
            return descriptionList;
        }
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
    public GoodsSelectListResponseData getGoodsList(String shortageYn, LocalDate regDtBegin, LocalDate regDtEnd, String assortId, String assortNm) {
        LocalDateTime start = regDtBegin.atStartOfDay();
        LocalDateTime end = regDtEnd.atTime(23,59,59);
        TypedQuery<Itasrt> query =
                em.createQuery("select t from Itasrt t " +
                                "left join fetch t.itcatg c " +
                                "where t.regDt " +
                                "between ?1 and ?2 " +
                                "and (?3 is null or trim(?3)='' or t.shortageYn = ?3) " +
                                "and (?4 is null or trim(?4)='' or t.assortId = ?4) " +
                                "and (?5 is null or trim(?5)='' or t.assortNm like concat('%',?5,'%'))"
                        , Itasrt.class);
        query.setParameter(1, start)
                .setParameter(2, end)
                .setParameter(3, shortageYn).setParameter(4,assortId).setParameter(5,assortNm);
        List<Itasrt> itasrtList = query.getResultList();
        List<GoodsSelectListResponseData.Goods> goodsList = new ArrayList<>();
        for(Itasrt itasrt : itasrtList){
            GoodsSelectListResponseData.Goods goods = new GoodsSelectListResponseData.Goods(itasrt);
            IfBrand ifBrand = jpaIfBrandRepository.findByChannelGbAndChannelBrandId(StringFactory.getGbOne(),itasrt.getBrandId()); // 채널은 01 하드코딩
            goods.setBrandNm(ifBrand==null? null:ifBrand.getBrandNm());
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
    	Itaimg r = jpaItaimgRepository.findById(uid).orElse(null);
    	
    	return r;
    	
    }
    
    @Transactional
    public void deleteItaimg(Itaimg ii) {
    	
     jpaItaimgRepository.delete(ii);

    }


    @Transactional
    public void batchSizeTest() {
        Itasrt itasrt = jpaItasrtRepository.findById("000075775").orElseGet(()->null);
    }
}
