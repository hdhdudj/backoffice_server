package io.spring.service.goods;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import io.spring.model.goods.response.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.infrastructure.mapstruct.GoodsSelectDetailResponseDataMapper;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.category.JpaIfCategoryRepository;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.goods.JpaIfBrandRepository;
import io.spring.jparepos.goods.JpaItaimgRepository;
import io.spring.jparepos.goods.JpaItasrdRepository;
import io.spring.jparepos.goods.JpaItasrnRepository;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmdRepository;
import io.spring.jparepos.goods.JpaItitmmRepository;
import io.spring.jparepos.goods.JpaItvariRepository;
import io.spring.jparepos.goods.JpaTmitemRepository;
import io.spring.jparepos.goods.JpaTmmapiRepository;
import io.spring.model.file.FileVo;
import io.spring.model.goods.entity.IfCategory;
import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.entity.Itasrd;
import io.spring.model.goods.entity.Itasrn;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Itbrnd;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmd;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.goods.entity.Tmitem;
import io.spring.model.goods.entity.Tmmapi;
import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.vendor.entity.Cmvdmr;
import io.spring.service.file.FileService;
import io.spring.service.stock.JpaStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

	private final JpaStockService jpaStockService;

    private final EntityManager em;

    private final GoodsSelectDetailResponseDataMapper goodsSelectDetailResponseDataMapper;

    public Optional<Itasrt> findById(String goodsId) {
        Optional<Itasrt> goods = jpaItasrtRepository.findById(goodsId);
        return goods;
    }

    /**
     * goods ?????? insert ????????? ??????
     * Pecan 21-04-26
     * @param goodsInsertRequestData
     * @return GoodsResponseData
     */

	// 20220307 rjb80 requestbody ??????
    @Transactional
    public String sequenceInsertOrUpdateGoods(GoodsInsertRequestData goodsInsertRequestData){

		String userId = goodsInsertRequestData.getUserId();

        // itasrt??? goods ?????? ??????
        Itasrt itasrt = this.saveItasrt(goodsInsertRequestData);
        // tmmapi??? ??????
        this.saveTmmapi(itasrt);
        // itasrn??? goods ?????? ??????
		Itasrn itasrn = this.saveItasrn(goodsInsertRequestData, userId);
        // itasrd??? ?????? ??????
		List<Itasrd> itasrd = this.saveItasrd(goodsInsertRequestData, userId);
        // itvari??? assort_id??? ???????????? ??????(??????, ?????????)
        List<Itvari> existItvariList = jpaItvariRepository.findByAssortId(goodsInsertRequestData.getAssortId());
		List<Itvari> itvariList = this.saveItvariList(goodsInsertRequestData, existItvariList, userId);
        // ititmm??? assort_id??? item ??????
        List<Ititmm> existItitmmList = jpaItitmmRepository.findByAssortId(goodsInsertRequestData.getAssortId());
		List<Ititmm> ititmmList = this.saveItemList(goodsInsertRequestData, existItitmmList, itvariList, userId);
        // tmitem??? ??????
        this.saveTmitem(ititmmList);
        // ititmd??? item ?????? ??????
		List<Ititmd> ititmdList = this.saveItemHistoryList(goodsInsertRequestData, ititmmList, userId);

        // itaimg??? assortId ???????????? ????????????
		this.updateItaimgAssortId(goodsInsertRequestData, itasrt.getAssortId(), userId);
//        List<GoodsInsertResponseData.Attributes> attributesList = this.makeGoodsResponseAttributes(itvariList);
//        List<GoodsInsertResponseData.Items> itemsList = this.makeGoodsResponseItems(ititmmList, itvariList);
//        return this.makeGoodsInsertResponseData(goodsInsertRequestData, attributesList, itemsList);
        return itasrt.getAssortId();
    }

    

    /**
     * Pecan
     * tmitem : insert, update ?????? ??????
      */
    private void saveTmitem(List<Ititmm> ititmmList) {
        for(Ititmm ititmm : ititmmList){
            Tmitem tmitem = jpaTmitemRepository.findByChannelGbAndAssortIdAndItemId(StringFactory.getGbOne(), ititmm.getAssortId(), ititmm.getItemId())
                    .orElseGet(() -> new Tmitem(ititmm)); // channelGb 01 ????????????

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
     * tmmapi : insert, update ?????? ??????
     * @param itasrt
     */
    private void saveTmmapi(Itasrt itasrt){
        Tmmapi tmmapi = jpaTmmapiRepository.findByChannelGbAndAssortId(StringFactory.getGbOne(), itasrt.getAssortId()).orElseGet(() -> null);
        if(tmmapi == null){ // insert
            tmmapi = new Tmmapi(itasrt); // channelGb 01 ????????????
            tmmapi.setUploadType(StringFactory.getGbOne()); // 01 : ??????, 02 : ????????????(??????)
        }
        else{ // update
            tmmapi.setUploadType(StringFactory.getGbTwo()); // 01 : ??????, 02 : ????????????(??????)
        }
        tmmapi.setJoinStatus(StringFactory.getGbTwo()); // 01 : ????????? ?????? ??????, 02 : ?????? ???????????? ????????? ?????? ?????? ?????? (01??? ????????? ??? batch??????)
        tmmapi.setUploadYn(StringFactory.getGbTwo()); // 01 : ????????? ??????, 02 : ????????? ?????????
        tmmapi.setAssortNm(itasrt.getAssortNm());
        tmmapi.setStandardPrice(itasrt.getLocalPrice());
        tmmapi.setSalePrice(itasrt.getLocalSale());
        tmmapi.setShortageYn(itasrt.getShortageYn());
//        jpaTmmapiRepository.save(tmmapi);
        em.persist(tmmapi);
    }

    /**
     * Pecan
     * itaimg??? ????????? assortId ???????????? ??????
     * @param goodsInsertRequestData
     * @param assortId
     */
	private void updateItaimgAssortId(GoodsInsertRequestData goodsInsertRequestData, String assortId, String userId) {
        List<GoodsInsertRequestData.UploadMainImage> uploadMainImageList = goodsInsertRequestData.getUploadMainImage();
        List<GoodsInsertRequestData.UploadAddImage> uploadAddImageList = goodsInsertRequestData.getUploadAddImage();
        for(GoodsInsertRequestData.UploadMainImage uploadMainImage : uploadMainImageList){
            Itaimg itaimg = jpaItaimgRepository.findById(uploadMainImage.getUid()).orElseGet(() -> null);
            itaimg.setAssortId(assortId);

			itaimg.setUpdId(userId);

            jpaItaimgRepository.save(itaimg);
        }
        for(GoodsInsertRequestData.UploadAddImage uploadAddImage : uploadAddImageList){
            Itaimg itaimg = jpaItaimgRepository.findById(uploadAddImage.getUid()).orElseGet(() -> null);
            itaimg.setAssortId(assortId);

			itaimg.setUpdId(userId);

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
     * ?????? ?????? ?????? insert, update
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
        // ????????? ????????? ?????? ??????????????? ?????? ??????. ???????????? ?????? ?????? 1???, ?????? ?????? ????????? ?????? ????????? 1?????? ???????????? ???.
		itasrt.setOptionUseYn(goodsInsertRequestData.getOptionUseYn());

//        jpaItasrtRepository.save(itasrt);
        em.persist(itasrt);
        return itasrt;
    }

    // ?????? ??????????????? ????????? ?????????????????? ????????????
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
     * ?????? ?????? ?????? insert, update
     * @param goodsInsertRequestData
     * @return Itasrn Object
     */
	private Itasrn saveItasrn(GoodsInsertRequestData goodsInsertRequestData, String userId) {
//        ItasrnId itasrnId = new ItasrnId(goodsRequestData);
        LocalDateTime effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT()); // ????????? ??????(?????? ?????? 9999-12-31 23:59:59?)
        Itasrn itasrn = jpaItasrnRepository.findByAssortIdAndEffEndDt(goodsInsertRequestData.getAssortId(), effEndDt);
        if(itasrn == null){ // insert
            itasrn = new Itasrn(goodsInsertRequestData);
        }
        else{ // update
            itasrn.setEffEndDt(LocalDateTime.now().minusSeconds(1));
            // update ??? ??? ?????? insert
            Itasrn newItasrn = new Itasrn(itasrn);

			newItasrn.setRegId(userId);
			newItasrn.setUpdId(userId);

            jpaItasrnRepository.save(newItasrn);
        }
        itasrn.setLocalSale(goodsInsertRequestData.getLocalSale() == null || goodsInsertRequestData.getLocalSale().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalSale()));
        itasrn.setShortageYn(goodsInsertRequestData.getShortageYn());
//        jpaItasrnRepository.save(itasrn);

		itasrn.setUpdId(userId);

        jpaItasrnRepository.save(itasrn);
        return itasrn;
    }

    /**
     * 21-04-28 Pecan
     * ??????(??? ???, ?????? ???) insert, update
     * @param goodsInsertRequestData
     * @return List<Itasrd>
     */
	private List<Itasrd> saveItasrd(GoodsInsertRequestData goodsInsertRequestData, String userId) {
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
//                if (seq == null || seq.trim().equals("")) { // insert -> ??? ?????????
//                    seq = StringFactory.getFourStartCd();//fourStartCd;
//                }
//                else{ // insert -> ??? ?????????
//                    seq = Utilities.plusOne(seq, 4);
//                }
                if(description.getOrdDetCd().equals(StringFactory.getGbOne())){
                    seq = StringFactory.getFourStartCd(); // 0001
                }
                else if(description.getOrdDetCd().equals(StringFactory.getGbTwo())){
                    seq = StringFactory.getFourSecondCd(); // 0002
                }
                itasrd.setSeq(seq);
				itasrd.setRegId(userId);

            }
            else{ // update
//                itasrd = jpaItasrdRepository.findByAssortIdAndSeq(goodsInsertRequestData.getAssortId(), seq);
                itasrd.setOrdDetCd(descriptionList.get(i).getOrdDetCd());
                itasrd.setMemo(descriptionList.get(i).getMemo());
                itasrd.setTextHtmlGb(descriptionList.get(i).getTextHtmlGb());
            }
//            jpaItasrdRepository.save(itasrd);

			itasrd.setUpdId(userId);

            jpaItasrdRepository.save(itasrd);
            itasrdList.add(itasrd);
        }
        return itasrdList;
    }

    /**
     * 21-04-28 Pecan
     * ?????? ?????? insert, update
     * @param goodsInsertRequestData
     * @return List<Itvari>
     */
	private List<Itvari> saveItvariList(GoodsInsertRequestData goodsInsertRequestData, List<Itvari> existItvariList,
			String userId) {

        List<Itvari> itvariList;
        if(existItvariList == null || existItvariList.size() == 0){
			itvariList = this.insertItvariList(goodsInsertRequestData, userId);
        }
        else{
			itvariList = this.updateItvariList(goodsInsertRequestData, existItvariList, userId);
        }
        return itvariList;
    }

    /**
     * ?????? insert ???
     */
	private List<Itvari> insertItvariList(GoodsInsertRequestData goodsInsertRequestData, String userId) {
		List<Itvari> itvariList = saveSingleOption(goodsInsertRequestData, userId);
        if(goodsInsertRequestData.getOptionUseYn().equals(StringFactory.getGbTwo())){ // optionUseYn??? 02, ??? ????????? ??????
            return itvariList; // ?????? ?????? 1?????? ???????????? ??????
        }
        List<GoodsInsertRequestData.Attributes> attributes = goodsInsertRequestData.getAttributes();
        if(attributes.size() > 0){
            itvariList.get(0).setDelYn(StringFactory.getGbOne());

			itvariList.get(0).setUpdId(userId);

            jpaItvariRepository.save(itvariList.get(0));
        }
        else{
            return itvariList;
        }
        Set<String> seqList = new HashSet<>();
        seqList.add(itvariList.get(0).getSeq());
        for(GoodsInsertRequestData.Attributes attribute : attributes){
            Itvari itvari = new Itvari(goodsInsertRequestData);
            String seq = Utilities.plusOne(this.findMaxSeq(seqList), 4);//jpaItvariRepository.findMaxSeqByAssortId(assortId);
            itvari.setSeq(seq);
            itvari.setOptionNm(attribute.getValue());
            itvari.setOptionGb(attribute.getVariationGb());
            itvari.setVariationGb(attribute.getVariationGb());
            itvariList.add(itvari);
            seqList.add(seq);

			itvari.setRegId(userId);

			itvari.setUpdId(userId);

            jpaItvariRepository.save(itvari);
        }
        return itvariList;
    }

    /**
     * ?????? update ???
     */
	private List<Itvari> updateItvariList(GoodsInsertRequestData goodsInsertRequestData, List<Itvari> existItvariList,
			String userId) {
        List<GoodsInsertRequestData.Attributes> attributes = goodsInsertRequestData.getAttributes();
        List<Itvari> itvariList = new ArrayList<>();
        Set<String> seqList = new HashSet<>();
        Set<String> removeSeqList = new HashSet<>();
        for(Itvari itvari : existItvariList){
            seqList.add(itvari.getSeq());
            removeSeqList.add(itvari.getSeq());
        }

        for(GoodsInsertRequestData.Attributes attribute : attributes){
            List<Itvari> origItvariList = existItvariList.stream().filter(x->x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getSeq().equals(attribute.getSeq()))
                    .collect(Collectors.toList());
            Itvari itvari = origItvariList.size() > 0? origItvariList.get(0) : null;
            String seq = attribute.getSeq();
//            Itvari itvari = new Itvari(goodsInsertRequestData);
//            itvari.setAssortId(goodsInsertRequestData.getAssortId());
            if(!seqList.contains(seq) && !seq.trim().equals("")){
                log.debug("?????? itvari??? seqList??? " + seq + "??? ???????????? ????????????.");
                continue;
            }
//            if(seq == null || seq.trim().equals("")){ // seq??? ???????????? ?????? ?????? == ????????? itvari INSERT -> seq max ??? ????????? ???
            if(itvari == null){ // seq??? ???????????? ?????? ?????? == ????????? itvari INSERT -> seq max ??? ????????? ???
                itvari = new Itvari(goodsInsertRequestData);
                seq = this.findMaxSeq(seqList);//jpaItvariRepository.findMaxSeqByAssortId(assortId);
                if(seq == null){ // max?????? ?????? -> ?????? assort id?????? ??? insert
                    seq = StringFactory.getFourStartCd();//fourStartCd;
                }
                else{ // max??? ?????? -> seq++
                    seq = Utilities.plusOne(seq, 4);
                }
                itvari.setSeq(seq);
                seqList.add(seq);
				itvari.setRegId(userId);
            }
            else { // ???????????? ?????? : itvari ????????? ???????????? ????????? -> update
//                itvari = existItvariList.stream().filter(x->x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getSeq().equals(attribute.getSeq()))
//                        .collect(Collectors.toList()).get(0);//jpaItvariRepository.findByAssortIdAndSeq(goodsInsertRequestData.getAssortId(), seq);
                if(itvari.getDelYn().equals(StringFactory.getGbOne()) || itvari.getSeq().equals(StringFactory.getFourStartCd())){ // ????????? ???????????? seq 0001??? itvari??? ??????x
                    log.debug("delYn??? 01????????? seq??? 0001(??????)??? itvari??? update??? ??? ????????????.");
                    continue;
                }
                removeSeqList.remove(seq);
            }
            itvari.setOptionNm(attribute.getValue());
            itvari.setOptionGb(attribute.getVariationGb());
            itvari.setVariationGb(attribute.getVariationGb());
            itvariList.add(itvari);

			itvari.setUpdId(userId);

            jpaItvariRepository.save(itvari);
        }

        for(Itvari i : existItvariList){
            if(removeSeqList.contains(i.getSeq())){
                i.setDelYn(StringFactory.getGbOne());
            }

			i.setUpdId(userId);

            jpaItvariRepository.save(i);
        }

        int itvariDelNo = 0;
        for(Itvari i : itvariList){
            if(i.getDelYn().equals(StringFactory.getGbTwo())){
                itvariDelNo++;
            }
        }
        if(itvariDelNo == 0){
            Itvari singleItvari = existItvariList.stream().filter(x->x.getSeq().equals(StringFactory.getFourStartCd())).collect(Collectors.toList()).get(0);
            singleItvari.setDelYn(StringFactory.getGbTwo());

			singleItvari.setUpdId(userId);

            jpaItvariRepository.save(singleItvari);
        }
        return itvariList;
    }

    /**
     * seq??? ??? ??????????????? seq??? ???????????? ?????????
     */
    private String findMaxSeq(Set<String> seqList) {
        int max = -1;
        String maxSeq = "";
        for(String seq : seqList){
            if(max <= Integer.parseInt(seq)){
                max = Integer.parseInt(seq);
                maxSeq = seq;
            }
        }
        return maxSeq;
    }

    /**
     * seq??? ???????????? ???????????? ??????
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
     * ?????? ?????? 1?????? ???????????? ??????
     * @param goodsInsertRequestData
     * @return
     */
	private List<Itvari> saveSingleOption(GoodsInsertRequestData goodsInsertRequestData, String userId) {
        List<Itvari> itvariList = new ArrayList<>();
        Itvari itvari = jpaItvariRepository.findByAssortIdAndSeq(goodsInsertRequestData.getAssortId(), StringFactory.getFourStartCd());
        if(itvari == null){
            itvari = new Itvari(goodsInsertRequestData);
            itvari.setSeq(StringFactory.getFourStartCd()); // 0001 ????????????
            itvari.setOptionGb(StringFactory.getGbOne()); // 01 ????????????
            itvari.setImgYn(StringFactory.getGbTwo()); // 02 ????????????
            itvari.setOptionNm(StringFactory.getStrSingleGoods()); // '??????' ????????????
            itvari.setVariationGb(StringFactory.getGbOne()); // 01 ????????????
//        jpaItvariRepository.save(itvari);
        }
        itvari.setDelYn(StringFactory.getGbTwo()); // 02 ????????????

		itvari.setUpdId(userId);

        jpaItvariRepository.save(itvari);
        itvariList.add(itvari);
        return itvariList;
    }

    /**
     * 21-04-28 Pecan
     * ????????? ?????? insert, update
     * @param goodsInsertRequestData
     * @return List<Ititmm>
     */
	private List<Ititmm> saveItemList(GoodsInsertRequestData goodsInsertRequestData, List<Ititmm> existItitmmList,
			List<Itvari> itvariList, String userId) {

        List<Ititmm> ititmmList;

        if(existItitmmList == null || existItitmmList.size() == 0){
			ititmmList = this.insertItitmmList(goodsInsertRequestData, itvariList, userId);
        }
        else{
			ititmmList = this.updateItitmmList(goodsInsertRequestData, existItitmmList, itvariList, userId);
        }
        return ititmmList;
    }

    /**
     * ?????? insert ??? ititmm ??????
     */
	private List<Ititmm> insertItitmmList(GoodsInsertRequestData goodsInsertRequestData, List<Itvari> itvariList,
			String userId) {
		List<Ititmm> ititmmList = this.saveSingleItem(goodsInsertRequestData, userId);
        if(goodsInsertRequestData.getOptionUseYn().equals(StringFactory.getGbTwo())){ // optionUseYn??? 02, ??? ????????? ??????
            return ititmmList; // ?????? ?????? 1?????? ???????????? ??????
        }
        List<GoodsInsertRequestData.Items> itemList = goodsInsertRequestData.getItems();
        if(itemList.size() > 0){
            ititmmList.get(0).setDelYn(StringFactory.getGbOne());

			ititmmList.get(0).setUpdId(userId);

            jpaItitmmRepository.save(ititmmList.get(0));
        }
        else{
            return ititmmList;
        }
        Set<String> seqList = new HashSet<>();
        seqList.add(ititmmList.get(0).getItemId());
        for(GoodsInsertRequestData.Items items : itemList){
            Ititmm ititmm = new Ititmm(goodsInsertRequestData);
            String itemId = Utilities.plusOne(this.findMaxSeq(seqList),4);
            ititmm.setItemId(itemId);
			ititmm.setRegId(userId);

            Itvari op1 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getOptionNm().equals(items.getVariationValue1()))
                    .collect(Utilities.toSingleton());
            if(op1 != null){
                ititmm.setVariationGb1(op1.getOptionGb());
                ititmm.setVariationSeq1(op1.getSeq());
            }
            // ??????2 ????????? ????????????
            Itvari op2 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getOptionNm().equals(items.getVariationValue2()))
                    .collect(Utilities.toSingleton());
            if(op2 != null){
                ititmm.setVariationGb2(op2.getOptionGb());
                ititmm.setVariationSeq2(op2.getSeq());
            }
            // ??????3 ????????? ????????????
            Itvari op3 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getOptionNm().equals(items.getVariationValue3()))
                    .collect(Utilities.toSingleton());
            if(op3 != null){
                ititmm.setVariationGb3(op3.getOptionGb());
                ititmm.setVariationSeq3(op3.getSeq());
            }
            ititmm.setAddPrice(items.getAddPrice() == null || items.getAddPrice().trim().equals("")? null : Float.parseFloat(items.getAddPrice()));
            ititmm.setShortYn(items.getShortYn());
            seqList.add(itemId);

			ititmm.setUpdId(userId);

            jpaItitmmRepository.save(ititmm);


        }
        return ititmmList;
    }

    /**
     * ?????? update ??? itimm ??????
     */
	private List<Ititmm> updateItitmmList(GoodsInsertRequestData goodsInsertRequestData, List<Ititmm> existItitmmList,
			List<Itvari> itvariList, String userId) {
        List<GoodsInsertRequestData.Items> itemList = goodsInsertRequestData.getItems();
        List<Ititmm> ititmmList = new ArrayList<>();
        Set<String> itemIdList = new HashSet<>();
        Set<String> removeItemIdList = new HashSet<>();
        for(Ititmm i : existItitmmList){
            itemIdList.add(i.getItemId());
            removeItemIdList.add(i.getItemId());
        }

        for(GoodsInsertRequestData.Items item : itemList){
            List<Ititmm> origItitmmList = existItitmmList.stream().filter(x->x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getItemId().equals(item.getItemId())).collect(Collectors.toList());
            Ititmm ititmm = origItitmmList.size() > 0? origItitmmList.get(0) : null;//jpaItitmmRepository.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(), itemId);
            String itemId = item.getItemId(); // item id??? ????????? ?????? ????????? ????????? ??????
//            Ititmm ititmm = new Ititmm(goodsInsertRequestData.getAssortId(), item);
//            if(itemId == null || itemId.trim().equals("")){ // ????????? item id??? ????????? jpa?????? max?????? ?????????
            if(!itemIdList.contains(itemId) && !itemId.trim().equals("")){
                log.debug("?????? ititmm??? itemIdList??? " + itemId + "??? ???????????? ????????????.");
                continue;
            }
            if(ititmm == null){ // ????????? item id??? ????????? jpa?????? max?????? ?????????
                ititmm = new Ititmm(goodsInsertRequestData.getAssortId(), item);
                itemId = this.findMaxSeq(itemIdList);//jpaItitmmRepository.findMaxItemIdByAssortId(goodsInsertRequestData.getAssortId());
                if(itemId == null || itemId.trim().equals("")){ // jpa?????? max?????? ??????????????? null?????? ?????? assort id??? item id??? ???????????? ???????????? ?????????(0001)??? ??????
                    itemId = StringFactory.getFourStartCd();
                }
                else { // jpa?????? max?????? ????????? ?????? 1??? ?????? ??? item id??? ??????
                    itemId = Utilities.plusOne(itemId, 4);
                }
                ititmm.setItemId(itemId);
				ititmm.setRegId(userId);

                itemIdList.add(itemId);
            }
            else { // ???????????? ?????? : itvari ????????? ???????????? ????????? -> update
                if(ititmm.getDelYn().equals(StringFactory.getGbOne()) || ititmm.getItemId().equals(StringFactory.getFourStartCd())){ // ????????? ???????????? seq 0001??? itvari??? ??????x
                    log.debug("delYn??? 01????????? itemId??? 0001(??????)??? ititmm??? update??? ??? ????????????.");
                    continue;
                }
                removeItemIdList.remove(itemId);
            }

            // ??????1 ????????? ????????????
            Itvari op1 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getVariationGb().equals(StringFactory.getGbOne()) && x.getOptionNm().equals(item.getVariationValue1()))
                    .collect(Utilities.toSingleton());
            if(op1 != null){
                ititmm.setVariationGb1(op1.getOptionGb());
                ititmm.setVariationSeq1(op1.getSeq());
            }
            // ??????2 ????????? ????????????
            Itvari op2 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getVariationGb().equals(StringFactory.getGbTwo()) && x.getOptionNm().equals(item.getVariationValue2()))
                    .collect(Utilities.toSingleton());
            if(op2 != null){
                ititmm.setVariationGb2(op2.getOptionGb());
                ititmm.setVariationSeq2(op2.getSeq());
            }
            // ??????3 ????????? ????????????
            Itvari op3 = itvariList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getVariationGb().equals(StringFactory.getGbThree()) && x.getOptionNm().equals(item.getVariationValue3()))
                    .collect(Utilities.toSingleton());
            if(op3 != null){
                ititmm.setVariationGb3(op3.getOptionGb());
                ititmm.setVariationSeq3(op3.getSeq());
            }
            ititmm.setAddPrice(item.getAddPrice() == null || item.getAddPrice().trim().equals("")? null : Float.parseFloat(item.getAddPrice()));
            ititmm.setShortYn(item.getShortYn());
