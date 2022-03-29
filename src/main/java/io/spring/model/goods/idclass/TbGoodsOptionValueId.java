package io.spring.model.goods.idclass;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class TbGoodsOptionValueId implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	public TbGoodsOptionValueId(String assortId, String seq) {
        this.assortId = assortId;
        this.seq = seq;
    }

	private String assortId;
	private String seq;
}

