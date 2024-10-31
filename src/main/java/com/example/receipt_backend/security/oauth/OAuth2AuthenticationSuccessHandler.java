package com.example.receipt_backend.security.oauth;

import com.example.receipt_backend.config.AppProperties;
import com.example.receipt_backend.exception.BadRequestException;
import com.example.receipt_backend.security.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

/**
 * Handles successful OAuth2 authentication.
 * - Creates a JWT token and redirects to the specified URI with the token as a query parameter.
 * - Validates redirect URIs for security.
 */
@Service
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        // Clear the authentication attributes without overriding the method
        super.clearAuthenticationAttributes(request);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, Authentication authentication) {
        String redirectUri = request.getParameter("redirect_uri");
        String originalRequestUri = request.getParameter("original_request_uri");

        if (redirectUri != null && !isRedirectOriginAuthorized(redirectUri)) {
            throw new BadRequestException("Unauthorized Redirect URI, can't proceed with the authentication");
        }

        String targetUrl = Optional.ofNullable(redirectUri).orElse(getDefaultTargetUrl());
        String token = jwtTokenProvider.createJWTToken(authentication);

        authentication.getAuthorities().forEach(auth -> System.out.println("Granted Authority: " + auth.getAuthority()));

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .queryParam("original_request_uri", originalRequestUri)
                .build().toUriString();
    }

    private boolean isRedirectOriginAuthorized(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return Arrays.stream(appProperties.getOAuth2().getAuthorizedRedirectOrigins())
                .anyMatch(authorizedRedirectOrigin -> {
                    URI authorizedURI = URI.create(authorizedRedirectOrigin);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) &&
                            authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
