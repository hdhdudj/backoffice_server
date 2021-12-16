package io.spring.jparepos.common;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.common.entity.Cmstgm;

public interface JpaCmstgmRepository extends JpaRepository<Cmstgm, String> {

	List<Cmstgm> findByUpStorageIdAndDefaultYnAndDelYn(String upStorageId, String defaultYn, String delYn);

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
