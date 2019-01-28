package com.wy.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name="tr_role_menu")
public class RoleMenu implements Serializable {
    @Column(nullable=false)
    private Integer roleId;

    @Column(nullable=false)
    private Integer menuId;

    private static final long serialVersionUID = 1L;
}