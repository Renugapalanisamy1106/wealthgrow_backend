package com.bfsi.entity;

import jakarta.persistence.*;

/**
 * JPA Entity for user_role table
 */
@Entity
@Table(name = "user_role", schema = "bfsimf_clean")
public class Role {

    @Id
    @Column(name = "role_id")
    private String roleId;

    @Column(name = "role_name")
    private String roleName;

    public Role() {
    }

    public Role(String roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}