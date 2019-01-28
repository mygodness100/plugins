package com.wy.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name="tr_user_role")
public class UserRole implements Serializable {
    @Id
    @Column(nullable=false)
    private Integer userId;

    @Id
    @Column(nullable=false)
    private Integer roleId;

    private static final long serialVersionUID = 1L;
}