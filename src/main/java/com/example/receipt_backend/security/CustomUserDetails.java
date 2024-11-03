package com.example.receipt_backend.security;
import com.example.receipt_backend.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
// TODO Refactor implementation to support OAuth2 and OIDC
public class CustomUserDetails implements OAuth2User, UserDetails {

    private String email;
    private String password;

    private User user;
    // refers to User -> Authorities, Usually defines roles (ROLE_USER, ROLE_ADMIN)
    private Collection<? extends GrantedAuthority> authorities;
    // permissions or combination of Scope:Permissions e.g. users:full, users:read, profile:full, profile:edit
    // private Map<String, String> permissions;
    // OAuth2 Provider attributes or custom Attributes
    private Map<String, Object> attributes;
    // =================================================

    public CustomUserDetails(String email,
                             String password,
                             User user,
                             Collection<? extends GrantedAuthority> authorities,
                             Map<String, Object> attributes) {
        this.email = email;
        this.password = password;
        this.user = user;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    public static CustomUserDetails buildFromUserEntity(User user) {

        Collection<? extends GrantedAuthority> grantedAuthorities = AppSecurityUtils
                .convertRolesSetToGrantedAuthorityList(user.getRoles());
        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                user,
                grantedAuthorities,
                new HashMap<>()
        );
    }


    public static CustomUserDetails buildWithAuthAttributesAndAuthorities(User user,
                                                                          Collection<? extends GrantedAuthority> authorities,
                                                                          Map<String, Object> attributes) {

        CustomUserDetails customUserDetails = CustomUserDetails.buildFromUserEntity(user);
        customUserDetails.setAuthorities(authorities);
        customUserDetails.setAttributes(attributes);
        return customUserDetails;
    }


    // UserDetails fields
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isEmailVerified();
    }

    // Oauth2User fields
    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return String.valueOf(this.getEmail());
    }
}