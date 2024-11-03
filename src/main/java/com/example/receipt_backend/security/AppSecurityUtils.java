package com.example.receipt_backend.security;

import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.utils.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AppSecurityUtils {



    /**
     * Converts list of roles into Collection of GrantedAuthority
     *
     * @param roles
     * @return Collection<? extends GrantedAuthority>
     */
    public static Collection<? extends GrantedAuthority> convertRolesSetToGrantedAuthorityList(Set<RoleEntity> roles) {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        for (RoleEntity role : roles) {
            String roleName = role.getName().toString();

            // Check if the role is OAUTH2_USER and replace it with ROLE_USER
            if ("OAUTH2_USER".equals(roleName)) {
                authorities.add(new SimpleGrantedAuthority(RoleType.ROLE_USER.toString()));
            } else {
                authorities.add(new SimpleGrantedAuthority(roleName));
            }
        }
        return authorities;
    }


    /**
     * Converts Collection of GrantedAuthority into list of roles
     *
     * @param grantedAuthorities
     * @return Set<String>
     */
    public static Set<RoleEntity> convertGrantedAuthorityListToRolesSet(Collection<? extends GrantedAuthority> grantedAuthorities) {
        Set<RoleEntity> roles = new HashSet<>();
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {

            String authority = grantedAuthority.getAuthority();

            // Skip scope-based authorities
            if (authority.startsWith("SCOPE_")) {
                continue;
            }

            RoleEntity roleEntity = new RoleEntity();
            // Map OAUTH2_USER to ROLE_USER
            if ("OAUTH2_USER".equals(authority)) {
                roleEntity.setName(RoleType.ROLE_USER);
            } else {
                roleEntity.setName(RoleType.valueOf(authority));
            }
            roles.add(roleEntity);
        }
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
    public static Optional<Long> getCurrentUserId() {
        Optional<Long> optionalUserId = Optional.ofNullable(getCurrentUserPrinciple())
                .map(customUserDetails -> customUserDetails.getUser())
                .map(userEntity -> userEntity.getId());
        return optionalUserId;
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
                    .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(RoleType.ROLE_USER));
        }
        return false;
    }

}