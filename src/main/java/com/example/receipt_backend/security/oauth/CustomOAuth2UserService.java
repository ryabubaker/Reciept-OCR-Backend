package com.example.receipt_backend.security.oauth;


import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.mapper.UserMapper;
import com.example.receipt_backend.repository.RoleRepository;
import com.example.receipt_backend.security.CustomUserDetails;
import com.example.receipt_backend.security.SecurityEnums;
import com.example.receipt_backend.security.oauth.common.CustomAbstractOAuth2UserInfo;
import com.example.receipt_backend.security.oauth.common.OAuth2Util;
import com.example.receipt_backend.service.UserService;
import com.example.receipt_backend.utils.RoleType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest,
                                         OAuth2User oAuth2User) {
        String clientRegistrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        CustomAbstractOAuth2UserInfo customAbstractOAuth2UserInfo = OAuth2Util.getOAuth2UserInfo(clientRegistrationId, oAuth2User.getAttributes());

        // Check if the email is provided by the OAuthProvider
        SecurityEnums.AuthProviderId registeredProviderId = SecurityEnums.AuthProviderId.valueOf(clientRegistrationId);
        String userEmail = customAbstractOAuth2UserInfo.getEmail();
        if (!StringUtils.hasText(userEmail)) {
            throw new InternalAuthenticationServiceException("Sorry, Couldn't retrieve your email from Provider " + clientRegistrationId + ". Email not available or Private by default");
        }

        // Set tenantId for the current request context
        String tenantId = extractTenantId(oAuth2UserRequest); // Extract tenantId
        CurrentTenantIdentifierResolverImpl.setTenant(tenantId);

        // Determine is this [ Login ] or [ New Sign up ]
        // Sign In (email will be present in our database)  OR Sign Up ( if don't have user email, we need to register user, and save email into db)
        Optional<UserDTO> optionalUserByEmail = userService.findOptionalUserByEmail(userEmail);
        if (optionalUserByEmail.isEmpty()) {
            optionalUserByEmail = Optional.of(registerNewOAuthUser(oAuth2UserRequest, customAbstractOAuth2UserInfo));
        }
        UserDTO userDTO = optionalUserByEmail.get();
        if (userDTO.getRegisteredProviderName().equals(registeredProviderId)) {
            updateExistingOAuthUser(userDTO, customAbstractOAuth2UserInfo);
        } else {
            String incorrectProviderChoice = "Sorry, this email is linked with \"" + userDTO.getRegisteredProviderName() + "\" account. " +
                    "Please use your \"" + userDTO.getRegisteredProviderName() + "\" account to login.";
            throw new InternalAuthenticationServiceException(incorrectProviderChoice);
        }


        List<GrantedAuthority> grantedAuthorities = oAuth2User.getAuthorities().stream().collect(Collectors.toList());
        grantedAuthorities.add(new SimpleGrantedAuthority(getUserRoleBasedOnRequest().toString()));
        User user = userMapper.toEntity(userDTO);
        return CustomUserDetails.buildWithAuthAttributesAndAuthorities(user, grantedAuthorities, oAuth2User.getAttributes());
    }

    private UserDTO registerNewOAuthUser(OAuth2UserRequest oAuth2UserRequest,
                                         CustomAbstractOAuth2UserInfo customAbstractOAuth2UserInfo) {
        String tenantId = extractTenantId(oAuth2UserRequest);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(customAbstractOAuth2UserInfo.getName());
        userDTO.setEmail(customAbstractOAuth2UserInfo.getEmail());
        userDTO.setRegisteredProviderName(SecurityEnums.AuthProviderId.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        userDTO.setRegisteredProviderId(customAbstractOAuth2UserInfo.getId());
        userDTO.setRole(getUserRoleBasedOnRequest().getName().toString());
        userDTO.setEmailVerified(true);
        userDTO.setTenantId(tenantId);
        User user = userService.createUser(userDTO, tenantId, RoleType.ROLE_MOBILE_USER);
        return userMapper.toDto(user);
    }
    private String extractTenantId(OAuth2UserRequest oAuth2UserRequest) {
        // Extract tenantId from additional parameters or headers
        String tenantId = oAuth2UserRequest.getAdditionalParameters().get("tenantId").toString();
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalArgumentException("Missing tenantId in OAuth2 request");
        }
        return tenantId;
    }



    private void updateExistingOAuthUser(UserDTO existingUserDTO,
                                         CustomAbstractOAuth2UserInfo customAbstractOAuth2UserInfo) {
        existingUserDTO.setUsername(customAbstractOAuth2UserInfo.getName());
        existingUserDTO.setImageUrl(customAbstractOAuth2UserInfo.getImageUrl());
        UserDTO updatedUserDTO = userService.updateUser(existingUserDTO.getId(), existingUserDTO);
        BeanUtils.copyProperties(updatedUserDTO, existingUserDTO);
    }

    private RoleEntity getUserRoleBasedOnRequest() {
//        String userAgent = request.getHeader("User-Agent");
        RoleEntity roleEntity= new RoleEntity() ; // Placeholder for the actual role retrieval logic
//
//        if (userAgent != null && userAgent.toLowerCase().contains("mobile")) {
//            // Assign the mobile user role
//            roleEntity = roleRepository.findByName(RoleType.ROLE_MOBILE_USER)
//                    .orElseThrow(() -> new ResourceNotFoundException("ROLE_MOBILE_USER not found"));
//        } else {
//            // Assign the desktop user role
//            roleEntity = roleRepository.findByName(RoleType.ROLE_DESKTOP_USER)
//                    .orElseThrow(() -> new ResourceNotFoundException("ROLE_DESKTOP_USER not found"));
//        }
        return roleEntity;
    }

}
