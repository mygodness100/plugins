package com.wy.model;

import io.swagger.annotations.ApiModelProperty;
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
@Table(name = "simpleoa.`ti_user`")
public class User extends BaseModel<User> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(generator = "JDBC")
    private Integer userId;

    @ApiModelProperty("用户名")
    @Column(nullable = false)
    private String username;

    @ApiModelProperty("密码,md5加密")
    @Column(nullable = false)
    private String password;

    @ApiModelProperty("真实姓名")
    @Column
    private String realname;

    @Column
    private Integer departId;

    @ApiModelProperty("身份证号")
    @Column
    private String idcard;

    @ApiModelProperty("出生日期")
    @Column
    private Date birthday;

    @ApiModelProperty("年龄")
    @Column
    private Integer age;

    @ApiModelProperty("性别,男m,女f")
    @Column
    private String sex;

    @ApiModelProperty("家庭住址")
    @Column
    private String address;

    @ApiModelProperty("邮件")
    @Column
    private String email;

    @ApiModelProperty("工资")
    @Column
    private BigDecimal salary;

    @ApiModelProperty("电话")
    @Column
    private String tel;

    @ApiModelProperty("用户状态,0黑名单1正常")
    @Column(nullable = false)
    private Byte state;

    @ApiModelProperty("用户图标")
    @Column
    private String userIcon;

    @Column(nullable = false)
    private Date createtime;

    @Column(nullable = false)
    private Date updatetime;
}