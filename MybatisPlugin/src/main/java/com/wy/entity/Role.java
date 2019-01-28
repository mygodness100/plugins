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
@Table(name="ti_role")
public class Role implements Serializable {
    @Id
    @Column(nullable=false)
    @GeneratedValue(generator ="JDBC")
    private Integer roleId;

    @Column(nullable=false)
    private String roleName;

    //角色状态0不可见,只有超级管理员不可见,1可见
    @Column
    private Byte roleState;

    //权限等级,当角色有多权限时,登录时以高权限展示菜单
    @Column(nullable=false)
    private Integer roleLevel;

    private static final long serialVersionUID = 1L;
}