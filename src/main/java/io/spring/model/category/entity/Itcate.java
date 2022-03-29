package io.spring.model.category.entity;

import io.spring.model.category.idclass.ItcateId;
import io.spring.model.common.entity.CommonProps;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="itcate")
@IdClass(ItcateId.class)
@Getter
@Setter
public class Itcate extends CommonProps implements Serializable {
    @Id
    private String categoryId; // 카테고리 아이디
    @Id
    private String channelId; // 채널 아이디 - 01 : 고도몰
    private LocalDateTime effStaDt; // 시작일
    private LocalDateTime effEndDt; // 종료일
    private String displayOrder; // 디스플레이 순서
    private String categoryNm; // 카테고리 이름
    private String displayNm; // 화면에 보여질 이름
    private String upCategoryId; // 부모 카테고리 id
    private String rootCategoryId; // 루트 카테고리 id
    private String isBottomYn; // 트리의 가장 하위인지 여부 - 01 : yes, 02 : no
    private String delYn; // 삭제여부 - 01 : yes, 02 : no
    private String userId; // 카테고리 관리 담당자 ( MD 등 )
    private int depth; // 뎁스
    private String linkUrl; // 링크
}
