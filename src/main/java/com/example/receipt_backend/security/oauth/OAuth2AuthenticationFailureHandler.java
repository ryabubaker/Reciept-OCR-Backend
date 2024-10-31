package com.example.receipt_backend.security.oauth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handles OAuth2 authentication failures.
 * - Redirects to a specified URI with an error message as a query parameter.
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {



    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String redirectUri = request.getParameter("redirect_uri");
        String targetUrl = (redirectUri != null) ? redirectUri : "/";

        // URL-encode the error message to avoid invalid characters
        String errorMessage = URLEncoder.encode("authentication_failed", StandardCharsets.UTF_8);
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", errorMessage)  // Use sanitized message
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
