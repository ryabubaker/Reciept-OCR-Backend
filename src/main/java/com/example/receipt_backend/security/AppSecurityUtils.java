package com.example.receipt_backend.security;

import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.entity.common.AbstractGenericPrimaryKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

public class AppSecurityUtils {

    public static final String ROLE_DEFAULT = "ROLE_DEFAULT";
    

    public AppSecurityUtils() {
    }

    /**
     * Converts list of roles into Collection of GrantedAuthority
     *
     * @param role
     * @return Collection<? extends GrantedAuthority>
     */
    public static List<GrantedAuthority> convertRolesSetToGrantedAuthorityList(RoleEntity role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getName().toString()));
    }

    /**
     * Converts Collection of GrantedAuthority into list of roles
     *
     * @param grantedAuthorities
     * @return Set<String>
     */
    public static Set<String> convertGrantedAuthorityListToRolesSet(Collection<? extends GrantedAuthority> grantedAuthorities) {
        Set<String> roles = AuthorityUtils.authorityListToSet(grantedAuthorities);
        return roles;
    }

    /**
     * Get Authentication object from SecurityContextHolder
     *
     * @return Authentication object
     */
    public static Authentication getAuthenticationObject() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Get current user principle
     *
     * @return CustomUserDetails - principle object
     */
    public static CustomUserDetails getCurrentUserPrinciple() {
        Authentication authentication = getAuthenticationObject();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal);
            }
        }
        return null;
    }

    /**
     * Get current user id
     *
     * @return Long - user id
     */
    public static Optional<UUID> getCurrentUserId() {
        return Optional.ofNullable(getCurrentUserPrinciple())
                .map(CustomUserDetails::getUser)
                .map(AbstractGenericPrimaryKey::getId);
    }


    /**
     * Check if user is Authenticated
     *
     * @return true - if user is Authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthenticationObject();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(AppSecurityUtils.ROLE_DEFAULT));
        }
        return false;
    }




}