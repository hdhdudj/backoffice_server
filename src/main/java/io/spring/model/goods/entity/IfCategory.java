package io.spring.model.goods.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.IfCategoryId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "if_category")
@Getter
@Setter
@NoArgsConstructor
@IdClass(value = IfCategoryId.class)
public class IfCategory extends CommonProps {
    @Id
    private String channelGb = StringFactory.getGbOne(); // 01 하드코딩;
    @Id
    private String channelCategoryId;
    private String channelCategoryNm;
    private String categoryId;
    private String categoryNm;
}
