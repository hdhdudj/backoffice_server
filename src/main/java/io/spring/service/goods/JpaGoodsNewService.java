package io.spring.service.goods;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.infrastructure.mapstruct.GoodsResponseDataMapper;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.category.JpaIfCategoryRepository;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.goods.JpaIfBrandRepository;
import io.spring.jparepos.goods.JpaItaimgRepository;
import io.spring.jparepos.goods.JpaItasrdRepository;
import io.spring.jparepos.goods.JpaItasrnRepository;
import io.spring.jparepos.goods.JpaItitmdRepository;
import io.spring.jparepos.goods.JpaTbGoodsAddInfoRepository;
import io.spring.jparepos.goods.JpaTbGoodsImageRepository;
import io.spring.jparepos.goods.JpaTbGoodsOptionRepository;
import io.spring.jparepos.goods.JpaTbGoodsOptionSupplierRepository;
import io.spring.jparepos.goods.JpaTbGoodsOptionValueRepository;
import io.spring.jparepos.goods.JpaTbGoodsRepository;
import io.spring.jparepos.goods.JpaTmitemRepository;
import io.spring.jparepos.goods.JpaTmmapiRepository;
import io.spring.model.file.FileVo;
import io.spring.model.goods.entity.IfCategory;
import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.entity.Itasrd;
import io.spring.model.goods.entity.Itasrn;
import io.spring.model.goods.entity.Itbrnd;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.TbGoods;
import io.spring.model.goods.entity.TbGoodsAddInfo;
import io.spring.model.goods.entity.TbGoodsImage;
import io.spring.model.goods.entity.TbGoodsOption;
import io.spring.model.goods.entity.TbGoodsOptionSupplier;
import io.spring.model.goods.entity.TbGoodsOptionValue;
import io.spring.model.goods.entity.Tmmapi;
import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.goods.request.GoodsPostRequestData;
import io.spring.model.goods.response.GetStockListResponseData;
import io.spring.model.goods.response.GoodsInsertResponseData;
import io.spring.model.goods.response.GoodsListResponseData;
import io.spring.model.goods.response.GoodsResponseData;
import io.spring.model.goods.response.GoodsSelectDetailResponseData;
import io.spring.model.vendor.entity.Cmvdmr;
import io.spring.service.file.FileService;
import io.spring.service.stock.JpaStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaGoodsNewService {
	private final JpaTbGoodsRepository jpaTbGoodsRepository;
	private final JpaItasrnRepository jpaItasrnRepository;
	private final JpaTbGoodsOptionValueRepository jpatbGoodsOptionValueRepository;
//    private MyBatisCommonDao myBatisCommonDao;
	private final MyBatisGoodsDao myBatisGoodsDao;
	private final JpaItasrdRepository jpaItasrdRepository;
	private final JpaTbGoodsOptionRepository jpaTbGoodsOptionRepository;
	private final JpaTbGoodsOptionValueRepository jpaTbGoodsOptionValueRepository;
	private final JpaItitmdRepository jpaItitmdRepository;
	private final JpaItaimgRepository jpaItaimgRepository;
	private final JpaSequenceDataRepository jpaSequenceDataRepository;
	private final JpaIfBrandRepository jpaIfBrandRepository;
	private final JpaIfCategoryRepository jpaIfCategoryRepository;

	private final JpaTbGoodsOptionSupplierRepository jpaTbGoodsOptionSupplierRepository;

	private final JpaTmmapiRepository jpaTmmapiRepository;
	private final JpaTmitemRepository jpaTmitemRepository;

	private final JpaTbGoodsAddInfoRepository jpaTbGoodsAddInfoRepository;

	private final JpaTbGoodsImageRepository jpaTbGoodsImageRepository;

	private final FileService fileService;

	private final JpaStockService jpaStockService;

	private final EntityManager em;

	private final GoodsResponseDataMapper goodsResponseDataMapper;

	public Optional<TbGoods> findById(String goodsId) {
		Optional<TbGoods> goods = jpaTbGoodsRepository.findById(goodsId);
		return goods;
	}

	/**
	 * goods 정보 insert 시퀀스 함수 Pecan 21-04-26
	 * 
	 * @param goodsInsertRequestData
	 * @return GoodsResponseData
	 */

	// 20220307 rjb80 requestbody 추가
	@Transactional
	public String sequenceInsertOrUpdateGoods(GoodsPostRequestData goodsPostRequestData) {

		String userId = goodsPostRequestData.getUserId();


		// TbGoods에 goods 정보 저장
		TbGoods TbGoods = this.saveTbGoods(goodsPostRequestData);

		this.saveAddInfo2(goodsPostRequestData, userId);
		this.saveOptionValue2(goodsPostRequestData, userId);
		this.saveOption2(goodsPostRequestData, userId);
		this.saveOptionSupplier2(goodsPostRequestData, userId);

		this.saveImage2(goodsPostRequestData, TbGoods.getAssortId(), userId);
//        List<GoodsInsertResponseData.Attributes> attributesList = this.makeGoodsResponseAttributes(tbGoodsOptionValueList);
//        List<GoodsInsertResponseData.Items> itemsList = this.makeGoodsResponseItems(TbGoodsOptionList, tbGoodsOptionValueList);
//        return this.makeGoodsInsertResponseData(goodsInsertRequestData, attributesList, itemsList);
		return TbGoods.getAssortId();
	}




	/**
	 * Pecan itaimg에 생성한 assortId 심어주는 함수
	 * 
	 * @param goodsInsertRequestData
	 * @param assortId
	 */

	private void saveImage2(GoodsPostRequestData goodsPostRequestData, String assortId, String userId) {
		TbGoods t = jpaTbGoodsRepository.findById(assortId).orElse(null);
		
		List<GoodsPostRequestData.UploadMainImage> uploadMainImageList = goodsPostRequestData.getUploadMainImage();
		List<GoodsPostRequestData.UploadAddImage> uploadAddImageList = goodsPostRequestData.getUploadAddImage();

		List<TbGoodsImage> oriMainImageList = jpaTbGoodsImageRepository.findByAssortIdAndImageGb(assortId, "01");
			List<TbGoodsImage> oriAddImageList = jpaTbGoodsImageRepository.findByAssortIdAndImageGb(assortId, "02");
	
		List<HashMap<String, Object>> mainList = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> addList = new ArrayList<HashMap<String, Object>>();

		// main image
		for (GoodsPostRequestData.UploadMainImage o : uploadMainImageList) {



			if (o.getSno() == null) {
				TbGoodsImage tgi = new TbGoodsImage(assortId, o);

				jpaTbGoodsImageRepository.save(tgi);
				
			} else {

				TbGoodsImage t1 = jpaTbGoodsImageRepository.findById(o.getSno()).orElse(null);
				
				if (t1 != null) {
					t1.setAssortId(assortId);
					t1.setImageGb(o.getImageGb() == null ? "01" : o.getImageGb());
					t1.setImageSeq(o.getUid());
					t1.setImageUrl(o.getUrl());
				}

				jpaTbGoodsImageRepository.save(t1);

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("sno", o.getSno());
				mainList.add(m);
			}
		}

		
		List<HashMap<String, Object>> deleteMainList = new ArrayList<HashMap<String, Object>>();
		
		for (TbGoodsImage o : oriMainImageList) {
			boolean chk=false;
			for (HashMap<String, Object> v : mainList) {
				if (v.get("sno") == o.getSno()) {
					chk = true;
					break;
				}

			}

			
			if(chk==false) {
				deleteMainList.add(
						new HashMap<String, Object>() {
							{// 초기값 지정
								put("sno", o.getSno());
							}
						});
			}
		}

		for (HashMap<String, Object> v : deleteMainList) {
			jpaTbGoodsImageRepository.deleteById((Long) v.get("sno"));
		}

		// add image
		for (GoodsPostRequestData.UploadAddImage o : uploadAddImageList) {

			if (o.getSno() == null) {
				TbGoodsImage tgi = new TbGoodsImage(assortId, o);

				jpaTbGoodsImageRepository.save(tgi);

			} else {

				TbGoodsImage t1 = jpaTbGoodsImageRepository.findById(o.getSno()).orElse(null);

				if (t1 != null) {
					t1.setAssortId(assortId);
					t1.setImageGb(o.getImageGb() == null ? "02" : o.getImageGb());
					t1.setImageSeq(o.getUid());
					t1.setImageUrl(o.getUrl());
				}

				jpaTbGoodsImageRepository.save(t1);

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("sno", o.getSno());
				addList.add(m);
			}
		}

		List<HashMap<String, Object>> deleteAddList = new ArrayList<HashMap<String, Object>>();

		for (TbGoodsImage o : oriAddImageList) {
			boolean chk = false;
			for (HashMap<String, Object> v : addList) {
				if (v.get("sno") == o.getSno()) {
					chk = true;
					break;
				}

			}

			if (chk == false) {
				deleteAddList.add(new HashMap<String, Object>() {
					{// 초기값 지정
						put("sno", o.getSno());
					}
				});
			}
		}

		for (HashMap<String, Object> v : deleteAddList) {
			jpaTbGoodsImageRepository.deleteById((Long) v.get("sno"));
		}

	}

	private void updateItaimgAssortId(GoodsInsertRequestData goodsInsertRequestData, String assortId, String userId) {

		TbGoods t = jpaTbGoodsRepository.findById(assortId).orElse(null);

		List<GoodsInsertRequestData.UploadMainImage> uploadMainImageList = goodsInsertRequestData.getUploadMainImage();
		List<GoodsInsertRequestData.UploadAddImage> uploadAddImageList = goodsInsertRequestData.getUploadAddImage();
		for (GoodsInsertRequestData.UploadMainImage uploadMainImage : uploadMainImageList) {

			Itaimg itaimg = jpaItaimgRepository.findById(uploadMainImage.getUid()).orElseGet(() -> null);
			itaimg.setAssortId(assortId);


			itaimg.setUpdId(userId);

			jpaItaimgRepository.save(itaimg);


			if (t != null) {
				t.setMainImageUrl(itaimg.getImageUrl());
			}

		}
		for (GoodsInsertRequestData.UploadAddImage uploadAddImage : uploadAddImageList) {
			Itaimg itaimg = jpaItaimgRepository.findById(uploadAddImage.getUid()).orElseGet(() -> null);
			itaimg.setAssortId(assortId);

			itaimg.setUpdId(userId);

			jpaItaimgRepository.save(itaimg);
		}

		if (t != null) {
			jpaTbGoodsRepository.save(t);

		}
	}

	private List<GoodsInsertResponseData.Attributes> makeGoodsResponseAttributes(
			List<TbGoodsOptionValue> tbGoodsOptionValueList) {
		List<GoodsInsertRequestData.Attributes> attributesList = new ArrayList<>();
		for (TbGoodsOptionValue i : tbGoodsOptionValueList) {
			GoodsInsertRequestData.Attributes a = new GoodsInsertRequestData.Attributes(i);
			attributesList.add(a);
		}
		return null;
	}

	private List<GoodsInsertResponseData.Items> makeGoodsResponseItems(List<TbGoodsOption> TbGoodsOptionList,
			List<TbGoodsOptionValue> tbGoodsOptionValueList) {
		List<GoodsInsertResponseData.Items> itemsList = new ArrayList<>();
		for (TbGoodsOption TbGoodsOption : TbGoodsOptionList) {
			GoodsInsertResponseData.Items items = new GoodsInsertResponseData.Items(TbGoodsOption);
			items.setVariationValue1(
					tbGoodsOptionValueList.stream().filter(x -> TbGoodsOption.getVariationSeq1().equals(x.getSeq()))
							.collect(Collectors.toList()).get(0).getOptionNm());
			items.setVariationValue2(TbGoodsOption.getVariationSeq2() == null ? ""
					: tbGoodsOptionValueList.stream().filter(x -> TbGoodsOption.getVariationSeq2().equals(x.getSeq()))
							.collect(Collectors.toList()).get(0).getOptionNm());
			items.setVariationValue3(TbGoodsOption.getVariationSeq3() == null ? ""
					: tbGoodsOptionValueList.stream().filter(x -> TbGoodsOption.getVariationSeq3().equals(x.getSeq()))
							.collect(Collectors.toList()).get(0).getOptionNm());
			itemsList.add(items);
		}
		return itemsList;
	}

	private GoodsInsertResponseData makeGoodsInsertResponseData(GoodsInsertRequestData goodsInsertRequestData,
			List<GoodsInsertResponseData.Attributes> attributesList, List<GoodsInsertResponseData.Items> itemsList) {
		GoodsInsertResponseData goodsInsertResponseData = GoodsInsertResponseData.builder()
				.goodsInsertRequestData(goodsInsertRequestData).attributesList(attributesList).itemsList(itemsList)
				.build();
		return goodsInsertResponseData;
	}

	public void deleteById(String goodsId) {
		jpaTbGoodsRepository.deleteById(goodsId);
	}

	/**
	 * 21-04-27 Pecan 물품 정보 저장 insert, update
	 * 
	 * @param goodsInsertRequestData
	 * @return TbGoods Object
	 */
	private TbGoods saveTbGoods(GoodsPostRequestData goodsPostRequestData) {
		TbGoods tbGoods = jpaTbGoodsRepository.findById(goodsPostRequestData.getAssortId())
				.orElseGet(() -> new TbGoods(goodsPostRequestData));
//        TbGoods.setUpdDt(new Date());

		tbGoods.setAssortNm(goodsPostRequestData.getAssortNm());

		tbGoods.setAssortDnm(goodsPostRequestData.getAssortDnm());
		tbGoods.setAssortEnm(goodsPostRequestData.getAssortEnm());

		tbGoods.setAssortColor(
				goodsPostRequestData.getAssortColor() == null || goodsPostRequestData.getAssortColor().trim().equals("")
						? null
						: goodsPostRequestData.getAssortColor());

		tbGoods.setDispCategoryId(goodsPostRequestData.getDispCategoryId() == null
				|| goodsPostRequestData.getDispCategoryId().trim().equals("") ? null
						: goodsPostRequestData.getDispCategoryId());
		tbGoods.setCategoryId(this.getGodoCateCd(goodsPostRequestData.getDispCategoryId()));

		tbGoods.setBrandId(
				goodsPostRequestData.getBrandId() == null || goodsPostRequestData.getBrandId().trim().equals("")
						? null
						: goodsPostRequestData.getBrandId());

		tbGoods.setOrigin(goodsPostRequestData.getOrigin());

		tbGoods.setManufactureNm(goodsPostRequestData.getManufactureNm() == null
				|| goodsPostRequestData.getManufactureNm().trim().equals("") ? null
						: goodsPostRequestData.getManufactureNm());

		tbGoods.setAssortModel(
				goodsPostRequestData.getAssortModel() == null || goodsPostRequestData.getAssortModel().trim().equals("")
						? null
						: goodsPostRequestData.getAssortModel());
		tbGoods.setVendorId(
				goodsPostRequestData.getVendorId() == null || goodsPostRequestData.getVendorId().trim().equals("")
						? null
						: goodsPostRequestData.getVendorId());

		tbGoods.setOptionGbName(goodsPostRequestData.getOptionGbName());
		tbGoods.setTaxGb(goodsPostRequestData.getTaxGb());
		tbGoods.setAssortState(goodsPostRequestData.getAssortState());
		tbGoods.setShortageYn(goodsPostRequestData.getShortageYn());

		tbGoods.setLocalPrice(
				goodsPostRequestData.getLocalPrice() == null || goodsPostRequestData.getLocalPrice().trim().equals("")
						? null
						: Float.parseFloat(goodsPostRequestData.getLocalPrice()));
		tbGoods.setLocalSale(
				goodsPostRequestData.getLocalSale() == null || goodsPostRequestData.getLocalSale().trim().equals("")
						? null
						: Float.parseFloat(goodsPostRequestData.getLocalSale()));
		tbGoods.setDeliPrice(
				goodsPostRequestData.getDeliPrice() == null || goodsPostRequestData.getDeliPrice().trim().equals("")
						? null
						: Float.parseFloat(goodsPostRequestData.getDeliPrice()));

		tbGoods.setMargin(
				goodsPostRequestData.getMargin() == null || goodsPostRequestData.getMargin().trim().equals("")
						? null
						: Float.parseFloat(goodsPostRequestData.getMargin()));

		tbGoods.setMdRrp(
				goodsPostRequestData.getMdRrp() == null || goodsPostRequestData.getMdRrp().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getMdRrp()));
		tbGoods.setMdYear(goodsPostRequestData.getMdYear());
		tbGoods.setMdVatrate(
				goodsPostRequestData.getMdVatrate() == null || goodsPostRequestData.getMdVatrate().trim().equals("")
						? null
						: Float.parseFloat(goodsPostRequestData.getMdVatrate()));
		tbGoods.setMdDiscountRate(goodsPostRequestData.getMdDiscountRate() == null
				|| goodsPostRequestData.getMdDiscountRate().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getMdDiscountRate()));
		tbGoods.setMdGoodsVatrate(goodsPostRequestData.getMdGoodsVatrate() == null
				|| goodsPostRequestData.getMdGoodsVatrate().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getMdGoodsVatrate()));
		tbGoods.setBuyWhere(goodsPostRequestData.getBuyWhere());
		tbGoods.setBuySupplyDiscount(goodsPostRequestData.getBuySupplyDiscount() == null
				|| goodsPostRequestData.getBuySupplyDiscount().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getBuySupplyDiscount()));
		tbGoods.setBuyExchangeRate(goodsPostRequestData.getBuyExchangeRate() == null
				|| goodsPostRequestData.getBuyExchangeRate().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getBuyExchangeRate()));
		tbGoods.setBuyRrpIncrement(goodsPostRequestData.getBuyRrpIncrement() == null
				|| goodsPostRequestData.getBuyRrpIncrement().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getBuyRrpIncrement()));

		tbGoods.setSellStaDt(goodsPostRequestData.getSellStaDt());
		tbGoods.setSellEndDt(goodsPostRequestData.getSellEndDt());

		tbGoods.setAsWidth(
				goodsPostRequestData.getAsWidth() == null || goodsPostRequestData.getAsWidth().trim().equals("")
						? null
						: Float.parseFloat(goodsPostRequestData.getAsWidth()));
		tbGoods.setAsLength(
				goodsPostRequestData.getAsLength() == null || goodsPostRequestData.getAsLength().trim().equals("")
						? null
						: Float.parseFloat(goodsPostRequestData.getAsLength()));
		tbGoods.setAsHeight(
				goodsPostRequestData.getAsHeight() == null || goodsPostRequestData.getAsHeight().trim().equals("")
						? null
						: Float.parseFloat(goodsPostRequestData.getAsHeight()));
		tbGoods.setWeight(
				goodsPostRequestData.getWeight() == null || goodsPostRequestData.getWeight().trim().equals("")
						? null
						: Float.parseFloat(goodsPostRequestData.getWeight()));

		tbGoods.setAssortGb(goodsPostRequestData.getAssortGb());

		tbGoods.setMdTax(goodsPostRequestData.getMdTax());

		tbGoods.setMdMargin(
				goodsPostRequestData.getMdMargin() == null || goodsPostRequestData.getMdMargin().trim().equals("")
						? null
						: Float.parseFloat(goodsPostRequestData.getMdMargin()));

		tbGoods.setMdGoodsVatrate(goodsPostRequestData.getMdGoodsVatrate() == null
				|| goodsPostRequestData.getMdGoodsVatrate().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getMdGoodsVatrate()));

		tbGoods.setBuyTax(goodsPostRequestData.getBuyTax());
		// 옵션과 옵션에 따른 아이템들의 존재 여부. 미존재시 단품 옵션 1개, 단품 옵션 내역을 가진 아이템 1개가 생성돼야 함.
		tbGoods.setOptionUseYn(goodsPostRequestData.getOptionUseYn());


		tbGoods.setGoodsDescription(goodsPostRequestData.getGoodsDescription());
		tbGoods.setShortDescription(goodsPostRequestData.getShortDescription());

		tbGoods = jpaTbGoodsRepository.save(tbGoods);

