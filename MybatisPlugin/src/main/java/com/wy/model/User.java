package com.wy.model;

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

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column
	private String realname;

	@Column
	private Integer departId;

	@Column
	private String idcard;

	@Column
	private Date birthday;

	@Column
	private Integer age;

	@Column
	private String sex;

	@Column
	private String address;

	@Column
	private String email;

	@Column
	private BigDecimal salary;

	@Column
	private String tel;

	@Column(nullable = false)
	private Byte state;

	@Column
	private String userIcon;

	@Column(nullable = false)
	private Date createtime;

	@Column(nullable = false)
	private Date updatetime;
}