package com.example.receipt_backend.security.oauth;

import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.repository.UserRepository;
import com.example.receipt_backend.security.CustomUserDetails;
import com.example.receipt_backend.security.JwtUtils;
import com.example.receipt_backend.security.SecurityEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);

        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            // Update OAuth2 fields if necessary
            user.setRegisteredProviderName(SecurityEnums.AuthProviderId.google); // Example
            user.setRegisteredProviderId(oAuth2User.getName());
            userRepository.save(user);
        } else {
            // Optionally, auto-register the user or handle accordingly
            throw new ServletException("User not found with email: " + email);
        }

        CustomUserDetails userDetails = CustomUserDetails.buildFromUserEntity(user);
        String accessToken = jwtUtils.generateAccessToken(userDetails);

        // Return the token in response or redirect as needed
        response.setContentType("application/json");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\"}");
    }
}