//        jpaTbGoodsRepository.save(TbGoods);
		// em.persist(TbGoods);
		return tbGoods;
	}

	// 우리 카테고리로 고도몰 카테고리코드 가져오기
	private String getGodoCateCd(String cateId) {

		String cateCd = null;
		IfCategory ifCategory = jpaIfCategoryRepository.findByChannelGbAndCategoryId(StringFactory.getGbOne(), cateId);
		if (ifCategory == null) {
			log.debug("category code is not exist.");
			return cateCd;
		}
		cateCd = ifCategory.getChannelCategoryId();


		return cateCd;
	}

	/**
	 * 21-04-28 Pecan 물품 정보 이력 insert, update
	 * 
	 * @param goodsInsertRequestData
	 * @return Itasrn Object
	 */
	private Itasrn saveItasrn(GoodsInsertRequestData goodsInsertRequestData, String userId) {
//        ItasrnId itasrnId = new ItasrnId(goodsRequestData);
		LocalDateTime effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT()); // 마지막 날짜(없을 경우 9999-12-31
																							// 23:59:59?)
		Itasrn itasrn = jpaItasrnRepository.findByAssortIdAndEffEndDt(goodsInsertRequestData.getAssortId(), effEndDt);
		if (itasrn == null) { // insert
			itasrn = new Itasrn(goodsInsertRequestData);
		} else { // update
			itasrn.setEffEndDt(LocalDateTime.now().minusSeconds(1));
			// update 후 새 이력 insert
			Itasrn newItasrn = new Itasrn(itasrn);

			newItasrn.setRegId(userId);
			newItasrn.setUpdId(userId);

			jpaItasrnRepository.save(newItasrn);
		}
		itasrn.setLocalSale(
				goodsInsertRequestData.getLocalSale() == null || goodsInsertRequestData.getLocalSale().trim().equals("")
						? null
						: Float.parseFloat(goodsInsertRequestData.getLocalSale()));
		itasrn.setShortageYn(goodsInsertRequestData.getShortageYn());
