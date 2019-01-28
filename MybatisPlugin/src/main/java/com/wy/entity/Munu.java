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
@Table(name="ti_menu")
public class Munu implements Serializable {
    @Id
    @Column(nullable=false)
    @GeneratedValue(generator ="JDBC")
    private Integer menuId;

    @Column(nullable=false)
    private String menuName;

    @Column(nullable=false)
    private Integer parentId;

    @Column(nullable=false)
    private String menuUrl;

    //菜单图标,必填,默认star.svg
    @Column(nullable=false)
    private String menuIcon;

    //菜单国际化字段,可做唯一标识
    @Column(nullable=false)
    private String menuI18n;

    @Column(nullable=false)
    private Integer sort;

    private static final long serialVersionUID = 1L;
}