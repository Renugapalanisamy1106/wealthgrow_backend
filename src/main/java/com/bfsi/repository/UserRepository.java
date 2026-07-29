package com.bfsi.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bfsi.entity.User;
import com.bfsi.entity.Role;

@Repository
public interface UserRepository
        extends JpaRepository<User, String> {

    /* =====================
       FIND USER (EMAIL)
       ===================== */
    User findByEmail(String email);


    /* =====================
       LOGIN VALIDATION
       ===================== */
    User findByEmailAndPasswordHash(String email, String password);


    /* =====================
       ROLE FETCH
       ===================== */
    @Query("""
        SELECT r FROM Role r
        WHERE r.roleId = (
            SELECT u.roleId FROM User u WHERE u.userId = :userId
        )
    """)
    Role findRoleByUserId(
            @Param("userId") String userId
    );

    /* =====================
       FIND USER IDS BY ROLE  (for role-targeted notifications)
       ===================== */
    @Query("""
        SELECT u.userId FROM User u WHERE u.roleId = :roleId
    """)
    java.util.List<String> findUserIdsByRole(@Param("roleId") String roleId);

    /* =====================
       COUNT USERS
       ===================== */
    @Query("""
        SELECT COUNT(u)
        FROM User u
    """)
    int countActiveUsers();
}