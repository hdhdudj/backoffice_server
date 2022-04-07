package io.spring.service.goods;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.spring.infrastructure.util.exception.ResourceNotFoundException;
import io.spring.jparepos.goods.JpaItaimgRepository;
import io.spring.jparepos.goods.JpaProductsAddInfoRepository;
import io.spring.jparepos.goods.JpaProductsImageRepository;
import io.spring.jparepos.goods.JpaProductsMasterRepository;
import io.spring.jparepos.goods.JpaProductsRepository;
import io.spring.model.file.FileVo;
import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.entity.Products;
import io.spring.model.goods.entity.ProductsAddInfo;
import io.spring.model.goods.entity.ProductsImage;
import io.spring.model.goods.entity.ProductsMaster;
import io.spring.model.goods.request.ProductsMasterPostRequestData;
import io.spring.model.goods.request.ProductsPostRequestData;
import io.spring.model.goods.response.ProductsListResponseData;
import io.spring.model.goods.response.ProductsMasterListResponseData;
import io.spring.model.goods.response.ProductsMasterResponseData;
import io.spring.model.goods.response.ProductsResponseData;
import io.spring.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaProductsService {
	private final JpaProductsRepository jpaProductsRepository;
	private final JpaProductsImageRepository jpaProductsImageRepository;
	private final JpaItaimgRepository jpaItaimgRepository;

	private final JpaProductsAddInfoRepository jpaProductsAddInfoRepository;
	private final JpaProductsMasterRepository jpaProductsMasterRepository;

	private final FileService fileService;

	@Transactional
	public Long save(ProductsPostRequestData req) {


		Products r = saveProduct(req);
		req.setProductId(r.getProductId());

		this.saveImage(req);

		this.saveAddInfo(req);

		return req.getProductId();

	}

	public ProductsResponseData getItem(Long productId) {

		ProductsResponseData r = getProduct(productId);

		r.setMainImage(getProductMainImage(productId));
		r.setAddImage(getProductAddImage(productId));
		r.setAddInfos(getProductAddInfo(productId));

		return r;


	}

	public ProductsListResponseData getItemList(LocalDate start, LocalDate end, String saleYn,
			String displayYn, Long productId, String productNm, Long masterId, String masterNm) {

		LocalDateTime startDt = start.atStartOfDay();
		LocalDateTime endDt = end.atTime(23, 59, 59);

		ProductsListResponseData r = new ProductsListResponseData(start, end, saleYn, displayYn, productId, productNm,
				masterId, masterNm);


		// List<ProductsResponseData> l = getProductsList(startDt, endDt, saleYn,
		// displayYn, productId, productNm,
		// masterId,
		// masterNm);


		// r.setProductsList(l);
		return r;

	}

	private List<ProductsResponseData> getProductsList(LocalDateTime start, LocalDateTime end, String saleYn,
			String displayYn, Long productId, String productNm, Long masterId, String masterNm) {

		List<Products> l = jpaProductsRepository.findList(start, end, saleYn, displayYn, productId, productNm,
				masterId, masterNm);

		List<ProductsResponseData> r = new ArrayList<>();

		for (Products o : l) {
			ProductsResponseData v = new ProductsResponseData(o);

			v.setMainImage(getProductMainImage(o.getProductId()));
			v.setAddImage(getProductAddImage(o.getProductId()));
			v.setAddInfos(getProductAddInfo(o.getProductId()));

			r.add(v);
		}

		// r.setProductsList(l);

		return r;

	}

	private ProductsResponseData getProduct(Long productId) {

		Products p = jpaProductsRepository.findById(productId).orElse(null);

		ProductsResponseData r = new ProductsResponseData(p);

		return r;

	}

	private List<ProductsResponseData.MainImage> getProductMainImage(Long productId) {

		List<ProductsImage> l = jpaProductsImageRepository.findByProductIdAndImageGb(productId, "01");
		
		List<ProductsResponseData.MainImage> r = new ArrayList<ProductsResponseData.MainImage>();
		
		for(ProductsImage o:l) {
			ProductsResponseData.MainImage im = new  ProductsResponseData.MainImage(o);
			
			r.add(im);

		}
		
		return r;

	}

	private List<ProductsResponseData.AddImage> getProductAddImage(Long productId) {

		List<ProductsImage> l = jpaProductsImageRepository.findByProductIdAndImageGb(productId, "02");

		List<ProductsResponseData.AddImage> r = new ArrayList<ProductsResponseData.AddImage>();

		for (ProductsImage o : l) {
			ProductsResponseData.AddImage im = new ProductsResponseData.AddImage(o);

			r.add(im);

		}

		return r;

	}

	private List<ProductsResponseData.AddInfo> getProductAddInfo(Long productId) {

		List<ProductsAddInfo> l = jpaProductsAddInfoRepository.findByProductId(productId);

		List<ProductsResponseData.AddInfo> r = new ArrayList<ProductsResponseData.AddInfo>();

		for (ProductsAddInfo o : l) {
			ProductsResponseData.AddInfo im = new ProductsResponseData.AddInfo(o);

			r.add(im);

		}

		return r;

	}

	@Transactional
	public Long saveMaster(ProductsMasterPostRequestData req) {
		Long masterId = saveProductMaster(req);
		return masterId;
	}

	private Long saveProductMaster(ProductsMasterPostRequestData req) {

		ProductsMaster p = new ProductsMaster(req);
		jpaProductsMasterRepository.save(p);

		return p.getMasterId();

	}

	private Products saveProduct(ProductsPostRequestData v) {


		Products p = new Products(v);
		jpaProductsRepository.save(p);

		return p;


	}

	private void saveImage(ProductsPostRequestData r) {

		Long productId = r.getProductId();


		List<ProductsPostRequestData.MainImage> uploadMainImageList = r.getMainImage();
		List<ProductsPostRequestData.AddImage> uploadAddImageList = r.getAddImage();

		List<ProductsImage> oriMainImageList = jpaProductsImageRepository.findByProductIdAndImageGb(r.getProductId(),
				"01");

		List<ProductsImage> oriAddImageList = jpaProductsImageRepository.findByProductIdAndImageGb(r.getProductId(),
				"02");

		List<HashMap<String, Object>> mainList = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> addList = new ArrayList<HashMap<String, Object>>();

		// main image
		for (ProductsPostRequestData.MainImage o : uploadMainImageList) {

			if (o.getSno() == null) {
				ProductsImage tgi = new ProductsImage(productId, o);

				jpaProductsImageRepository.save(tgi);

			} else {

				ProductsImage t1 = jpaProductsImageRepository.findById(o.getSno()).orElse(null);

				if (t1 != null) {
					t1.setProductId(productId);
					t1.setImageGb(o.getImageGb() == null ? "01" : o.getImageGb());
					t1.setImageSeq(Long.valueOf(o.getUid()));
					t1.setImageUrl(o.getUrl());
				}

				jpaProductsImageRepository.save(t1);

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("sno", o.getSno());
				mainList.add(m);
			}
		}

		List<HashMap<String, Object>> deleteMainList = new ArrayList<HashMap<String, Object>>();

		for (ProductsImage o : oriMainImageList) {
			boolean chk = false;
			for (HashMap<String, Object> v : mainList) {
				if (v.get("sno") == o.getSno()) {
					chk = true;
					break;
				}

			}

			if (chk == false) {
				deleteMainList.add(new HashMap<String, Object>() {
					{// 초기값 지정
						put("sno", o.getSno());
					}
				});
			}
		}

		for (HashMap<String, Object> v : deleteMainList) {
			jpaProductsImageRepository.deleteById((Long) v.get("sno"));
		}

		// add image
		for (ProductsPostRequestData.AddImage o : uploadAddImageList) {

			if (o.getSno() == null) {
				ProductsImage tgi = new ProductsImage(productId, o);

				jpaProductsImageRepository.save(tgi);

			} else {

				ProductsImage t1 = jpaProductsImageRepository.findById(o.getSno()).orElse(null);

				if (t1 != null) {
					t1.setProductId(productId);
					t1.setImageGb(o.getImageGb() == null ? "02" : o.getImageGb());
					t1.setImageSeq(Long.valueOf(o.getUid()));
					t1.setImageUrl(o.getUrl());
				}

				jpaProductsImageRepository.save(t1);

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("sno", o.getSno());
				addList.add(m);
			}
		}

		List<HashMap<String, Object>> deleteAddList = new ArrayList<HashMap<String, Object>>();

		for (ProductsImage o : oriAddImageList) {
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
			jpaProductsImageRepository.deleteById((Long) v.get("sno"));
		}

	}

	@Transactional
	public void deleteImage(Long sno) {

		ProductsImage tgi = jpaProductsImageRepository.findById(sno).orElse(null);

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

			jpaProductsImageRepository.delete(tgi);
		} else {
			throw new ResourceNotFoundException();
		}

	}

	private void saveAddInfo(ProductsPostRequestData req) {

		String userId = req.getUserId();

		List<ProductsAddInfo> oriInfoList = jpaProductsAddInfoRepository.findByProductId(req.getProductId());

		List<HashMap<String, Object>> existedSupplierList = new ArrayList<HashMap<String, Object>>();

		List<ProductsPostRequestData.AddInfo> itemList = req.getAddInfos();

		System.out.println(itemList);

		// supplier add
		for (ProductsPostRequestData.AddInfo o : itemList) {

			if (o.getSno() == null || o.getSno().toString().length() == 0) {

				ProductsAddInfo tgai = new ProductsAddInfo();

				tgai.setProductId(req.getProductId());
				tgai.setInfoTitle(o.getInfoTitle());
				tgai.setInfoValue(o.getInfoValue());
				tgai.setRegId(req.getUserId());
				tgai.setUpdId(req.getUserId());
				jpaProductsAddInfoRepository.save(tgai);
			} else if (o.getSno().toString().length() > 0) {

				ProductsAddInfo tgai = jpaProductsAddInfoRepository.findById(o.getSno()).orElse(null);

				if (tgai != null) {
					tgai.setInfoTitle(o.getInfoTitle());
					tgai.setInfoValue(o.getInfoValue());

					tgai.setUpdId(req.getUserId());
					jpaProductsAddInfoRepository.save(tgai);

					HashMap<String, Object> s = new HashMap<String, Object>();

					s.put("sno", o.getSno());

					existedSupplierList.add(s);
				}

			}

		}

		// 비교
		List<HashMap<String, Object>> deleteList = new ArrayList<HashMap<String, Object>>();

		for (ProductsAddInfo o : oriInfoList) {
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
			jpaProductsAddInfoRepository.deleteById((Long) v.get("sno"));

		}

	}

	public ProductsMasterResponseData getMasterItem(Long masterId) {

		ProductsMasterResponseData r = getMaster(masterId);


		return r;

	}

	private ProductsMasterResponseData getMaster(Long masterId) {

		ProductsMaster v = jpaProductsMasterRepository.findById(masterId).orElse(null);
		if (v != null) {
			ProductsMasterResponseData r = new ProductsMasterResponseData(v);
			return r;
		} else {

			return null;
		}


	}

	public ProductsMasterListResponseData getMasterItemList(String masterNm) {

		List<ProductsMasterListResponseData.Master> l = getMastersList(masterNm);

		ProductsMasterListResponseData r = new ProductsMasterListResponseData(masterNm);

		r.setMastersList(l);

		return r;

	}

	private List<ProductsMasterListResponseData.Master> getMastersList(String masterNm) {
		List<ProductsMaster> l = jpaProductsMasterRepository.findList(masterNm);

		List<ProductsMasterListResponseData.Master> r = new ArrayList<>();
		for (ProductsMaster o : l) {
			ProductsMasterListResponseData.Master v = new ProductsMasterListResponseData.Master(o);
			r.add(v);
		}
		return r;
	}
}