//            jpaItitmmRepository.save(ititmm);
//            System.out.println("===== : " + ititmm.toString());

			ititmm.setUpdId(userId);

            jpaItitmmRepository.save(ititmm);
            ititmmList.add(ititmm);
        }
        for(Ititmm i : existItitmmList){
            if(removeItemIdList.contains(i.getItemId())){
                i.setDelYn(StringFactory.getGbOne());
            }

			i.setUpdId(userId);
            jpaItitmmRepository.save(i);
        }

        int ititmmDelNo = 0;
        for(Ititmm i : ititmmList){
            if(i.getDelYn().equals(StringFactory.getGbTwo())){
                ititmmDelNo++;
            }
        }
        if(ititmmDelNo == 0){
            Ititmm singleItitmm = existItitmmList.stream().filter(x->x.getItemId().equals(StringFactory.getFourStartCd())).collect(Collectors.toList()).get(0);
            singleItitmm.setDelYn(StringFactory.getGbTwo());
			singleItitmm.setUpdId(userId);
            jpaItitmmRepository.save(singleItitmm);
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
//            String itemId = item.getItemId(); // item id??? ????????? ?????? ????????? ????????? ??????
//            Ititmm ititmm = new Ititmm(goodsInsertRequestData.getAssortId(), item);
//            if(itemId == null || itemId.trim().equals("")){ // ????????? item id??? ????????? jpa?????? max?????? ?????????
//                itemId = jpaItitmmRepository.findMaxItemIdByAssortId(goodsInsertRequestData.getAssortId());
//                if(itemId == null || itemId.trim().equals("")){ // jpa?????? max?????? ??????????????? null?????? ?????? assort id??? item id??? ???????????? ???????????? ?????????(0001)??? ??????
//                    itemId = StringFactory.getFourStartCd();
//                }
//                else { // jpa?????? max?????? ????????? ?????? 1??? ?????? ??? item id??? ??????
//                    itemId = Utilities.plusOne(itemId, 4);
//                }
//                ititmm.setItemId(itemId);
//            }
//            else{ // ????????? item id??? ????????? ?????? ????????? ?????? ??????????????? ????????? ????????? (update)
//                ititmm = jpaItitmmRepository.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(), itemId);
//            }
//            System.out.println("1 : "+System.currentTimeMillis());
//            // ??????1 ????????? ????????????
//            HashMap<String, Object> op1 = myBatisGoodsDao.selectOneSeqOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue1());//jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue1());
//            if(op1 != null){
//                ititmm.setVariationGb1((String)op1.get("optionGb"));
//                ititmm.setVariationSeq1((String)op1.get("seq"));
//            }
//            System.out.println("2 : "+System.currentTimeMillis());
//            // ??????2 ????????? ????????????
//            HashMap<String, Object> op2 = myBatisGoodsDao.selectOneSeqOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue2()); //Itvari op2 = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue2());
//            if(op2 != null){
//                ititmm.setVariationGb2((String)op2.get("optionGb"));
//                ititmm.setVariationSeq2((String)op2.get("seq"));
//            }
//            System.out.println("3 : "+System.currentTimeMillis());
////            String[] optionNmList = item.getValue().split(StringFactory.getSplitGb());
////            // itvari?????? ?????? ?????? ????????????
////            for(String optionNm : optionNmList){
////                Itvari op = jpaItvariRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), optionNm);
////                String opGb = op.getOptionGb();
////                if(opGb.equals(StringFactory.getGbOne())){ // optionGb??? 01??? ??????
////                    ititmm.setVariationGb1(opGb);
////                    ititmm.setVariationSeq1(op.getSeq());
////                }
////                else if(opGb.equals(StringFactory.getGbTwo())){ // optionGb??? 02??? ??????
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
     * ?????? ????????? ?????? ????????? 1?????? ???????????? ??????
     * @param goodsInsertRequestData
     * @return
     */
	private List<Ititmm> saveSingleItem(GoodsInsertRequestData goodsInsertRequestData, String userId) {
        List<Ititmm> ititmmList = new ArrayList<>();
        Ititmm ititmm = jpaItitmmRepository.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(), StringFactory.getFourStartCd());
        if(ititmm == null){
            ititmm = new Ititmm(goodsInsertRequestData);
            ititmm.setItemId(StringFactory.getFourStartCd()); // 0001
            ititmm.setVariationGb1(StringFactory.getGbOne()); // 01
            ititmm.setVariationSeq1(StringFactory.getFourStartCd()); // 0001

			ititmm.setRegId(userId);
			ititmm.setUpdId(userId);

            jpaItitmmRepository.save(ititmm);
//            em.persist(ititmm);
        }
        else {
            ititmm.setDelYn(StringFactory.getGbTwo()); // ?????? ???????????? ??? ????????????
        }
        ititmmList.add(ititmm);

        return ititmmList;
    }

    /**
     * 21-04-28 Pecan
     * ????????? ?????? ?????? insert, update
     * @param goodsInsertRequestData
     * @param ititmmList
     * @return List<Ititmd>
     */
	private List<Ititmd> saveItemHistoryList(GoodsInsertRequestData goodsInsertRequestData, List<Ititmm> ititmmList,
			String userId) {
        List<Ititmd> ititmdList = new ArrayList<>();
        LocalDateTime effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT()); // ????????? ??????(?????? ?????? 9999-12-31 23:59:59?)
        List<Ititmd> allItitmdList = jpaItitmdRepository.findAll();
        for (Ititmm ititmm : ititmmList) {
            Ititmd ititmd = allItitmdList.stream().filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
            && x.getItemId().equals(ititmm.getItemId()) && x.getEffEndDt().equals(effEndDt)).collect(Utilities.toSingleton());
