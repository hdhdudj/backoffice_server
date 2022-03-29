package io.spring.model.goods.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "xml_test")
@Entity
public class XmlTest {
    @Id
    private String assortId;
    private String xml;
}
