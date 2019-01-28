package com.wy.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
@Table(name="ti_user")
public class User implements Serializable {
    @Id
    @Column(nullable=false)
    @GeneratedValue(generator ="JDBC")
    private Integer userId;

    //用户名
    @Column(nullable=false)
    private String username;

    //密码,md5加密
    @Column(nullable=false)
    private String password;

    //真实姓名
    @Column
    private String realname;

    @Column
    private Integer departId;

    //身份证号
    @Column
    private String idcard;

    //出生日期
    @Column
    private Date birthday;

    //年龄
    @Column
    private Integer age;

    //性别,男m,女f
    @Column
    private String sex;

    //家庭住址
    @Column
    private String address;

    //邮件
    @Column
    private String email;

    //工资
    @Column
    private BigDecimal salary;

    //电话
    @Column
    private String tel;

    //用户状态,0黑名单1正常
    @Column(nullable=false)
    private Byte state;

    //用户图标
    @Column
    private String userIcon;

    @Column(nullable=false)
    private Date createtime;

    @Column(nullable=false)
    private Date updatetime;

    private static final long serialVersionUID = 1L;
}