//            Ititmd ititmd = jpaItitmdRepository.findByAssortIdAndItemIdAndEffEndDt(goodsInsertRequestData.getAssortId(), ititmm.getItemId() , effEndDt);
            if(ititmd == null){ // insert
                ititmd = new Ititmd(ititmm);

				ititmd.setRegId(userId);
            }
            else{ // update
                LocalDateTime newDate = LocalDateTime.now().minusSeconds(1);
                ititmd.setEffEndDt(newDate);
                // update ??? ??? ?????? insert
                Ititmd newItitmd = new Ititmd(ititmd);
				newItitmd.setRegId(userId);
				newItitmd.setUpdId(userId);

                jpaItitmdRepository.save(newItitmd);
//            saveItasrn(goodsRequestData);
            }
            ititmd.setShortYn(ititmm.getShortYn());

			ititmd.setUpdId(userId);

            jpaItitmdRepository.save(ititmd);
        }

        return ititmdList;
    }

	public void updateById(String goodsId, Itasrt goods, String userId) {
        Optional<Itasrt> e = jpaItasrtRepository.findById(goodsId);
        if (e.isPresent()) {
            e.get().setAssortId(goods.getAssortId());
            e.get().setAssortNm(goods.getAssortNm());

			goods.setUpdId(userId);

            jpaItasrtRepository.save(goods);
        }
    }

    /**
     * 21-04-29 Pecan
     * assortId??? ?????? detail ???????????? ???????????? ????????? ???????????? ??????
     * @param assortId
     * @return GoodsResponseData
     */
    public GoodsSelectDetailResponseData getGoodsDetailPage(String assortId) {
        Itasrt itasrt = em.createQuery("select distinct(i) from Itasrt i " +
//                "left outer join fetch i.cmvdmr cv " +
//                "left outer join fetch i.ifBrand ib " +
                "left outer join fetch i.itvariList ivList " +
                "where i.assortId=?1", Itasrt.class).setParameter(1,assortId).getSingleResult();//jpaItasrtRepository.findById(assortId).orElseThrow(() -> new ResourceNotFoundException());
    	
//		System.out.println(itasrt);
        GoodsSelectDetailResponseData goodsSelectDetailResponseData = new GoodsSelectDetailResponseData(itasrt);

        // ??????????????????
        if(itasrt.getDispCategoryId() != null && !itasrt.getDispCategoryId().trim().equals("")){
            Cmvdmr cmvdmr = itasrt.getCmvdmr();
            goodsSelectDetailResponseData.setVendorNm(itasrt.getVendorId() != null && !itasrt.getVendorId().trim().equals("")? cmvdmr.getVdNm() : "");
        }
        // brand
		// IfBrand ifBrand;
		Itbrnd itbrnd;
        if(itasrt.getBrandId() != null && !itasrt.getBrandId().trim().equals("")){
			itbrnd = itasrt.getItbrnd();// jpaIfBrandRepository.findByChannelGbAndBrandId(StringFactory.getGbOne(),itasrt.getBrandId());
			goodsSelectDetailResponseData.setBrandNm(itbrnd == null ? null : itbrnd.getBrandNm());
        }
        List<GoodsSelectDetailResponseData.Description> descriptions = this.makeDescriptions(jpaItasrdRepository.findByAssortId(itasrt.getAssortId()));
        List<GoodsSelectDetailResponseData.Attributes> attributesList = this.makeAttributesList(itasrt.getItvariList());
        List<GoodsSelectDetailResponseData.Items> itemsList = this.makeItemsList(jpaItitmmRepository.findByAssortId(itasrt.getAssortId()));
        List<Itaimg> itaimgList = jpaItaimgRepository.findByAssortId(itasrt.getAssortId());
        List<GoodsSelectDetailResponseData.UploadMainImage> uploadMainImageList = this.makeUploadMainImageList(itaimgList.stream().filter(x->x.getImageGb().equals(StringFactory.getGbOne())).collect(Collectors.toList()));
        List<GoodsSelectDetailResponseData.UploadAddImage> uploadAddImageList = this.makeUploadAddImageList(itaimgList.stream().filter(x->x.getImageGb().equals(StringFactory.getGbTwo())).collect(Collectors.toList()));
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
            log.debug("itasrt.itaimgList??? ???????????? ????????????.");
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
            log.debug("itasrt.itaimgList??? ???????????? ????????????.");
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

    // ititmm -> items ????????? ???????????? ??????
    private List<GoodsSelectDetailResponseData.Items> makeItemsList(List<Ititmm> ititmmList) {
        ititmmList = ititmmList.stream().filter(x->x.getDelYn().equals(StringFactory.getGbTwo())).collect(Collectors.toList());
        List<GoodsSelectDetailResponseData.Items> itemsList = new ArrayList<>();
        if(ititmmList == null){
            log.debug("itasrt.ititmmList??? ???????????? ????????????.");
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
			item.setStatus1(StringFactory.getStrR()); // r ????????????
            if(ititmm.getVariationSeq2() != null){
                Itvari op2 = ititmm.getItvari2();//jpaItvariRepository.findByAssortIdAndSeq(ititmm.getAssortId(), ititmm.getVariationSeq2());
                optionNm = op2 == null? null : op2.getOptionNm();
                seq = op2 == null? null : op2.getSeq();
				item.setSeq2(seq);
				item.setValue2(optionNm);
				item.setStatus2(StringFactory.getStrR()); // r ????????????
            }
            if(ititmm.getVariationSeq3() != null){
                Itvari op3 = ititmm.getItvari3();//jpaItvariRepository.findByAssortIdAndSeq(ititmm.getAssortId(), ititmm.getVariationSeq2());
                optionNm = op3 == null? null : op3.getOptionNm();
                seq = op3 == null? null : op3.getSeq();
                item.setSeq3(seq);
                item.setValue3(optionNm);
                item.setStatus3(StringFactory.getStrR()); // r ????????????
            }
            item.setAddPrice(ititmm.getAddPrice() == null? null : ititmm.getAddPrice() + "");
			item.setShortageYn(ititmm.getShortYn());
            item = goodsSelectDetailResponseDataMapper.nullToEmpty(item);
            itemsList.add(item);
        }
        return itemsList;
    }

    // itvari -> attributes ????????? ???????????? ??????
    private List<GoodsSelectDetailResponseData.Attributes> makeAttributesList(List<Itvari> itvariList) {
        List<GoodsSelectDetailResponseData.Attributes> attributesList = new ArrayList<>();
        if(itvariList == null){
            log.debug("itasrt.itvariList??? ???????????? ????????????.");
            return attributesList;
        }
        for(Itvari itvari : itvariList){
            if(itvari.getDelYn().equals(StringFactory.getGbOne())){
                continue;
            }
            GoodsSelectDetailResponseData.Attributes attr = new GoodsSelectDetailResponseData.Attributes(itvari);
            attributesList.add(attr);
        }
        return attributesList;
    }

    // itasrd -> description ????????? ???????????? ??????
    private List<GoodsSelectDetailResponseData.Description> makeDescriptions(List<Itasrd> itasrdList) {
        List<GoodsSelectDetailResponseData.Description> descriptionList = new ArrayList<>();
        if(itasrdList == null){
            log.debug("itasrt.itasrdList??? ???????????? ????????????.");
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
     * brandId, dispCategoryId, regDt, shortageYn, (?????? itasrt) dispCategoryId(itcatg), brandId(itbrnd) ??? list ?????? ???????????? ??????
     * @param shortageYn, RegDtBegin, regDtEnd
     * @return GoodsSelectListResponseData
     */
    public GoodsSelectListResponseData getGoodsList(String shortageYn, LocalDate regDtBegin, LocalDate regDtEnd, String assortId, String assortNm) {
        boolean isAssortIdExist = assortId != null && !assortId.trim().equals("");
        boolean isAssortNmExist = assortNm != null && !assortNm.trim().equals("");

        LocalDateTime start = isAssortIdExist || isAssortNmExist? null : regDtBegin.atStartOfDay();
        LocalDateTime end = isAssortIdExist || isAssortNmExist? null : regDtEnd.atTime(23,59,59);
        GoodsSelectListResponseData goodsSelectListResponseData = new GoodsSelectListResponseData(shortageYn, regDtBegin, regDtEnd, assortId, assortNm);

        LocalDateTime oldDay = Utilities.strToLocalDateTime(StringFactory.getOldDayT());
        LocalDateTime doomsDay = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());

        List<Itasrt> itasrtList = jpaItasrtRepository.findMasterList(start, end, shortageYn, assortId, assortNm, oldDay, doomsDay);//query.getResultList();
        List<GoodsSelectListResponseData.Goods> goodsList = new ArrayList<>();
        if(itasrtList.size() == 0){
            log.debug("?????? ????????? ???????????? ????????? ???????????? ????????????.");
            goodsSelectListResponseData.setGoodsList(goodsList);
            return goodsSelectListResponseData;
        }
//		List<Itbrnd> brandList;
		// List<String> brandIdList = new ArrayList<>();
///        for(Itasrt itasrt : itasrtList){
		// if(!brandIdList.contains(itasrt.getBrandId())){
		// brandIdList.add(itasrt.getBrandId());
//            }
		// }
		// brandList =
		// jpaIfBrandRepository.findByBrandIdListByChannelIdAndBrandIdList(StringFactory.getGbOne(),
		// brandIdList);
		// brand
        for(Itasrt itasrt : itasrtList){
            GoodsSelectListResponseData.Goods goods = new GoodsSelectListResponseData.Goods(itasrt);
			// List<IfBrand> brandList1 =
			// brandList.stream().filter(x->x.getBrandId().equals(itasrt.getBrandId())).collect(Collectors.toList());
			// IfBrand ifBrand = brandList1 == null || brandList1.size() == 0? null :
			// brandList1.get(0);//jpaIfBrandRepository.findByChannelGbAndChannelBrandId(StringFactory.getGbOne(),itasrt.getBrandId());
			// // ????????? 01 ????????????
//            goods.setBrandNm(ifBrand==null? null:ifBrand.getBrandNm());
            goodsList.add(goods);
        }
        goodsSelectListResponseData.setGoodsList(goodsList);
        return goodsSelectListResponseData;
    }

    @Transactional
    public void changeVendor(String assortId, String channelGoodsNo, String vendorId){
        Itasrt itasrt = jpaItasrtRepository.findByChannelGoodsNoOrAssortId(channelGoodsNo, assortId);
        Itasrn itasrn = jpaItasrnRepository.findByAssortIdAndEffEndDt(itasrt.getAssortId(), Utilities.strToLocalDateTime(StringFactory.getDoomDayT()));
        itasrt.setVendorId(vendorId);
        itasrn.setEffEndDt(LocalDateTime.now());
        Itasrn newItasrn = new Itasrn(itasrn);
        jpaItasrnRepository.save(itasrn);
        jpaItasrnRepository.save(newItasrn);
        jpaItasrtRepository.save(itasrt);
    }

//    private GoodsInsertResponseData makeGoodsSelectListResponseData(List<Itasrt> goodsList) {
//        return null;
//    }
    

    @Transactional
	public Itaimg saveItaimg(String imageGb, FileVo f, String userId) {
    	Itaimg ii =new Itaimg(imageGb,f);

		ii.setRegId(userId);
		ii.setUpdId(userId);

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

	public GetStockListResponseData getStockList(String storageId, String purchaseVendorId, String assortId,
			String assortNm, String channelGoodsNo) {

		System.out.println("getGoodsList");
		List<Ititmc> ititmcList = jpaStockService.getItitmc(storageId, purchaseVendorId, assortId, assortNm);
		List<GetStockListResponseData.Goods> goodsList = new ArrayList<>();
		GetStockListResponseData ret = new GetStockListResponseData(storageId, purchaseVendorId, assortId, assortNm);
		for (Ititmc ititmc : ititmcList) {

			Tmmapi tmmapi = jpaTmmapiRepository
					.findByChannelGbAndAssortId(StringFactory.getGbOne(), ititmc.getAssortId()).orElseGet(() -> null);
			
			
			if (channelGoodsNo != null && channelGoodsNo.length() > 1) {

				if (tmmapi != null && tmmapi.getChannelGoodsNo().equals(channelGoodsNo)) {
					GetStockListResponseData.Goods goods = new GetStockListResponseData.Goods(ititmc);

					goods.setOrderQty(0L);
					goods.setAvailableQty(goods.getAvailableQty());
					goods.setChannelGoodsNo(tmmapi.getChannelGoodsNo());
					goodsList.add(goods);
				}

			} else {
				GetStockListResponseData.Goods goods = new GetStockListResponseData.Goods(ititmc);


				goods.setOrderQty(0L);
				goods.setAvailableQty(goods.getAvailableQty());

				String channelGoodsNo1 = tmmapi == null ? null : tmmapi.getChannelGoodsNo();
				goods.setChannelGoodsNo(channelGoodsNo1);
				goodsList.add(goods);
			}


		}

		ret.setGoods(goodsList);
		return ret;
	}
}
