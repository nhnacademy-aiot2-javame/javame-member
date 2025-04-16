package com.nhnacademy.role.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

/**
 * 역할(권한) 정보를 저장하는 엔티티 클래스입니다.
 */
@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Role {

    /**
     * 역할 고유 식별자 ("ROLE_USER", "ROLE_ADMIN", "ROLE_OWNER").
     */
    @Id
    @Column(name = "role_id", length = 20)
    private String roleId;

    /**
     * 역할의 이름.
     */
    @Comment("권한 이름")
    @Column(name = "role_name", length = 20, nullable = false)
    private String roleName;

    /**
     * 역할에 대한 추가 설명.
     */
    @Comment("권한 설명")
    @Column(name = "role_description", length = 100)
    private String roleDescription;

    /**
     * Role 엔티티를 생성합니다.
     *
     * @param roleId          역할 ID
     * @param roleName        역할 이름
     * @param roleDescription 역할 설명
     */
    public Role(String roleId, String roleName, String roleDescription) {
        if (roleId == null || roleId.isBlank()) {
            throw new IllegalArgumentException("Role ID는 필수입니다.");
        }
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role 이름은 필수입니다.");
        }
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    /**
     * 역할의 이름과 설명을 수정합니다.
     * 역할 ID(roleId)는 변경할 수 없습니다.
     *
     * @param roleName        새로운 역할 이름
     * @param roleDescription 새로운 역할 설명
     */
    public void updateRoleDetails(String roleName, String roleDescription) {
        if (roleName != null && !roleName.isBlank()) {
            this.roleName = roleName;
        }
        this.roleDescription = roleDescription;
    }
}
