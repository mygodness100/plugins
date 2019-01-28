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
@Table(name="ti_depart")
public class Depart implements Serializable {
    @Id
    @Column(nullable=false)
    @GeneratedValue(generator ="JDBC")
    private Integer departId;

    @Column(nullable=false)
    private String departName;

    @Column(nullable=false)
    private Integer parentId;

    //排序
    @Column(nullable=false)
    private Integer sort;

    private static final long serialVersionUID = 1L;
}