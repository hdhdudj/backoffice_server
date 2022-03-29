package io.spring.model.category.entity;

import io.spring.model.common.entity.CommonProps;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="itcate")
public class Itcate extends CommonProps {
    @Id
    private String categoryId; // 카테고리 아이디
    @Id
    private String channelId; // 채널 아이디
    private String displayOrder; // 디스플레이 순서
    private String categoryNm; // 카테고리 이름
    private String displayNm; // 화면에 보여질 이름
    private String upCategoryId; // 부모 카테고리 id
    private String isBottomYn; // 트리의 가장 하위인지 여부 - 01 : yes, 02 : no
    private String userId; // 카테고리 관리 담당자 ( MD 등 )
}
