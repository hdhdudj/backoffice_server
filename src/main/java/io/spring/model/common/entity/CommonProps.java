package io.spring.model.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public class CommonProps {
    protected CommonProps(long regId, long updId, Date regDt, Date updDt){
        this.regId = regId;
        this.updId = updId;
        this.regDt = regDt;
        this.updDt = updDt;
    }
    private Long regId;
    private Long updId;
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date regDt;
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date updDt;
}