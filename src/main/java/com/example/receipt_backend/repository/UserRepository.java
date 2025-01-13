package com.example.receipt_backend.repository;

import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.security.SecurityEnums;
import com.example.receipt_backend.utils.RoleType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = "role", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "u.email = :email and u.registeredProviderName = :registeredProviderName " +
            "and u.verificationCodeExpiresAt >= CURRENT_TIMESTAMP  and u.verificationCode = :verificationCode")
    Optional<User> verifyAndRetrieveEmailVerificationRequestUser(@Param("email") String email,
                                                                 @Param("registeredProviderName") SecurityEnums.AuthProviderId registeredProviderName,
                                                                 @Param("verificationCode") String verificationCode);

    @Query("SELECT u FROM User u WHERE " +
            "u.email = :email and u.registeredProviderName = :validProviderName " +
            "and u.verificationCodeExpiresAt >= CURRENT_TIMESTAMP and u.verificationCode = :verificationCode")
    Optional<User> verifyAndRetrieveForgotPasswordRequestUser(@Param("email") String email,
                                                              @Param("validProviderName") SecurityEnums.AuthProviderId validProviderName,
                                                              @Param("verificationCode") String verificationCode);

    boolean existsByEmailAndTenant_TenantId(String email, UUID tenantId);



    Optional<User> findByUsername(String username);

    boolean existsByRole_Name(RoleType roleType);

    boolean existsByEmailAndTenant_TenantName(String adminEmail, String tenantName);

    Optional<User> findByUsernameAndTenant_TenantId(String username, UUID tenant_tenantId);
}
