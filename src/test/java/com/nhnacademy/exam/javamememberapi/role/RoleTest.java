//package com.nhnacademy.exam.javamememberapi.role;
//
//import com.nhnacademy.exam.javamememberapi.role.domain.Role;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.context.ActiveProfiles;
//
//@ActiveProfiles("test")
//@DataJpaTest
//class RoleTest {
//
//    @Autowired
//    TestEntityManager entityManager;
//
//    Role role;
//
//    @BeforeEach
//    void setUp(){
//        role = new Role("ROLE_OWNER", "OWNER", "This is admin");
//        entityManager.persist(role);
//    }
//
//
//    @Test
//    @DisplayName("Register Role Test")
//    void roleRegister(){
//        entityManager.persist(role);
//
//        Role findRole = entityManager.find(Role.class, role.getRoleId());
//        Assertions.assertNotNull(findRole);
//
//        Assertions.assertAll(
//                ()->Assertions.assertEquals("ROLE_OWNER", findRole.getRoleId()),
//                ()->Assertions.assertEquals("OWNER", findRole.getRoleName()),
//                ()->Assertions.assertEquals("This is admin", findRole.getRoleDescription())
//        );
//    }
//
//    @Test
//    @DisplayName("Role find Test")
//    void roleFind(){
//        Role findRole = entityManager.find(Role.class, role.getRoleId());
//        Assertions.assertNotNull(findRole);
//        Assertions.assertAll(
//                ()->Assertions.assertEquals("ROLE_OWNER", findRole.getRoleId()),
//                ()->Assertions.assertEquals("OWNER", findRole.getRoleName()),
//                ()->Assertions.assertEquals("This is admin", findRole.getRoleDescription())
//        );
//    }
//
//
//    @Test
//    @DisplayName("Role update Test")
//    void roleUpdate(){
//        Role updateRole = entityManager.find(Role.class, role.getRoleId());
//        updateRole.updateRole("role_change", "changed_roleDescription");
//
//        Role foundRole = entityManager.find(Role.class, role.getRoleId());
//        Assertions.assertAll(
//                ()->Assertions.assertEquals("ROLE_OWNER", foundRole.getRoleId()),
//                ()->Assertions.assertEquals("OWNER", foundRole.getRoleName()),
//                ()->Assertions.assertEquals("This is admin", foundRole.getRoleDescription())
//        );
//
//    }
//
//    @Test
//    @DisplayName("Delete Role Test")
//    void deleteRole(){
//        Role foundRole = entityManager.find(Role.class, role.getRoleId());
//        entityManager.remove(foundRole);
//        Assertions.assertNull(foundRole);
//    }
//}
