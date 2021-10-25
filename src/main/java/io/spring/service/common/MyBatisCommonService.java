package io.spring.service.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spring.dao.common.MyBatisCommonDao;

@Service
public class MyBatisCommonService {
	 @Autowired
	    private MyBatisCommonDao myBatisCommonDao;

		public HashMap<String, Object> getCategory(HashMap<String, Object> param) {
			HashMap<String, Object> category = myBatisCommonDao.getCategory(param);

	        return category;
	    }
		
		public List<HashMap<String, Object>> getBrandSearchList(HashMap<String, Object> param) {
			List<HashMap<String, Object>> list = myBatisCommonDao.getBrandSearchList(param);

	        return list;
	    }
		
				
		
		public List<HashMap<String, Object>> getPurchaseVendorSearchList(HashMap<String, Object> param) {
			List<HashMap<String, Object>> list = myBatisCommonDao.getPurchaseVendorSearchList(param);

			return list;
		}

		public List<HashMap<String, Object>> getCommonPurchaseVendor(HashMap<String, Object> param) {
			List<HashMap<String, Object>> list = myBatisCommonDao.getCommonPurchaseVendor(param);

			return list;
		}

		public List<HashMap<String, Object>> getCommonStorage(HashMap<String, Object> param) {
			List<HashMap<String, Object>> list = myBatisCommonDao.getCommonStorage(param);

			return list;
		}

		public List<HashMap<String, Object>> getCommonOrderStatus(HashMap<String, Object> param) {
			List<HashMap<String, Object>> list = myBatisCommonDao.getCommonOrderStatus(param);

			return list;
		}


		public LinkedList<String> findUpperCategory(String categoryId) {
			
			System.out.println("findUpperCategory");
			
			LinkedList<String> a =new LinkedList<String>();
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("categoryId", categoryId);
			
			HashMap<String, Object> category = myBatisCommonDao.getCategory(param);
			
			//Itcatg itcatg = jpaItcatgRepository.findById(categoryId).orElseGet(() -> null);
			
			
			if (category == null) {
				return null;
			}
			
			

			a.addFirst(category.get("categoryId").toString());
			
			String upCatId="";
			String catId="";
			
			upCatId = category.get("upCategoryId").toString();
			catId = upCatId;
			
			while (upCatId != "A00000000")  {
		

				if(!upCatId.equals("A00000000")) {
					HashMap<String, Object> p = new HashMap<String, Object>();
					p.put("categoryId", catId);
					HashMap<String, Object> categoryT = myBatisCommonDao.getCategory(p);
									
					a.addFirst(categoryT.get("categoryId").toString()); 
					upCatId = categoryT.get("upCategoryId").toString();
					catId = upCatId;
				}else {
					break;
				}
			
		 
		    }

			return a;
		}		
		
}
