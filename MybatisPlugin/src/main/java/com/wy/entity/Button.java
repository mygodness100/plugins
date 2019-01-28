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
@Table(name="ti_button")
public class Button implements Serializable {
    @Id
    @Column(nullable=false)
    @GeneratedValue(generator ="JDBC")
    private Integer buttonId;

    @Column(nullable=false)
    private String buttonName;

    @Column
    private Integer menuId;

    @Column(nullable=false)
    private Integer sortIndex;

    private static final long serialVersionUID = 1L;
}