//        jpaItasrnRepository.save(itasrn);

		itasrn.setUpdId(userId);

		jpaItasrnRepository.save(itasrn);
		return itasrn;
	}

	/**
	 * 21-04-28 Pecan 메모(긴 글, 짧은 글) insert, update
	 * 
	 * @param goodsInsertRequestData
	 * @return List<Itasrd>
	 */
	private List<Itasrd> saveItasrd(GoodsInsertRequestData goodsInsertRequestData, String userId) {
		List<GoodsInsertRequestData.Description> descriptionList = goodsInsertRequestData.getDescription();
		List<Itasrd> itasrdList = new ArrayList<>();
		List<Itasrd> itasrdList1 = jpaItasrdRepository.findByAssortId(goodsInsertRequestData.getAssortId());// new
																											// Itasrd(goodsInsertRequestData);
		for (int i = 0; i < descriptionList.size(); i++) {
			GoodsInsertRequestData.Description description = descriptionList.get(i);
			List<Itasrd> itasrdList2 = itasrdList1.stream()
					.filter(x -> x.getOrdDetCd().equals(description.getOrdDetCd())).collect(Collectors.toList());
			Itasrd itasrd = itasrdList2.size() > 0 ? itasrdList2.get(0) : null;
			String seq = descriptionList.get(i).getSeq();
//            if(seq == null || seq.trim().equals("")){ // insert
			if (itasrd == null) { // insert
				itasrd = new Itasrd(goodsInsertRequestData, description);
//                if (seq == null || seq.trim().equals("")) { // insert -> 빈 테이블
//                    seq = StringFactory.getFourStartCd();//fourStartCd;
//                }
//                else{ // insert -> 찬 테이블
//                    seq = Utilities.plusOne(seq, 4);
//                }
				if (description.getOrdDetCd().equals(StringFactory.getGbOne())) {
					seq = StringFactory.getFourStartCd(); // 0001
				} else if (description.getOrdDetCd().equals(StringFactory.getGbTwo())) {
					seq = StringFactory.getFourSecondCd(); // 0002
				}
				itasrd.setSeq(seq);
				itasrd.setRegId(userId);

			} else { // update
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
	 * 21-04-28 Pecan 옵션 정보 insert, update
	 * 
	 * @param goodsInsertRequestData
	 * @return List<tbGoodsOptionValue>
	 */

	// 2022-03-25 사용안함
//	private List<TbGoodsOptionValue> saveTgovList(GoodsInsertRequestData goodsInsertRequestData,
//			List<TbGoodsOptionValue> existTgovList,
//			String userId) {
//
//		List<TbGoodsOptionValue> tgovList;
//		if (existTgovList == null || existTgovList.size() == 0) {
//			tgovList = this.insertOptionValue(goodsInsertRequestData, userId);
//		} else {
//			// tgovList = this.updatetbGoodsOptionValueList(goodsInsertRequestData,
//			// existTgovList,
//			// userId);
//			tgovList = this.updateTgovList(goodsInsertRequestData, existTgovList, userId);
//		}
//		return tgovList;
//	}

	private void saveOptionValue2(GoodsPostRequestData goodsPostRequestData, String userId) {

		// 현재 옵션과 기존옵션을 비교하여
		// 기존옵션의 건을 살리거나 죽이거나해야함.

		// 없냐
		// 있는데 죽어있냐
		// 있는데 살아있냐
		List<GoodsPostRequestData.Attributes> AttrList = goodsPostRequestData.getAttributes();
		
		String maxSeq = jpaTbGoodsOptionValueRepository.findMaxSeqByAssortId(goodsPostRequestData.getAssortId());
		
		List<TbGoodsOptionValue> oriTgovList = jpaTbGoodsOptionValueRepository
				.findByAssortIdAndDelYn(goodsPostRequestData.getAssortId(), "02");
		


		List<HashMap<String, Object>> existedList = new ArrayList<HashMap<String, Object>>();
		


		for (GoodsPostRequestData.Attributes o : AttrList) {


			
			if (o.getSeq() == null || o.getSeq().trim().equals("")) {
				// 현재 옵션속성이 없거나 사용안하는 상태임.
				// 속성이 없는경우

				TbGoodsOptionValue tgov = jpaTbGoodsOptionValueRepository.findByAssortIdAndOptionNmAndVariationGb(
						goodsPostRequestData.getAssortId(), o.getValue(), o.getVariationGb());


				if(tgov==null) {

					String seq = "";
					//그냥 하나만들면됨.
					if (maxSeq == null) {
						seq = "0001";
						maxSeq = seq;
					} else {
						seq = Utilities.plusOne(maxSeq, 4);
						maxSeq = seq;
					}

					TbGoodsOptionValue tgov1 = new TbGoodsOptionValue(goodsPostRequestData);
					tgov1.setSeq(seq);
					tgov1.setOptionNm(o.getValue());
					tgov1.setOptionGb(o.getVariationGb());
					tgov1.setVariationGb(o.getVariationGb());
					tgov1.setRegId(userId);

					tgov1.setUpdId(userId);

					jpaTbGoodsOptionValueRepository.save(tgov1);

				}else {
					// 속성을 사용안하는 경우
					tgov.setDelYn("02");
					tgov.setUpdId(userId);
					jpaTbGoodsOptionValueRepository.save(tgov);
					//이건 나온거를 살리면됨.
				}
				
			

			} else {
				TbGoodsOptionValue tgov = jpaTbGoodsOptionValueRepository
						.findByAssortIdAndSeq(goodsPostRequestData.getAssortId(), o.getSeq());

				tgov.setDelYn("02");
				tgov.setUpdId(userId);
				jpaTbGoodsOptionValueRepository.save(tgov);

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("seq", o.getSeq());
				existedList.add(m);
			}

		}

		List<HashMap<String, Object>> deleteList = new ArrayList<HashMap<String, Object>>();



		for (TbGoodsOptionValue o : oriTgovList) {



			boolean chk = false;
			for (HashMap<String, Object> v : existedList) {
				if (v.get("seq").equals(o.getSeq())) {
					chk = true;
					break;
				}

			}

			if (chk == false) {
				deleteList.add(new HashMap<String, Object>() {
					{// 초기값 지정
						put("seq", o.getSeq());
					}
				});
			}
		}

		List<TbGoodsOptionValue> l2 = jpaTbGoodsOptionValueRepository
				.findByAssortId(goodsPostRequestData.getAssortId());

		// 삭제
		for (HashMap<String, Object> v : deleteList) {
			TbGoodsOptionValue tgov = jpaTbGoodsOptionValueRepository
					.findByAssortIdAndSeq(goodsPostRequestData.getAssortId(), v.get("seq").toString());

			tgov.setDelYn("01");
			tgov.setUpdId(userId);
			jpaTbGoodsOptionValueRepository.save(tgov);

			// jpaTbGoodsImageRepository.deleteById((Long) v.get("sno"));
		}




	}

	// 20220325 사용안함
//	private List<TbGoodsOptionValue> insertOptionValue(GoodsInsertRequestData goodsInsertRequestData, String userId) {
//		List<TbGoodsOptionValue> tgovList = saveSingleOptionValue(goodsInsertRequestData, userId);
//		if (goodsInsertRequestData.getOptionUseYn().equals(StringFactory.getGbTwo())) { // optionUseYn이 02, 즉 단품인 경우
//			return tgovList; // 단품 옵션 1개를 저장하는 함수
//		}
//		List<GoodsInsertRequestData.Attributes> attributes = goodsInsertRequestData.getAttributes();
//		if (attributes.size() > 0) {
//			tgovList.get(0).setDelYn(StringFactory.getGbOne());
//
//			tgovList.get(0).setUpdId(userId);
//
//			jpaTbGoodsOptionValueRepository.save(tgovList.get(0));
//		} else {
//			return tgovList;
//		}
//		Set<String> seqList = new HashSet<>();
//		seqList.add(tgovList.get(0).getSeq());
//		for (GoodsInsertRequestData.Attributes attribute : attributes) {
//			TbGoodsOptionValue tgov = new TbGoodsOptionValue(goodsInsertRequestData);
//			String seq = Utilities.plusOne(this.findMaxSeq(seqList), 4);// jpatbGoodsOptionValueRepository.findMaxSeqByAssortId(assortId);
//			tgov.setSeq(seq);
//			tgov.setOptionNm(attribute.getValue());
//			tgov.setOptionGb(attribute.getVariationGb());
//			tgov.setVariationGb(attribute.getVariationGb());
//			tgovList.add(tgov);
//			seqList.add(seq);
//
//			tgov.setRegId(userId);
//
//			tgov.setUpdId(userId);
//
//			jpaTbGoodsOptionValueRepository.save(tgov);
//		}
//		return tgovList;
//	}



	/**
	 * seq가 든 리스트에서 seq의 최댓값을 반환함
	 */
	private String findMaxSeq(Set<String> seqList) {
		int max = -1;
		String maxSeq = "";
		for (String seq : seqList) {
			if (max <= Integer.parseInt(seq)) {
				max = Integer.parseInt(seq);
				maxSeq = seq;
			}
		}
		return maxSeq;
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

	private void saveSingleOption2(GoodsPostRequestData goodsPostRequestData, String userId) {

		List<TbGoodsOption> l = jpaTbGoodsOptionRepository.findByAssortIdAndDelYn(goodsPostRequestData.getAssortId(),
				"02");

		for (TbGoodsOption o : l) {

			o.setDelYn("01");
			o.setUpdId(userId);
			jpaTbGoodsOptionRepository.save(o);

		}

		TbGoodsOptionValue v = jpaTbGoodsOptionValueRepository
				.findByAssortIdAndOptionNmAndVariationGb(goodsPostRequestData.getAssortId(), "단품", "01");

		TbGoodsOption tgo = jpaTbGoodsOptionRepository
				.findByAssortIdAndVariationSeq1AndDelYn(goodsPostRequestData.getAssortId(), v.getSeq(), "01");

		String maxItemId = jpaTbGoodsOptionRepository.findMaxItemIdByAssortId(goodsPostRequestData.getAssortId());

		if (tgo == null) {
			TbGoodsOption tgo1 = new TbGoodsOption(goodsPostRequestData);
			String itemId = "";
			if (maxItemId == null) {
				itemId = "0001";
			} else {
				itemId = Utilities.plusOne(maxItemId, 4);
			}

			tgo1.setItemId(itemId);

			tgo1.setRegId(userId);
			tgo1.setDelYn("02");
			tgo1.setItemNm(goodsPostRequestData.getAssortNm());

			tgo1.setVariationGb1("01");
			tgo1.setVariationSeq1(v.getSeq());

			tgo1.setShortYn("01");
			tgo1.setUpdId(userId);
			jpaTbGoodsOptionRepository.save(tgo1);

		} else {
			tgo.setDelYn("02");
			tgo.setUpdId(userId);
			jpaTbGoodsOptionRepository.save(tgo);
		}

	}

	private void saveSingleOptionValue2(GoodsPostRequestData goodsPostRequestData, String userId) {

		List<TbGoodsOptionValue> l = jpaTbGoodsOptionValueRepository
				.findByAssortIdAndDelYn(goodsPostRequestData.getAssortId(), "02");

		String maxSeq = jpaTbGoodsOptionValueRepository.findMaxSeqByAssortId(goodsPostRequestData.getAssortId());

		for (TbGoodsOptionValue o : l) {

			o.setDelYn("01");
			o.setUpdId(userId);
			jpaTbGoodsOptionValueRepository.save(o);

		}

		TbGoodsOptionValue v = jpaTbGoodsOptionValueRepository
				.findByAssortIdAndOptionNmAndVariationGb(goodsPostRequestData.getAssortId(),
				"단품", "01");

		if (v == null) {
			String seq = "";
			TbGoodsOptionValue v1 = new TbGoodsOptionValue(goodsPostRequestData);

			if (maxSeq == null) {
				seq = "0001";
			} else {
				seq = Utilities.plusOne(maxSeq, 4);

			}

			v1.setSeq(seq);
			v1.setOptionGb("01");
			v1.setOptionNm("단품");
			v1.setVariationGb("01");
			v1.setRegId(userId);
			v1.setUpdId(userId);

			jpaTbGoodsOptionValueRepository.save(v1);
		} else {
			v.setDelYn("02");
			v.setUpdId(userId);
			jpaTbGoodsOptionValueRepository.save(v);

		}

	}

	// 2022-03-25 사용한함.
//	private List<TbGoodsOptionValue> saveSingleOptionValue(GoodsInsertRequestData goodsInsertRequestData,
//			String userId) {
//
//		List<TbGoodsOptionValue> tgovList = new ArrayList<>();
//		TbGoodsOptionValue tgov = jpaTbGoodsOptionValueRepository
//				.findByAssortIdAndSeq(goodsInsertRequestData.getAssortId(), StringFactory.getFourStartCd());
//		if (tgov == null) {
//			tgov = new TbGoodsOptionValue(goodsInsertRequestData);
//			tgov.setSeq(StringFactory.getFourStartCd()); // 0001 하드코딩
//			tgov.setOptionGb(StringFactory.getGbOne()); // 01 하드코딩
//			tgov.setImgYn(StringFactory.getGbTwo()); // 02 하드코딩
//			tgov.setOptionNm(StringFactory.getStrSingleGoods()); // '단품' 하드코딩
//			tgov.setVariationGb(StringFactory.getGbOne()); // 01 하드코딩
////        jpatbGoodsOptionValueRepository.save(tbGoodsOptionValue);
//		}
//		tgov.setDelYn(StringFactory.getGbTwo()); // 02 하드코딩
//
//		tgov.setUpdId(userId);
//
//		jpaTbGoodsOptionValueRepository.save(tgov);
//		tgovList.add(tgov);
//		return tgovList;
//
//	}

	/**
	 * 21-04-28 Pecan 아이템 정보 insert, update
	 * 
	 * @param goodsInsertRequestData
	 * @return List<TbGoodsOption>
	 */

	// 사용안함.2022-03-25
//	private List<TbGoodsOption> saveItemList(GoodsInsertRequestData goodsInsertRequestData,
//			List<TbGoodsOption> existTbGoodsOptionList, List<TbGoodsOptionValue> tbGoodsOptionValueList,
//			String userId) {
//
//		List<TbGoodsOption> TbGoodsOptionList;
//
//		// 옵션이 없는건
//
//		// 옵션이 있는건
//
//		if (existTbGoodsOptionList == null || existTbGoodsOptionList.size() == 0) {
//			TbGoodsOptionList = this.insertTbGoodsOptionList(goodsInsertRequestData, tbGoodsOptionValueList, userId);
//		} else {
//			TbGoodsOptionList = this.updateTbGoodsOptionList(goodsInsertRequestData, existTbGoodsOptionList,
//					tbGoodsOptionValueList,
//					userId);
//		}
//		return TbGoodsOptionList;
//	}

	/**
	 * 상품 insert 시 TbGoodsOption 저장
	 */
//	private List<TbGoodsOption> insertTbGoodsOptionList(GoodsInsertRequestData goodsInsertRequestData,
//			List<TbGoodsOptionValue> tbGoodsOptionValueList, String userId) {
//
//		List<GoodsInsertRequestData.Items> itemList = goodsInsertRequestData.getItems();
//
////		Set<String> seqList = new HashSet<>();
//		seqList.add(TbGoodsOptionList.get(0).getItemId());
//		for (GoodsInsertRequestData.Items items : itemList) {
//			TbGoodsOption TbGoodsOption = new TbGoodsOption(goodsInsertRequestData);
//			String itemId = Utilities.plusOne(this.findMaxSeq(seqList), 4);
//			TbGoodsOption.setItemId(itemId);
//			TbGoodsOption.setRegId(userId);
//
//			TbGoodsOptionValue op1 = tbGoodsOptionValueList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//					&& x.getOptionNm().equals(items.getVariationValue1())).collect(Utilities.toSingleton());
//			if (op1 != null) {
//				TbGoodsOption.setVariationGb1(op1.getOptionGb());
//				TbGoodsOption.setVariationSeq1(op1.getSeq());
//			}
//			// 옵션2 관련값 찾아넣기
//			TbGoodsOptionValue op2 = tbGoodsOptionValueList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//					&& x.getOptionNm().equals(items.getVariationValue2())).collect(Utilities.toSingleton());
//			if (op2 != null) {
//				TbGoodsOption.setVariationGb2(op2.getOptionGb());
//				TbGoodsOption.setVariationSeq2(op2.getSeq());
//			}
//			// 옵션3 관련값 찾아넣기
//			TbGoodsOptionValue op3 = tbGoodsOptionValueList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//					&& x.getOptionNm().equals(items.getVariationValue3())).collect(Utilities.toSingleton());
//			if (op3 != null) {
//				TbGoodsOption.setVariationGb3(op3.getOptionGb());
//				TbGoodsOption.setVariationSeq3(op3.getSeq());
//			}
//			TbGoodsOption.setAddPrice(items.getAddPrice() == null || items.getAddPrice().trim().equals("") ? null
//					: Float.parseFloat(items.getAddPrice()));
//			TbGoodsOption.setShortYn(items.getShortYn());
//			seqList.add(itemId);
//
//			TbGoodsOption.setUpdId(userId);
//
//			jpaTbGoodsOptionRepository.save(TbGoodsOption);
//
//			List<GoodsInsertRequestData.itemSupplier> l = items.getItemSupplier();
//
//			for (GoodsInsertRequestData.itemSupplier o : l) {
//
//
//				o.setItemId(itemId);
//
//				TbGoodsOptionSupplier tgos = new TbGoodsOptionSupplier(goodsInsertRequestData.getAssortId(), o);
//
//				tgos.setUpdId(userId);
//
//				jpaTbGoodsOptionSupplierRepository.save(tgos);
//
//			}
//		}
//		return TbGoodsOptionList;
//	}



	/**
	 * 상품 update 시 itimm 저장
	 */

	// 2022-03-25 사용안함
//	private List<TbGoodsOption> updateTbGoodsOptionList(GoodsInsertRequestData goodsInsertRequestData,
//			List<TbGoodsOption> existTbGoodsOptionList, List<TbGoodsOptionValue> tbGoodsOptionValueList,
//			String userId) {
//
//		List<GoodsInsertRequestData.Items> itemList = goodsInsertRequestData.getItems();
//		List<TbGoodsOption> TbGoodsOptionList = new ArrayList<>();
//		Set<String> itemIdList = new HashSet<>();
//		Set<String> removeItemIdList = new HashSet<>();
//
//		for (TbGoodsOption i : existTbGoodsOptionList) {
//			itemIdList.add(i.getItemId());
//			removeItemIdList.add(i.getItemId());
//		}
//
//
//		for (GoodsInsertRequestData.Items item : itemList) {
//			List<TbGoodsOption> origTbGoodsOptionList = existTbGoodsOptionList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//							&& x.getItemId().equals(item.getItemId()))
//					.collect(Collectors.toList());
//			TbGoodsOption TbGoodsOption = origTbGoodsOptionList.size() > 0 ? origTbGoodsOptionList.get(0) : null;// jpaTbGoodsOptionRepository.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(),
//																													// itemId);
//			String itemId = item.getItemId(); // item id를 객체가 갖고 있으면 그것을 이용
////            TbGoodsOption TbGoodsOption = new TbGoodsOption(goodsInsertRequestData.getAssortId(), item);
////            if(itemId == null || itemId.trim().equals("")){ // 객체에 item id가 없으면 jpa에서 max값을 가져옴
//			if (!itemIdList.contains(itemId) && !itemId.trim().equals("")) {
//				log.debug("기존 TbGoodsOption의 itemIdList에 " + itemId + "가 존재하지 않습니다.");
//				continue;
//			}
//			if (TbGoodsOption == null) { // 객체에 item id가 없으면 jpa에서 max값을 가져옴
//				TbGoodsOption = new TbGoodsOption(goodsInsertRequestData.getAssortId(), item);
//				itemId = this.findMaxSeq(itemIdList);// jpaTbGoodsOptionRepository.findMaxItemIdByAssortId(goodsInsertRequestData.getAssortId());
//				if (itemId == null || itemId.trim().equals("")) { // jpa에서 max값을 가져왔는데 null이면 해당 assort id에 item id가
//																	// 존재하지 않으므로 초기값(0001)을 설정
//					itemId = StringFactory.getFourStartCd();
//				} else { // jpa에서 max값을 가져온 경우 1을 더한 후 item id로 삼음
//					itemId = Utilities.plusOne(itemId, 4);
//				}
//				TbGoodsOption.setItemId(itemId);
//				TbGoodsOption.setRegId(userId);
//
//				itemIdList.add(itemId);
//			} else { // 존재하는 경우 : tbGoodsOptionValue 객체가 존재함이 보장됨 -> update
//				if (TbGoodsOption.getDelYn().equals(StringFactory.getGbOne())
//						|| TbGoodsOption.getItemId().equals(StringFactory.getFourStartCd())) { // 삭제된 상태거나 seq 0001인
//																								// tbGoodsOptionValue는
//																								// 수정x
//					log.debug("delYn이 01이거나 itemId가 0001(단품)인 TbGoodsOption를 update할 수 없습니다.");
//					continue;
//				}
//				removeItemIdList.remove(itemId);
//			}
//
//			// 옵션1 관련값 찾아넣기
//			TbGoodsOptionValue op1 = tbGoodsOptionValueList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//							&& x.getVariationGb().equals(StringFactory.getGbOne())
//							&& x.getOptionNm().equals(item.getVariationValue1()))
//					.collect(Utilities.toSingleton());
//			if (op1 != null) {
//				TbGoodsOption.setVariationGb1(op1.getOptionGb());
//				TbGoodsOption.setVariationSeq1(op1.getSeq());
//			}
//			// 옵션2 관련값 찾아넣기
//			TbGoodsOptionValue op2 = tbGoodsOptionValueList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//							&& x.getVariationGb().equals(StringFactory.getGbTwo())
//							&& x.getOptionNm().equals(item.getVariationValue2()))
//					.collect(Utilities.toSingleton());
//			if (op2 != null) {
//				TbGoodsOption.setVariationGb2(op2.getOptionGb());
//				TbGoodsOption.setVariationSeq2(op2.getSeq());
//			}
//			// 옵션3 관련값 찾아넣기
//			TbGoodsOptionValue op3 = tbGoodsOptionValueList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//							&& x.getVariationGb().equals(StringFactory.getGbThree())
//							&& x.getOptionNm().equals(item.getVariationValue3()))
//					.collect(Utilities.toSingleton());
//			if (op3 != null) {
//				TbGoodsOption.setVariationGb3(op3.getOptionGb());
//				TbGoodsOption.setVariationSeq3(op3.getSeq());
//			}
//			TbGoodsOption.setAddPrice(item.getAddPrice() == null || item.getAddPrice().trim().equals("") ? null
//					: Float.parseFloat(item.getAddPrice()));
//			TbGoodsOption.setShortYn(item.getShortYn());
////            jpaTbGoodsOptionRepository.save(TbGoodsOption);
////            System.out.println("===== : " + TbGoodsOption.toString());
//
//			TbGoodsOption.setUpdId(userId);
//
//			jpaTbGoodsOptionRepository.save(TbGoodsOption);
//			TbGoodsOptionList.add(TbGoodsOption);
//		}
//		for (TbGoodsOption i : existTbGoodsOptionList) {
//			if (removeItemIdList.contains(i.getItemId())) {
//				i.setDelYn(StringFactory.getGbOne());
//			}
//
//			i.setUpdId(userId);
//			jpaTbGoodsOptionRepository.save(i);
//		}
//
//		int TbGoodsOptionDelNo = 0;
//		for (TbGoodsOption i : TbGoodsOptionList) {
//			if (i.getDelYn().equals(StringFactory.getGbTwo())) {
//				TbGoodsOptionDelNo++;
//			}
//		}
//		if (TbGoodsOptionDelNo == 0) {
//			TbGoodsOption singleTbGoodsOption = existTbGoodsOptionList.stream()
//					.filter(x -> x.getItemId().equals(StringFactory.getFourStartCd())).collect(Collectors.toList())
//					.get(0);
//			singleTbGoodsOption.setDelYn(StringFactory.getGbTwo());
//			singleTbGoodsOption.setUpdId(userId);
//			jpaTbGoodsOptionRepository.save(singleTbGoodsOption);
//		}
//
//
//
//		List<TbGoodsOptionSupplier> oriSupplierList = jpaTbGoodsOptionSupplierRepository
//				.findByAssortId(goodsInsertRequestData.getAssortId());
//
//		List<HashMap<String, Object>> existedSupplierList = new ArrayList<HashMap<String, Object>>();
//
//		// supplier add
//		for (GoodsInsertRequestData.Items o : itemList) {
//
//			List<GoodsInsertRequestData.itemSupplier> itemSupplierList = o.getItemSupplier();
//
//			for (GoodsInsertRequestData.itemSupplier v : itemSupplierList) {
//				if (v.getSno() == null) {
//					TbGoodsOptionSupplier tgos = new TbGoodsOptionSupplier(goodsInsertRequestData.getAssortId(), v);
//					tgos.setUpdId(userId);
//					jpaTbGoodsOptionSupplierRepository.save(tgos);
//				} else if (v.getSno() != null) {
//
//					TbGoodsOptionSupplier tg = jpaTbGoodsOptionSupplierRepository.findById(v.getSno()).orElse(null);
//
//					if (tg != null) {
//						tg.setAssortId(goodsInsertRequestData.getAssortId());
//						tg.setItemId(o.getItemId());
//						tg.setSalePrice(v.getSalePrice());
//						tg.setStockCnt(v.getStockCnt());
//						tg.setSaleYn(v.getSaleYn());
//						tg.setSupplierId(v.getSupplierId());
//						tg.setUpdId(userId);
//						jpaTbGoodsOptionSupplierRepository.save(tg);
//
//						HashMap<String, Object> s = new HashMap<String, Object>();
//
//						s.put("sno", v.getSno());
//
//						existedSupplierList.add(s);
//					}
//
//
//				}
//				
//			}
//
//		}
//
//		// 비교
//		List<HashMap<String, Object>> deleteSupplierList = new ArrayList<HashMap<String, Object>>();
//
//		for (TbGoodsOptionSupplier o : oriSupplierList) {
//			boolean chk = false;
//			for (HashMap<String, Object> v : existedSupplierList) {
//				if (v.get("sno") == o.getSno()) {
//					chk = true;
//					break;
//				}
//
//			}
//
//			if (chk == false) {
//				deleteSupplierList.add(new HashMap<String, Object>() {
//					{// 초기값 지정
//						put("sno", o.getSno());
//					}
//				});
//			}
//		}
//
//		// 삭제
//		for (HashMap<String, Object> v : deleteSupplierList) {
//			jpaTbGoodsOptionSupplierRepository.deleteById((Long) v.get("sno"));
//		}
//
//		return TbGoodsOptionList;
//	}


	private void saveOptionSupplier2(GoodsPostRequestData goodsPostRequestData, String userId) {
		List<TbGoodsOptionSupplier> oriSupplierList = jpaTbGoodsOptionSupplierRepository
				.findByAssortId(goodsPostRequestData.getAssortId());

		List<HashMap<String, Object>> existedSupplierList = new ArrayList<HashMap<String, Object>>();

		List<GoodsPostRequestData.Items> itemList = goodsPostRequestData.getItems();

		// supplier add
		for (GoodsPostRequestData.Items o : itemList) {

			List<GoodsPostRequestData.itemSupplier> itemSupplierList = o.getItemSupplier();

			Map<String, Long> m = new HashMap<String, Long>();

			m = itemSupplierList.stream().collect(
					Collectors.groupingBy(GoodsPostRequestData.itemSupplier::getSupplierId, Collectors.counting())
			);
			
			m.values().removeIf(l -> l < 2);

			if (!m.isEmpty()) {
				throw new IllegalArgumentException("option supplier duplicate check..");
			}

//			m = itemSupplierList.stream().collect(
			// Collectors.groupingBy(GoodsInsertRequestData.itemSupplier::getSupplierId,
			// Collectors.counting()));


			for (GoodsPostRequestData.itemSupplier v : itemSupplierList) {
				

				
				if (v.getSno() == null) {
					TbGoodsOptionSupplier tgos = new TbGoodsOptionSupplier(goodsPostRequestData.getAssortId(), v);

					tgos.setItemId(o.getItemId());
					tgos.setUpdId(userId);
					jpaTbGoodsOptionSupplierRepository.save(tgos);
				} else if (v.getSno() != null) {

					TbGoodsOptionSupplier tg = jpaTbGoodsOptionSupplierRepository.findById(v.getSno()).orElse(null);

					if (tg != null) {
						tg.setAssortId(goodsPostRequestData.getAssortId());
						tg.setItemId(o.getItemId());
						tg.setSalePrice(v.getSalePrice());
						tg.setStockCnt(v.getStockCnt());
						tg.setSaleYn(v.getSaleYn());
						tg.setSupplierId(v.getSupplierId());
						tg.setUpdId(userId);
						jpaTbGoodsOptionSupplierRepository.save(tg);

						HashMap<String, Object> s = new HashMap<String, Object>();

						s.put("sno", v.getSno());

						existedSupplierList.add(s);
					}


				}
				
			}

		}

		// 비교
		List<HashMap<String, Object>> deleteSupplierList = new ArrayList<HashMap<String, Object>>();

		for (TbGoodsOptionSupplier o : oriSupplierList) {
			boolean chk = false;
			for (HashMap<String, Object> v : existedSupplierList) {
				if (v.get("sno") == o.getSno()) {
					chk = true;
					break;
				}

			}

			if (chk == false) {
				deleteSupplierList.add(new HashMap<String, Object>() {
					{// 초기값 지정
						put("sno", o.getSno());
					}
				});
			}
		}

		// 삭제
		for (HashMap<String, Object> v : deleteSupplierList) {
			jpaTbGoodsOptionSupplierRepository.deleteById((Long) v.get("sno"));
		}

	}

	private void saveAddInfo2(GoodsPostRequestData goodsPostRequestData, String userId) {

		List<TbGoodsAddInfo> oriInfoList = jpaTbGoodsAddInfoRepository
				.findByAssortId(goodsPostRequestData.getAssortId());

		List<HashMap<String, Object>> existedSupplierList = new ArrayList<HashMap<String, Object>>();

		List<GoodsPostRequestData.AddInfo> itemList = goodsPostRequestData.getAddInfos();


			System.out.println(itemList);


		// supplier add
		for (GoodsPostRequestData.AddInfo o : itemList) {



			if (o.getSno() == null || o.getSno().toString().length() == 0) {

					TbGoodsAddInfo tgai = new TbGoodsAddInfo();

					tgai.setAssortId(goodsPostRequestData.getAssortId());
					tgai.setInfoTitle(o.getInfoTitle());
					tgai.setInfoValue(o.getInfoValue());
					tgai.setRegId(userId);
					tgai.setUpdId(userId);
					jpaTbGoodsAddInfoRepository.save(tgai);
				} else if (o.getSno().toString().length() > 0) {

					TbGoodsAddInfo tgai = jpaTbGoodsAddInfoRepository.findById(o.getSno()).orElse(null);

					if (tgai != null) {
						tgai.setInfoTitle(o.getInfoTitle());
						tgai.setInfoValue(o.getInfoValue());

						tgai.setUpdId(userId);
						jpaTbGoodsAddInfoRepository.save(tgai);

						HashMap<String, Object> s = new HashMap<String, Object>();

						s.put("sno", o.getSno());

						existedSupplierList.add(s);
					}

				}

			}



		// 비교
		List<HashMap<String, Object>> deleteList = new ArrayList<HashMap<String, Object>>();

		for (TbGoodsAddInfo o : oriInfoList) {
			boolean chk = false;
			for (HashMap<String, Object> v : existedSupplierList) {
				if (v.get("sno") == o.getSno()) {
					chk = true;
					break;
				}

			}

			if (chk == false) {
				deleteList.add(new HashMap<String, Object>() {
					{// 초기값 지정
						put("sno", o.getSno());
					}
				});
			}
		}

		// 삭제
		for (HashMap<String, Object> v : deleteList) {
			jpaTbGoodsAddInfoRepository.deleteById((Long) v.get("sno"));

		}

	}

	private void saveOption2(GoodsPostRequestData goodsPostRequestData, String userId) {

	

		List<GoodsPostRequestData.Items> itemList = goodsPostRequestData.getItems();
		List<TbGoodsOption> TbGoodsOptionList = new ArrayList<>();

		List<TbGoodsOption> oriTgoList = jpaTbGoodsOptionRepository
				.findByAssortIdAndDelYn(goodsPostRequestData.getAssortId(), "02");

		String maxItemId = jpaTbGoodsOptionRepository.findMaxItemIdByAssortId(goodsPostRequestData.getAssortId());

		// 없거나
		// 조회시
		// 있는데 죽어있거나

		List<TbGoodsOptionValue> tgovList = jpaTbGoodsOptionValueRepository
				.findByAssortIdAndDelYn(goodsPostRequestData.getAssortId(), "02");

		List<HashMap<String, Object>> existedList = new ArrayList<HashMap<String, Object>>();
		
		for (GoodsPostRequestData.Items o : itemList) {

	
			if (o.getItemId() == null || o.getItemId().trim().length() == 0) {

				
				String vSeq1 = "";
				String vSeq2 = "";
				String vSeq3 = "";

				boolean isError = false;

		
				if (o.getVariationSeq1() != null && o.getVariationSeq1().trim().length() > 0) {
					vSeq1 = o.getVariationSeq1().toString();
					
					List<TbGoodsOptionValue> l = tgovList.stream()
							.filter(i -> i.getVariationGb().equals("01")
									&& i.getSeq().equals(o.getVariationSeq1().toString())
									&& i.getDelYn().equals("02"))
							.collect(Collectors.toList());					
					
					
					if (l.size() == 0) {
						isError = true;
					}
					
				}else {
					
					if (o.getVariationValue1() != null && o.getVariationValue1().trim().length() > 0) {
						List<TbGoodsOptionValue> l = tgovList.stream()
								.filter(i -> i.getVariationGb().equals("01")
										&& i.getOptionNm().equals(o.getVariationValue1().toString())
										&& i.getDelYn().equals("02"))
								.collect(Collectors.toList());

				
						if (l.size() > 0) {
							vSeq1 = l.get(0).getSeq().toString();
						} else {
							isError = true;
						}

					}
					
				}

			
				if (o.getVariationSeq2() != null && o.getVariationSeq2().trim().length() > 0) {
					vSeq2 = o.getVariationSeq2().toString();

					List<TbGoodsOptionValue> l = tgovList.stream()
							.filter(i -> i.getVariationGb().equals("02")
									&& i.getSeq().equals(o.getVariationSeq2().toString()) && i.getDelYn().equals("02"))
							.collect(Collectors.toList());

					if (l.size() == 0) {
						isError = true;
					}

				} else {

					if (o.getVariationValue2() != null && o.getVariationValue2().trim().length() > 0) {

						List<TbGoodsOptionValue> l = tgovList.stream()
								.filter(i -> i.getVariationGb().equals("02")
										&& i.getOptionNm().equals(o.getVariationValue2().toString())
										&& i.getDelYn().equals("02"))
								.collect(Collectors.toList());

						if (l.size() > 0) {
							vSeq2 = l.get(0).getSeq().toString();
						} else {
							isError = true;
						}
					}
				}

				
		
				if (o.getVariationSeq3() != null && o.getVariationSeq3().trim().length() > 0) {
					vSeq3 = o.getVariationSeq3().toString();

					List<TbGoodsOptionValue> l = tgovList.stream()
							.filter(i -> i.getVariationGb().equals("03")
									&& i.getSeq().equals(o.getVariationSeq3().toString()) && i.getDelYn().equals("02"))
							.collect(Collectors.toList());

					if (l.size() == 0) {
						isError = true;
					}

				} else {
					if (o.getVariationValue3() != null && o.getVariationValue3().trim().length() > 0) {

				
						List<TbGoodsOptionValue> l = tgovList.stream()
								.filter(i -> i.getVariationGb().equals("03")
										&& i.getOptionNm().equals(o.getVariationValue3().toString())
										&& i.getDelYn().equals("02"))
								.collect(Collectors.toList());

						if (l.size() > 0) {
							vSeq3 = l.get(0).getSeq().toString();
						} else {
							isError = true;
						}
					}
				}
					if (isError) {
					throw new IllegalArgumentException("option value check..");
				}

				List<TbGoodsOption> l = jpaTbGoodsOptionRepository.findOptionList(goodsPostRequestData.getAssortId(),
						vSeq1, vSeq2, vSeq3);

				if (l.size() == 0) {
					String itemId = "";
					if (maxItemId == null) {
						itemId = "0001";
						maxItemId = itemId;
					} else {
						itemId = Utilities.plusOne(maxItemId, 4);
						maxItemId = itemId;
					}

					o.setItemId(itemId);

					TbGoodsOption tbGoodsOption = new TbGoodsOption(goodsPostRequestData.getAssortId(), o);

					tbGoodsOption.setItemId(o.getItemId());
					tbGoodsOption.setRegId(userId);
					tbGoodsOption.setDelYn("02");
					tbGoodsOption.setItemNm(goodsPostRequestData.getAssortNm());

					if (vSeq1 != null && vSeq1.trim().length() > 0) {
						tbGoodsOption.setVariationGb1("01");
						tbGoodsOption.setVariationSeq1(vSeq1);

					}

					if (vSeq2 != null && vSeq2.trim().length() > 0) {
						tbGoodsOption.setVariationGb2("02");
						tbGoodsOption.setVariationSeq2(vSeq2);

					}

					if (vSeq3 != null && vSeq3.trim().length() > 0) {
						tbGoodsOption.setVariationGb3("03");
						tbGoodsOption.setVariationSeq3(vSeq3);

					}

					tbGoodsOption.setShortYn("01");
					tbGoodsOption.setUpdId(userId);
					jpaTbGoodsOptionRepository.save(tbGoodsOption);

				} else {

					TbGoodsOption tgo = l.get(0);

					o.setItemId(l.get(0).getItemId());

					if (tgo.getDelYn().equals("01")) {
						tgo.setDelYn("02");
						tgo.setUpdId(userId);
						jpaTbGoodsOptionRepository.save(tgo);
					}

				}



			} else {
				// itemID가 있음
				// 아이템이 있는경우에도 값을 이상하게 보낼수가 있어서 관련 로직 추가

				
				TbGoodsOption tgo = jpaTbGoodsOptionRepository
						.findByAssortIdAndItemId(goodsPostRequestData.getAssortId(), o.getItemId());
					
				// tgo.setUpdId(userId);
				boolean isError = false;
				String vSeq1 = "";
				String vSeq2 = "";
				String vSeq3 = "";

				if (o.getVariationSeq1() != null && o.getVariationSeq1().trim().length() > 0) {
					vSeq1 = o.getVariationSeq1().toString();

					List<TbGoodsOptionValue> l = tgovList.stream()
							.filter(i -> i.getVariationGb().equals("01")
									&& i.getSeq().equals(o.getVariationSeq1().toString()) && i.getDelYn().equals("02"))
							.collect(Collectors.toList());

					if (l.size() == 0) {
						isError = true;
					}

				} else {

					if (o.getVariationValue1() != null && o.getVariationValue1().trim().length() > 0) {
						List<TbGoodsOptionValue> l = tgovList.stream()
								.filter(i -> i.getVariationGb().equals("01")
										&& i.getOptionNm().equals(o.getVariationValue1().toString())
										&& i.getDelYn().equals("02"))
								.collect(Collectors.toList());

						if (l.size() > 0) {
							vSeq1 = l.get(0).getSeq().toString();
						} else {
							isError = true;
						}

					}

				}

				if (o.getVariationSeq2() != null && o.getVariationSeq2().trim().length() > 0) {
					vSeq2 = o.getVariationSeq2().toString();

					List<TbGoodsOptionValue> l = tgovList.stream()
							.filter(i -> i.getVariationGb().equals("02")
									&& i.getSeq().equals(o.getVariationSeq2().toString()) && i.getDelYn().equals("02"))
							.collect(Collectors.toList());

					if (l.size() == 0) {
						isError = true;
					}

				} else {

					if (o.getVariationValue2() != null && o.getVariationValue2().trim().length() > 0) {

						List<TbGoodsOptionValue> l = tgovList.stream()
								.filter(i -> i.getVariationGb().equals("02")
										&& i.getOptionNm().equals(o.getVariationValue2().toString())
										&& i.getDelYn().equals("02"))
								.collect(Collectors.toList());

						if (l.size() > 0) {
							vSeq2 = l.get(0).getSeq().toString();
						} else {
							isError = true;
						}
					}
				}

				if (o.getVariationSeq3() != null && o.getVariationSeq3().trim().length() > 0) {
					vSeq3 = o.getVariationSeq3().toString();

					List<TbGoodsOptionValue> l = tgovList.stream()
							.filter(i -> i.getVariationGb().equals("03")
									&& i.getSeq().equals(o.getVariationSeq3().toString()) && i.getDelYn().equals("02"))
							.collect(Collectors.toList());

					if (l.size() == 0) {
						isError = true;
					}

				} else {
					if (o.getVariationValue3() != null && o.getVariationValue3().trim().length() > 0) {

						List<TbGoodsOptionValue> l = tgovList.stream()
								.filter(i -> i.getVariationGb().equals("03")
										&& i.getOptionNm().equals(o.getVariationValue3().toString())
										&& i.getDelYn().equals("02"))
								.collect(Collectors.toList());

						if (l.size() > 0) {
							vSeq3 = l.get(0).getSeq().toString();
						} else {
							isError = true;
						}
					}
				}
				if (isError) {
					throw new IllegalArgumentException("option value check..");
				}

//				List<TbGoodsOption> l = jpaTbGoodsOptionRepository.findOptionList(goodsInsertRequestData.getAssortId(),
				// vSeq1, vSeq2, vSeq3);

				tgo.setDelYn("02");
				tgo.setUpdId(userId);
				jpaTbGoodsOptionRepository.save(tgo);

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("itemId", o.getItemId());
				existedList.add(m);

			}
		}

		List<HashMap<String, Object>> deleteList = new ArrayList<HashMap<String, Object>>();

		for (TbGoodsOption o : oriTgoList) {
			boolean chk = false;
			for (HashMap<String, Object> v : existedList) {
				if (v.get("itemId").equals(o.getItemId())

				) {
					chk = true;
					break;
				}

			}

			if (chk == false) {
				deleteList.add(new HashMap<String, Object>() {
					{// 초기값 지정
						put("itemId", o.getItemId());
					}
				});
			}
		}

	
		for (HashMap<String, Object> v : deleteList) {

			TbGoodsOption tgo = jpaTbGoodsOptionRepository.findByAssortIdAndItemId(goodsPostRequestData.getAssortId(),
					v.get("itemId").toString());
			
			tgo.setDelYn("01");
			tgo.setUpdId(userId);
			
			jpaTbGoodsOptionRepository.save(tgo);
			
		}

	

	}

	// 2022-03-24 수집까지 성공 todo 삭제랑 싱글일떄 처리가 필요

	// 2022-03-29 사용안함
//	private List<TbGoodsOption> updateTbGoodsOptionList2(GoodsInsertRequestData goodsInsertRequestData,
//			List<TbGoodsOption> existTbGoodsOptionList, List<TbGoodsOptionValue> tbGoodsOptionValueList,
//			String userId) {
//
//		List<GoodsInsertRequestData.Items> itemList = goodsInsertRequestData.getItems();
//		List<TbGoodsOption> TbGoodsOptionList = new ArrayList<>();
//		Set<String> itemIdList = new HashSet<>();
//		Set<String> removeItemIdList = new HashSet<>();
//
//		List<TbGoodsOption> oriTgoList = jpaTbGoodsOptionRepository
//				.findByAssortIdAndDelYn(goodsInsertRequestData.getAssortId(), "02");
//
//		//itemId = this.findMaxSeq(itemIdList);
//		String maxItemId = jpaTbGoodsOptionRepository.findMaxItemIdByAssortId(goodsInsertRequestData.getAssortId());
//		
//		for(GoodsInsertRequestData.Items o:itemList) {
//			if (o.getItemId() == null || o.getItemId().trim().length() == 0) {
//				String itemId="";
//				if(maxItemId==null) {
//					itemId = Utilities.plusOne(itemId, 4);
//				}else {
//					itemId = Utilities.plusOne(maxItemId, 4);
//				}
//				 
//				o.setItemId(itemId);
//			
//				
//				TbGoodsOption tbGoodsOption = new TbGoodsOption(goodsInsertRequestData.getAssortId(), o);
//				
//				tbGoodsOption.setItemId(o.getItemId());
//				tbGoodsOption.setRegId(userId);
//				
//			} else {
//
//
//			}
//		}
//
//		for (TbGoodsOption i : existTbGoodsOptionList) {
//			itemIdList.add(i.getItemId());
//			removeItemIdList.add(i.getItemId());
//		}
//
//		for (GoodsInsertRequestData.Items item : itemList) {
//			List<TbGoodsOption> origTbGoodsOptionList = existTbGoodsOptionList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//							&& x.getItemId().equals(item.getItemId()))
//					.collect(Collectors.toList());
//			TbGoodsOption TbGoodsOption = origTbGoodsOptionList.size() > 0 ? origTbGoodsOptionList.get(0) : null;// jpaTbGoodsOptionRepository.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(),
//																													// itemId);
//			String itemId = item.getItemId(); // item id를 객체가 갖고 있으면 그것을 이용
////            TbGoodsOption TbGoodsOption = new TbGoodsOption(goodsInsertRequestData.getAssortId(), item);
////            if(itemId == null || itemId.trim().equals("")){ // 객체에 item id가 없으면 jpa에서 max값을 가져옴
//			if (!itemIdList.contains(itemId) && !itemId.trim().equals("")) {
//				log.debug("기존 TbGoodsOption의 itemIdList에 " + itemId + "가 존재하지 않습니다.");
//				continue;
//			}
//			if (TbGoodsOption == null) { // 객체에 item id가 없으면 jpa에서 max값을 가져옴
//				TbGoodsOption = new TbGoodsOption(goodsInsertRequestData.getAssortId(), item);
//				itemId = this.findMaxSeq(itemIdList);// jpaTbGoodsOptionRepository.findMaxItemIdByAssortId(goodsInsertRequestData.getAssortId());
//				if (itemId == null || itemId.trim().equals("")) { // jpa에서 max값을 가져왔는데 null이면 해당 assort id에 item id가
//																	// 존재하지 않으므로 초기값(0001)을 설정
//					itemId = StringFactory.getFourStartCd();
//				} else { // jpa에서 max값을 가져온 경우 1을 더한 후 item id로 삼음
//					itemId = Utilities.plusOne(itemId, 4);
//				}
//				TbGoodsOption.setItemId(itemId);
//				TbGoodsOption.setRegId(userId);
//
//				itemIdList.add(itemId);
//			} else { // 존재하는 경우 : tbGoodsOptionValue 객체가 존재함이 보장됨 -> update
//				if (TbGoodsOption.getDelYn().equals(StringFactory.getGbOne())
//						|| TbGoodsOption.getItemId().equals(StringFactory.getFourStartCd())) { // 삭제된 상태거나 seq 0001인
//																								// tbGoodsOptionValue는
//																								// 수정x
//					log.debug("delYn이 01이거나 itemId가 0001(단품)인 TbGoodsOption를 update할 수 없습니다.");
//					continue;
//				}
//				removeItemIdList.remove(itemId);
//			}
//
//			// 옵션1 관련값 찾아넣기
//			TbGoodsOptionValue op1 = tbGoodsOptionValueList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//							&& x.getVariationGb().equals(StringFactory.getGbOne())
//							&& x.getOptionNm().equals(item.getVariationValue1()))
//					.collect(Utilities.toSingleton());
//			if (op1 != null) {
//				TbGoodsOption.setVariationGb1(op1.getOptionGb());
//				TbGoodsOption.setVariationSeq1(op1.getSeq());
//			}
//			// 옵션2 관련값 찾아넣기
//			TbGoodsOptionValue op2 = tbGoodsOptionValueList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//							&& x.getVariationGb().equals(StringFactory.getGbTwo())
//							&& x.getOptionNm().equals(item.getVariationValue2()))
//					.collect(Utilities.toSingleton());
//			if (op2 != null) {
//				TbGoodsOption.setVariationGb2(op2.getOptionGb());
//				TbGoodsOption.setVariationSeq2(op2.getSeq());
//			}
//			// 옵션3 관련값 찾아넣기
//			TbGoodsOptionValue op3 = tbGoodsOptionValueList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//							&& x.getVariationGb().equals(StringFactory.getGbThree())
//							&& x.getOptionNm().equals(item.getVariationValue3()))
//					.collect(Utilities.toSingleton());
//			if (op3 != null) {
//				TbGoodsOption.setVariationGb3(op3.getOptionGb());
//				TbGoodsOption.setVariationSeq3(op3.getSeq());
//			}
//			TbGoodsOption.setAddPrice(item.getAddPrice() == null || item.getAddPrice().trim().equals("") ? null
//					: Float.parseFloat(item.getAddPrice()));
//			TbGoodsOption.setShortYn(item.getShortYn());
////            jpaTbGoodsOptionRepository.save(TbGoodsOption);
////            System.out.println("===== : " + TbGoodsOption.toString());
//
//			TbGoodsOption.setUpdId(userId);
//
//			jpaTbGoodsOptionRepository.save(TbGoodsOption);
//			TbGoodsOptionList.add(TbGoodsOption);
//		}
//		for (TbGoodsOption i : existTbGoodsOptionList) {
//			if (removeItemIdList.contains(i.getItemId())) {
//				i.setDelYn(StringFactory.getGbOne());
//			}
//
//			i.setUpdId(userId);
//			jpaTbGoodsOptionRepository.save(i);
//		}
//
//		int TbGoodsOptionDelNo = 0;
//		for (TbGoodsOption i : TbGoodsOptionList) {
//			if (i.getDelYn().equals(StringFactory.getGbTwo())) {
//				TbGoodsOptionDelNo++;
//			}
//		}
//		if (TbGoodsOptionDelNo == 0) {
//			TbGoodsOption singleTbGoodsOption = existTbGoodsOptionList.stream()
//					.filter(x -> x.getItemId().equals(StringFactory.getFourStartCd())).collect(Collectors.toList())
//					.get(0);
//			singleTbGoodsOption.setDelYn(StringFactory.getGbTwo());
//			singleTbGoodsOption.setUpdId(userId);
//			jpaTbGoodsOptionRepository.save(singleTbGoodsOption);
//		}
//
//		List<TbGoodsOptionSupplier> oriSupplierList = jpaTbGoodsOptionSupplierRepository
//				.findByAssortId(goodsInsertRequestData.getAssortId());
//
//		List<HashMap<String, Object>> existedSupplierList = new ArrayList<HashMap<String, Object>>();
//
//		// supplier add
//		for (GoodsInsertRequestData.Items o : itemList) {
//
//			List<GoodsInsertRequestData.itemSupplier> itemSupplierList = o.getItemSupplier();
//
//			for (GoodsInsertRequestData.itemSupplier v : itemSupplierList) {
//
//		
//				if (v.getSno() == null) {
//					TbGoodsOptionSupplier tgos = new TbGoodsOptionSupplier(goodsInsertRequestData.getAssortId(), v);
//					tgos.setUpdId(userId);
//					jpaTbGoodsOptionSupplierRepository.save(tgos);
//				} else if (v.getSno() != null) {
//
//					TbGoodsOptionSupplier tg = jpaTbGoodsOptionSupplierRepository.findById(v.getSno()).orElse(null);
//
//					if (tg != null) {
//						tg.setAssortId(goodsInsertRequestData.getAssortId());
//						tg.setItemId(o.getItemId());
//						tg.setSalePrice(v.getSalePrice());
//						tg.setStockCnt(v.getStockCnt());
//						tg.setSaleYn(v.getSaleYn());
//						tg.setSupplierId(v.getSupplierId());
//						tg.setUpdId(userId);
//						jpaTbGoodsOptionSupplierRepository.save(tg);
//
//						HashMap<String, Object> s = new HashMap<String, Object>();
//
//						s.put("sno", v.getSno());
//
//						existedSupplierList.add(s);
//					}
//
//				}
//
//			}
//
//		}
//
//		// 비교
//		List<HashMap<String, Object>> deleteSupplierList = new ArrayList<HashMap<String, Object>>();
//
//		for (TbGoodsOptionSupplier o : oriSupplierList) {
//			boolean chk = false;
//			for (HashMap<String, Object> v : existedSupplierList) {
//				if (v.get("sno") == o.getSno()) {
//					chk = true;
//					break;
//				}
//
//			}
//
//			if (chk == false) {
//				deleteSupplierList.add(new HashMap<String, Object>() {
//					{// 초기값 지정
//						put("sno", o.getSno());
//					}
//				});
//			}
//		}
//
//		// 삭제
//		for (HashMap<String, Object> v : deleteSupplierList) {
//			jpaTbGoodsOptionSupplierRepository.deleteById((Long) v.get("sno"));
//		}
//
//		return TbGoodsOptionList;
//	}

//    private List<TbGoodsOption> saveItemList(GoodsInsertRequestData goodsInsertRequestData) {
//        if(goodsInsertRequestData.getOptionUseYn().equals(StringFactory.getGbTwo())){
//            return saveSingleItem(goodsInsertRequestData);
//        }
//        List<GoodsInsertRequestData.Items> itemList = goodsInsertRequestData.getItems();
//        List<TbGoodsOption> TbGoodsOptionList = new ArrayList<>();
//        for(GoodsInsertRequestData.Items item : itemList){
//            String itemId = item.getItemId(); // item id를 객체가 갖고 있으면 그것을 이용
//            TbGoodsOption TbGoodsOption = new TbGoodsOption(goodsInsertRequestData.getAssortId(), item);
//            if(itemId == null || itemId.trim().equals("")){ // 객체에 item id가 없으면 jpa에서 max값을 가져옴
//                itemId = jpaTbGoodsOptionRepository.findMaxItemIdByAssortId(goodsInsertRequestData.getAssortId());
//                if(itemId == null || itemId.trim().equals("")){ // jpa에서 max값을 가져왔는데 null이면 해당 assort id에 item id가 존재하지 않으므로 초기값(0001)을 설정
//                    itemId = StringFactory.getFourStartCd();
//                }
//                else { // jpa에서 max값을 가져온 경우 1을 더한 후 item id로 삼음
//                    itemId = Utilities.plusOne(itemId, 4);
//                }
//                TbGoodsOption.setItemId(itemId);
//            }
//            else{ // 객체에 item id가 있으면 해당 객체가 이미 존재하므로 객체를 가져옴 (update)
//                TbGoodsOption = jpaTbGoodsOptionRepository.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(), itemId);
//            }
//            System.out.println("1 : "+System.currentTimeMillis());
//            // 옵션1 관련값 찾아넣기
//            HashMap<String, Object> op1 = myBatisGoodsDao.selectOneSeqOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue1());//jpatbGoodsOptionValueRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue1());
//            if(op1 != null){
//                TbGoodsOption.setVariationGb1((String)op1.get("optionGb"));
//                TbGoodsOption.setVariationSeq1((String)op1.get("seq"));
//            }
//            System.out.println("2 : "+System.currentTimeMillis());
//            // 옵션2 관련값 찾아넣기
//            HashMap<String, Object> op2 = myBatisGoodsDao.selectOneSeqOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue2()); //tbGoodsOptionValue op2 = jpatbGoodsOptionValueRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), item.getVariationValue2());
//            if(op2 != null){
//                TbGoodsOption.setVariationGb2((String)op2.get("optionGb"));
//                TbGoodsOption.setVariationSeq2((String)op2.get("seq"));
//            }
//            System.out.println("3 : "+System.currentTimeMillis());
////            String[] optionNmList = item.getValue().split(StringFactory.getSplitGb());
////            // tbGoodsOptionValue에서 옵션 형질 찾아오기
////            for(String optionNm : optionNmList){
////                tbGoodsOptionValue op = jpatbGoodsOptionValueRepository.findByAssortIdAndOptionNm(goodsInsertRequestData.getAssortId(), optionNm);
////                String opGb = op.getOptionGb();
////                if(opGb.equals(StringFactory.getGbOne())){ // optionGb이 01인 경우
////                    TbGoodsOption.setVariationGb1(opGb);
////                    TbGoodsOption.setVariationSeq1(op.getSeq());
////                }
////                else if(opGb.equals(StringFactory.getGbTwo())){ // optionGb이 02인 경우
////                    TbGoodsOption.setVariationGb2(opGb);
////                    TbGoodsOption.setVariationSeq2(op.getSeq());
////                }
////            }
//            TbGoodsOption.setAddPrice(item.getAddPrice());
//            TbGoodsOption.setShortYn(item.getShortYn());
//            jpaTbGoodsOptionRepository.save(TbGoodsOption);
//            TbGoodsOptionList.add(TbGoodsOption);
//        }
//        return TbGoodsOptionList;
//    }

	/**
	 * 21-06-11 Pecan 단품 옵션을 가진 아이템 1개를 저장하는 함수
	 * 
	 * @param goodsInsertRequestData
	 * @return
	 */

	// 2022-03-29 사용안함
//	private List<TbGoodsOption> saveSingleItem(GoodsInsertRequestData goodsInsertRequestData, String userId) {
//		List<TbGoodsOption> TbGoodsOptionList = new ArrayList<>();
//		TbGoodsOption TbGoodsOption = jpaTbGoodsOptionRepository
//				.findByAssortIdAndItemId(goodsInsertRequestData.getAssortId(), StringFactory.getFourStartCd());
//		if (TbGoodsOption == null) {
//			TbGoodsOption = new TbGoodsOption(goodsInsertRequestData);
//			TbGoodsOption.setItemId(StringFactory.getFourStartCd()); // 0001
//			TbGoodsOption.setVariationGb1(StringFactory.getGbOne()); // 01
//			TbGoodsOption.setVariationSeq1(StringFactory.getFourStartCd()); // 0001
//
//			TbGoodsOption.setRegId(userId);
//			TbGoodsOption.setUpdId(userId);
//
//			jpaTbGoodsOptionRepository.save(TbGoodsOption);
////            em.persist(TbGoodsOption);
//		} else {
//			TbGoodsOption.setDelYn(StringFactory.getGbTwo()); // 삭제 상태였던 걸 원래대로
//		}
//		TbGoodsOptionList.add(TbGoodsOption);
//
//		return TbGoodsOptionList;
//	}
//


	public void updateById(String goodsId, TbGoods goods, String userId) {
		Optional<TbGoods> e = jpaTbGoodsRepository.findById(goodsId);
		if (e.isPresent()) {
			e.get().setAssortId(goods.getAssortId());
			e.get().setAssortNm(goods.getAssortNm());

			goods.setUpdId(userId);

			jpaTbGoodsRepository.save(goods);
		}
	}

	/**
	 * 21-04-29 Pecan assortId를 통해 detail 페이지를 구성하는 정보를 반환하는 함수
	 * 
	 * @param assortId
	 * @return GoodsResponseData
	 */
//	public GoodsSelectDetailResponseData getGoodsDetailPage(String assortId) {
//		TbGoods TbGoods = em.createQuery("select distinct(i) from TbGoods i " +
////                "left outer join fetch i.cmvdmr cv " +
////                "left outer join fetch i.ifBrand ib " +
//				"left outer join fetch i.tbGoodsOptionValueList ivList " + "where i.assortId=?1", TbGoods.class)
//				.setParameter(1, assortId).getSingleResult();// jpaTbGoodsRepository.findById(assortId).orElseThrow(()
//																// -> new ResourceNotFoundException());
//
////		System.out.println(TbGoods);
//		GoodsSelectDetailResponseData goodsSelectDetailResponseData = new GoodsSelectDetailResponseData(TbGoods);
//
//		// 카테고리벨류
//		if (TbGoods.getDispCategoryId() != null && !TbGoods.getDispCategoryId().trim().equals("")) {
//			Cmvdmr cmvdmr = TbGoods.getCmvdmr();
//			goodsSelectDetailResponseData.setVendorNm(
//					TbGoods.getVendorId() != null && !TbGoods.getVendorId().trim().equals("") ? cmvdmr.getVdNm() : "");
//		}
//		// brand
//		// IfBrand ifBrand;
//		Itbrnd itbrnd;
//		if (TbGoods.getBrandId() != null && !TbGoods.getBrandId().trim().equals("")) {
//			itbrnd = TbGoods.getItbrnd();// jpaIfBrandRepository.findByChannelGbAndBrandId(StringFactory.getGbOne(),TbGoods.getBrandId());
//			goodsSelectDetailResponseData.setBrandNm(itbrnd == null ? null : itbrnd.getBrandNm());
//		}
//		List<GoodsSelectDetailResponseData.Description> descriptions = this
//				.makeDescriptions(jpaItasrdRepository.findByAssortId(TbGoods.getAssortId()));
//
//		List<GoodsSelectDetailResponseData.Attributes> attributesList = this
//				.makeAttributesList(TbGoods.getTbGoodsOptionValueList());
//
//		List<GoodsSelectDetailResponseData.Items> itemsList = this
//				.makeItemsList(jpaTbGoodsOptionRepository.findByAssortId(TbGoods.getAssortId()));
//		List<Itaimg> itaimgList = jpaItaimgRepository.findByAssortId(TbGoods.getAssortId());
//		List<GoodsResponseData.UploadMainImage> uploadMainImageList = this
//				.makeUploadMainImageList(itaimgList.stream()
//						.filter(x -> x.getImageGb().equals(StringFactory.getGbOne())).collect(Collectors.toList()));
//		List<GoodsSelectDetailResponseData.UploadAddImage> uploadAddImageList = this.makeUploadAddImageList(itaimgList
//				.stream().filter(x -> x.getImageGb().equals(StringFactory.getGbTwo())).collect(Collectors.toList()));
//		goodsSelectDetailResponseData.setDescription(descriptions);
//		goodsSelectDetailResponseData.setAttributes(attributesList);
//		goodsSelectDetailResponseData.setItems(itemsList);
//		goodsSelectDetailResponseData.setUploadMainImage(uploadMainImageList);
//		goodsSelectDetailResponseData.setUploadAddImage(uploadAddImageList);
//		goodsSelectDetailResponseData.setDeleteImage(new ArrayList<>());
//		goodsSelectDetailResponseData = goodsSelectDetailResponseDataMapper.nullToEmpty(goodsSelectDetailResponseData);
//		return goodsSelectDetailResponseData;
//	}

	public GoodsResponseData getGoodsDetailPage2(String assortId) {


		TbGoods TbGoods = jpaTbGoodsRepository.findByAssortId(assortId);

		// System.out.println(TbGoods.getAssortNm());

		GoodsResponseData goodsResponseData = new GoodsResponseData(TbGoods);


		Cmvdmr cmvdmr = TbGoods.getCmvdmr();
		goodsResponseData.setVendorNm(
				TbGoods.getVendorId() != null && !TbGoods.getVendorId().trim().equals("") ? cmvdmr.getVdNm() : "");

		Itbrnd itbrnd;
		if (TbGoods.getBrandId() != null && !TbGoods.getBrandId().trim().equals("")) {
			itbrnd = TbGoods.getItbrnd();// jpaIfBrandRepository.findByChannelGbAndBrandId(StringFactory.getGbOne(),TbGoods.getBrandId());
			goodsResponseData.setBrandNm(itbrnd == null ? null : itbrnd.getBrandNm());
		}


		goodsResponseData.setAttributes(makeAttributesList(TbGoods.getTbGoodsOptionValueList()));
		goodsResponseData
				.setItems(makeItemsList(assortId));

		goodsResponseData.setAddInfos(makeAddInfoList(TbGoods.getAssortId()));

		goodsResponseData.setUploadMainImage(makeUploadMainImageList(TbGoods.getAssortId()));
		goodsResponseData.setUploadAddImage(makeUploadAddImageList(TbGoods.getAssortId()));
		goodsResponseData.setDeleteImage(new ArrayList<>());


		goodsResponseData = goodsResponseDataMapper.nullToEmpty(goodsResponseData);
		return goodsResponseData;
	}

	private List<GoodsResponseData.AddInfo> makeAddInfoList(String assortId) {
		List<TbGoodsAddInfo> list = jpaTbGoodsAddInfoRepository.findByAssortId(assortId);

		List<GoodsResponseData.AddInfo> r = new ArrayList<>();

		if (list == null) {
			log.debug("add image list 존재하지 않습니다.");
			return r;
		}
		for (TbGoodsAddInfo o : list) {

			GoodsResponseData.AddInfo add = new GoodsResponseData.AddInfo(o);
			r.add(add);

		}
		return r;

	}

	private List<GoodsResponseData.UploadAddImage> makeUploadAddImageList(String assortId) {
		List<TbGoodsImage> addList = jpaTbGoodsImageRepository.findByAssortIdAndImageGb(assortId, "02");

		List<GoodsResponseData.UploadAddImage> uploadAddImageList = new ArrayList<>();
		if (addList == null) {
			log.debug("add image list 존재하지 않습니다.");
			return uploadAddImageList;
		}
		for (TbGoodsImage o : addList) {

			GoodsResponseData.UploadAddImage add = new GoodsResponseData.UploadAddImage(o);
			uploadAddImageList.add(add);

		}
		return uploadAddImageList;
	}

	private List<GoodsResponseData.UploadMainImage> makeUploadMainImageList(String assortId) {

		List<TbGoodsImage> mainList = jpaTbGoodsImageRepository.findByAssortIdAndImageGb(assortId, "01");

		List<GoodsResponseData.UploadMainImage> uploadMainImageList = new ArrayList<>();
		if (mainList == null) {
			log.debug("TbGoods.itaimgList가 존재하지 않습니다.");
			return uploadMainImageList;
		}
		for (TbGoodsImage o : mainList) {

			GoodsResponseData.UploadMainImage uploadMainImage = new GoodsResponseData.UploadMainImage(
					o);
				uploadMainImageList.add(uploadMainImage);

		}
		return uploadMainImageList;
	}

	// TbGoodsOption -> items 형태로 바꿔주는 함수
	private List<GoodsResponseData.Items> makeItemsList(String assortId) {

		
		List<TbGoodsOption> tbGoodsOptionList = jpaTbGoodsOptionRepository.findByAssortId(assortId);

		List<GoodsResponseData.Items> itemsList = new ArrayList<GoodsResponseData.Items>();

		List<TbGoodsOptionSupplier> supplierList = jpaTbGoodsOptionSupplierRepository.findByAssortId(assortId);
		
		for (TbGoodsOption o : tbGoodsOptionList) {
			if (o.getDelYn().equals("02")) {
				GoodsResponseData.Items item = new GoodsResponseData.Items();
				item.setItemId(o.getItemId());
				TbGoodsOptionValue op1 = o.getTbGoodsOptionValue1();// jpatbGoodsOptionValueRepository.findByAssortIdAndSeq(TbGoodsOption.getAssortId(),
				// TbGoodsOption.getVariationSeq1());
				String optionNm = op1 == null ? null : op1.getOptionNm();
				String seq = op1 == null ? null : op1.getSeq();
				item.setValue1(optionNm);
				item.setSeq1(seq);
				item.setStatus1(StringFactory.getStrR()); // r 하드코딩
				if (o.getVariationSeq2() != null) {
					TbGoodsOptionValue op2 = o.getTbGoodsOptionValue2();// jpatbGoodsOptionValueRepository.findByAssortIdAndSeq(TbGoodsOption.getAssortId(),
					// TbGoodsOption.getVariationSeq2());
					optionNm = op2 == null ? null : op2.getOptionNm();
					seq = op2 == null ? null : op2.getSeq();
					item.setSeq2(seq);
					item.setValue2(optionNm);
					item.setStatus2(StringFactory.getStrR()); // r 하드코딩
				}
				if (o.getVariationSeq3() != null) {
					TbGoodsOptionValue op3 = o.getTbGoodsOptionValue3();// jpatbGoodsOptionValueRepository.findByAssortIdAndSeq(TbGoodsOption.getAssortId(),
					// TbGoodsOption.getVariationSeq2());
					optionNm = op3 == null ? null : op3.getOptionNm();
					seq = op3 == null ? null : op3.getSeq();
					item.setSeq3(seq);
					item.setValue3(optionNm);
					item.setStatus3(StringFactory.getStrR()); // r 하드코딩
				}

//				item.setAddPrice(TbGoodsOption.getAddPrice() == null ? null : TbGoodsOption.getAddPrice() + "");
				// item.setShortageYn(TbGoodsOption.getShortYn());

				

				
				List<GoodsResponseData.itemSupplier> l = new ArrayList<GoodsResponseData.itemSupplier>();
				
				List<TbGoodsOptionSupplier> itemSupperList = supplierList.stream()
						.filter(x -> x.getItemId().equals(o.getItemId())).collect(Collectors.toList());
				
				for (TbGoodsOptionSupplier tgos : itemSupperList) {
					GoodsResponseData.itemSupplier v = new GoodsResponseData.itemSupplier(tgos);
					l.add(v);
				}
				
				item.setItemSupplier(l);

				item = goodsResponseDataMapper.nullToEmpty(item);
				itemsList.add(item);
			}
		}


		return itemsList;
	}

	// tbGoodsOptionValue -> attributes 형태로 바꿔주는 함수
	private List<GoodsResponseData.Attributes> makeAttributesList(
			List<TbGoodsOptionValue> tbGoodsOptionValueList) {

		List<GoodsResponseData.Attributes> attributesList = new ArrayList<>();

		if (tbGoodsOptionValueList == null) {
			log.debug("TbGoods.tbGoodsOptionValueList가 존재하지 않습니다.");
			return attributesList;
		}

		for (TbGoodsOptionValue o : tbGoodsOptionValueList) {
			if (o.getDelYn().equals(StringFactory.getGbTwo())) {
				GoodsResponseData.Attributes attr = new GoodsResponseData.Attributes(
						o);
				attributesList.add(attr);
			}

		}
		return attributesList;
	}

	// itasrd -> description 형태로 바꿔주는 함수
	private List<GoodsSelectDetailResponseData.Description> makeDescriptions(List<Itasrd> itasrdList) {
		List<GoodsSelectDetailResponseData.Description> descriptionList = new ArrayList<>();
		if (itasrdList == null) {
			log.debug("TbGoods.itasrdList가 존재하지 않습니다.");
			return descriptionList;
		}
		for (Itasrd itasrd : itasrdList) {
			GoodsSelectDetailResponseData.Description desc = new GoodsSelectDetailResponseData.Description();
			desc.setSeq(itasrd.getSeq());
			desc.setOrdDetCd(itasrd.getOrdDetCd());
			desc.setTextHtmlGb(itasrd.getTextHtmlGb());
			desc.setMemo(itasrd.getMemo());
			descriptionList.add(desc);
		}
		return descriptionList;
	}

	public GoodsListResponseData getGoodsList2(String shortageYn, LocalDate regDtBegin, LocalDate regDtEnd,
			String assortId, String assortNm) {
		boolean isAssortIdExist = assortId != null && !assortId.trim().equals("");
		boolean isAssortNmExist = assortNm != null && !assortNm.trim().equals("");

		LocalDateTime start = isAssortIdExist || isAssortNmExist ? null : regDtBegin.atStartOfDay();
		LocalDateTime end = isAssortIdExist || isAssortNmExist ? null : regDtEnd.atTime(23, 59, 59);
		GoodsListResponseData goodsListResponseData = new GoodsListResponseData(shortageYn,
				regDtBegin, regDtEnd, assortId, assortNm);

		LocalDateTime oldDay = Utilities.strToLocalDateTime(StringFactory.getOldDayT());
		LocalDateTime doomsDay = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());

		List<TbGoods> tbGoodsList = jpaTbGoodsRepository.findMasterList(start, end, shortageYn, assortId, assortNm,
				oldDay, doomsDay);// query.getResultList();
		List<GoodsListResponseData.Goods> goodsList = new ArrayList<>();
		if (tbGoodsList.size() == 0) {
			log.debug("검색 조건을 만족하는 상품이 존재하지 않습니다.");
			goodsListResponseData.setGoodsList(goodsList);
			return goodsListResponseData;
		}

		for (TbGoods o : tbGoodsList) {
			GoodsListResponseData.Goods goods = new GoodsListResponseData.Goods(o);
			goodsList.add(goods);
		}

		goodsListResponseData.setGoodsList(goodsList);
		return goodsListResponseData;
	}

	/**
	 * 21-05-10 Pecan brandId, dispCategoryId, regDt, shortageYn, (이상 TbGoods)
	 * dispCategoryId(itcatg), brandId(itbrnd) 로 list 목록 가져오는 함수
	 * 
	 * @param shortageYn, RegDtBegin, regDtEnd
	 * @return GoodsSelectListResponseData
	 */


	// 2022-0329 리스트 조회 사용안함
//	public GoodsSelectListResponseData getGoodsList(String shortageYn, LocalDate regDtBegin, LocalDate regDtEnd,
//			String assortId, String assortNm) {
//		boolean isAssortIdExist = assortId != null && !assortId.trim().equals("");
//		boolean isAssortNmExist = assortNm != null && !assortNm.trim().equals("");
//
//		LocalDateTime start = isAssortIdExist || isAssortNmExist ? null : regDtBegin.atStartOfDay();
//		LocalDateTime end = isAssortIdExist || isAssortNmExist ? null : regDtEnd.atTime(23, 59, 59);
//		GoodsSelectListResponseData goodsSelectListResponseData = new GoodsSelectListResponseData(shortageYn,
//				regDtBegin, regDtEnd, assortId, assortNm);
//
//		LocalDateTime oldDay = Utilities.strToLocalDateTime(StringFactory.getOldDayT());
//		LocalDateTime doomsDay = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
//
//		List<TbGoods> TbGoodsList = jpaTbGoodsRepository.findMasterList(start, end, shortageYn, assortId, assortNm,
//				oldDay, doomsDay);// query.getResultList();
//		List<GoodsSelectListResponseData.Goods> goodsList = new ArrayList<>();
//		if (TbGoodsList.size() == 0) {
//			log.debug("검색 조건을 만족하는 상품이 존재하지 않습니다.");
//			goodsSelectListResponseData.setGoodsList(goodsList);
//			return goodsSelectListResponseData;
//		}
////		List<Itbrnd> brandList;
//		// List<String> brandIdList = new ArrayList<>();
/////        for(TbGoods TbGoods : TbGoodsList){
//		// if(!brandIdList.contains(TbGoods.getBrandId())){
//		// brandIdList.add(TbGoods.getBrandId());
////            }
//		// }
//		// brandList =
//		// jpaIfBrandRepository.findByBrandIdListByChannelIdAndBrandIdList(StringFactory.getGbOne(),
//		// brandIdList);
//		// brand
//		for (TbGoods TbGoods : TbGoodsList) {
//			GoodsSelectListResponseData.Goods goods = new GoodsSelectListResponseData.Goods(TbGoods);
//			// List<IfBrand> brandList1 =
//			// brandList.stream().filter(x->x.getBrandId().equals(TbGoods.getBrandId())).collect(Collectors.toList());
//			// IfBrand ifBrand = brandList1 == null || brandList1.size() == 0? null :
//			// brandList1.get(0);//jpaIfBrandRepository.findByChannelGbAndChannelBrandId(StringFactory.getGbOne(),TbGoods.getBrandId());
//			// // 채널은 01 하드코딩
////            goods.setBrandNm(ifBrand==null? null:ifBrand.getBrandNm());
//			goodsList.add(goods);
//		}
//		goodsSelectListResponseData.setGoodsList(goodsList);
//		return goodsSelectListResponseData;
//	}
//
////    private GoodsInsertResponseData makeGoodsSelectListResponseData(List<TbGoods> goodsList) {
////        return null;
////    }

	@Transactional
	public Itaimg saveItaimg(String imageGb, FileVo f, String userId) {
		Itaimg ii = new Itaimg(imageGb, f);

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
		TbGoods TbGoods = jpaTbGoodsRepository.findById("000075775").orElseGet(() -> null);
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

	@Transactional
	public void deleteGoodsImage(Long sno) {

		TbGoodsImage tgi = jpaTbGoodsImageRepository.findById(sno).orElse(null);

		if (tgi != null) {
			Long imageSeq = tgi.getImageSeq();

			if (imageSeq != null) {
				Itaimg r = jpaItaimgRepository.findById(imageSeq).orElse(null);

				if (r != null) {
					FileVo f = new FileVo();
					f.setFilePath(r.getImagePath());
					f.setFileName(r.getImageName());

					String ret = fileService.deleteFile(f);

					if (ret.equals("success")) {
						jpaItaimgRepository.deleteById(imageSeq);
					}
				}

			}


		}

		jpaTbGoodsImageRepository.delete(tgi);

	}

	// 2022-03-29 사용안함
//	private List<TbGoodsOptionValue> updateTgovList(GoodsInsertRequestData goodsInsertRequestData,
//			List<TbGoodsOptionValue> existTgovList, String userId) {
//		List<GoodsInsertRequestData.Attributes> attributes = goodsInsertRequestData.getAttributes();
//		List<TbGoodsOptionValue> tgovList = new ArrayList<>();
//		Set<String> seqList = new HashSet<>();
//		Set<String> removeSeqList = new HashSet<>();
//		for (TbGoodsOptionValue o : existTgovList) {
//			seqList.add(o.getSeq());
//			removeSeqList.add(o.getSeq());
//		}
//
//		for (GoodsInsertRequestData.Attributes attribute : attributes) {
//			List<TbGoodsOptionValue> origTgovList = existTgovList.stream()
//					.filter(x -> x.getAssortId().equals(goodsInsertRequestData.getAssortId())
//							&& x.getSeq().equals(attribute.getSeq()))
//					.collect(Collectors.toList());
//			TbGoodsOptionValue tgov = origTgovList.size() > 0 ? origTgovList.get(0) : null;
//			String seq = attribute.getSeq();
////            tbGoodsOptionValue tbGoodsOptionValue = new tbGoodsOptionValue(goodsInsertRequestData);
////            tbGoodsOptionValue.setAssortId(goodsInsertRequestData.getAssortId());
//			if (!seqList.contains(seq) && !seq.trim().equals("")) {
//				log.debug("기존 tbGoodsOptionValue의 seqList에 " + seq + "가 존재하지 않습니다.");
//				continue;
//			}
////            if(seq == null || seq.trim().equals("")){ // seq가 존재하지 않는 경우 == 새로운 tbGoodsOptionValue INSERT -> seq max 값 따와야 함
//			if (tgov == null) { // seq가 존재하지 않는 경우 == 새로운 tbGoodsOptionValue INSERT -> seq max 값 따와야 함
//				tgov = new TbGoodsOptionValue(goodsInsertRequestData);
//				seq = this.findMaxSeq(seqList);// jpatbGoodsOptionValueRepository.findMaxSeqByAssortId(assortId);
//				if (seq == null) { // max값이 없음 -> 해당 assort id에서 첫 insert
//					seq = StringFactory.getFourStartCd();// fourStartCd;
//				} else { // max값 따옴 -> seq++
//					seq = Utilities.plusOne(seq, 4);
//				}
//				tgov.setSeq(seq);
//				seqList.add(seq);
//				tgov.setRegId(userId);
//			} else { // 존재하는 경우 : tbGoodsOptionValue 객체가 존재함이 보장됨 -> update
////                tbGoodsOptionValue = existtbGoodsOptionValueList.stream().filter(x->x.getAssortId().equals(goodsInsertRequestData.getAssortId()) && x.getSeq().equals(attribute.getSeq()))
////                        .collect(Collectors.toList()).get(0);//jpatbGoodsOptionValueRepository.findByAssortIdAndSeq(goodsInsertRequestData.getAssortId(), seq);
//				if (tgov.getDelYn().equals(StringFactory.getGbOne())
//						|| tgov.getSeq().equals(StringFactory.getFourStartCd())) { // 삭제된 상태거나 seq 0001인
//																					// tbGoodsOptionValue는 수정x
//					log.debug("delYn이 01이거나 seq가 0001(단품)인 tbGoodsOptionValue를 update할 수 없습니다.");
//					continue;
//				}
//				removeSeqList.remove(seq);
//			}
//			tgov.setOptionNm(attribute.getValue());
//			tgov.setOptionGb(attribute.getVariationGb());
//			tgov.setVariationGb(attribute.getVariationGb());
//			tgovList.add(tgov);
//
//			tgov.setUpdId(userId);
//
//			jpaTbGoodsOptionValueRepository.save(tgov);
//		}
//
//		for (TbGoodsOptionValue i : existTgovList) {
//			if (removeSeqList.contains(i.getSeq())) {
//				i.setDelYn(StringFactory.getGbOne());
//			}
//
//			i.setUpdId(userId);
//
//			jpaTbGoodsOptionValueRepository.save(i);
//		}
//
//		int tbGoodsOptionValueDelNo = 0;
//		for (TbGoodsOptionValue i : tgovList) {
//			if (i.getDelYn().equals(StringFactory.getGbTwo())) {
//				tbGoodsOptionValueDelNo++;
//			}
//		}
//		if (tbGoodsOptionValueDelNo == 0) {
//			TbGoodsOptionValue singleTgov = existTgovList.stream()
//					.filter(x -> x.getSeq().equals(StringFactory.getFourStartCd())).collect(Collectors.toList()).get(0);
//			singleTgov.setDelYn(StringFactory.getGbTwo());
//
//			singleTgov.setUpdId(userId);
//
//			jpaTbGoodsOptionValueRepository.save(singleTgov);
//		}
//		return tgovList;
//	}

}

