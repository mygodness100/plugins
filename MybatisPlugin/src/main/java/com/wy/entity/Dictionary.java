package com.wy.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name="ti_dictionary")
public class Dictionary implements Serializable {
    @Id
    @Column(nullable=false)
    @GeneratedValue(generator ="JDBC")
    private Integer dicId;

    //字典名
    @Column(nullable=false)
    private String dicName;

    //唯一标识符,不可重复
    @Column(nullable=false)
    private String dicCode;

    //上级字典
    @Column(nullable=false)
    private Integer parentId;

    //排序
    @Column(nullable=false)
    private Integer sort;

    private static final long serialVersionUID = 1L;
}