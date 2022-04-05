package io.spring.jparepos.common;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.common.entity.Suppliers;

public interface JpaSuppliersRepository extends JpaRepository<Suppliers, String> {



	

	/*
	 * 
	 * public interface JpaIfCategoryRepository extends JpaRepository<IfCategory,
	 * IfCategoryId> { IfCategory findByChannelGbAndChannelCategoryId(String
	 * channelGb, String channelCategoryId);
	 * 
	 * IfCategory findByChannelGbAndCategoryId(String channelGb, String categoryId);
	 * }
	 * 
	 */

}
