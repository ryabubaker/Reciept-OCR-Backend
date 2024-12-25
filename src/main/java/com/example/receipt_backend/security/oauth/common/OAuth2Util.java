package com.example.receipt_backend.security.oauth.common;

import com.example.receipt_backend.security.SecurityEnums;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import java.util.Map;

public class OAuth2Util {

    public static CustomAbstractOAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                                 Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(SecurityEnums.AuthProviderId.google.toString())) {
            return new GoogleCustomAbstractOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(SecurityEnums.AuthProviderId.facebook.toString())) {
            return new FacebookCustomAbstractOAuth2UserInfo(attributes);
        } else {
            throw new InternalAuthenticationServiceException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
    

}
