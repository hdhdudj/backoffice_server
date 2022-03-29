package io.spring.model.category.idclass;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ItcateId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;
    public ItcateId(String categoryId, String channelId){
        this.categoryId = categoryId;
        this.channelId = channelId;
    }

    private String categoryId;
    private String channelId;
}
