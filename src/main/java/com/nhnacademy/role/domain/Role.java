package com.nhnacademy.role.domain;

import jakarta.persistence.*;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Entity
@Table(name ="roles")
@ToString
public class Role {

    @Id
    @Column(name = "role_id", nullable = false)
    private String roleId;

    @Comment("권한 이름")
    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Comment("권한설명")
    @Column(name = "role_description")
    private String roleDescription;


    public Role(String roleId, String roleName, String roleDescription) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    public Role() {}

    public void updateRole(String roleName, String roleDescription){
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }
}
