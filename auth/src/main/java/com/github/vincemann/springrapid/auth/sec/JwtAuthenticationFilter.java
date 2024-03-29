package com.github.vincemann.springrapid.auth.sec;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.token.JwtService;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter for token authentication
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Log log = LogFactory.getLog(JwtAuthenticationFilter.class);


    private AuthorizationTokenService authorizationTokenService;
    private AuthProperties authProperties;

    public JwtAuthenticationFilter(AuthorizationTokenService authorizationTokenService, AuthProperties authProperties) {
        this.authorizationTokenService = authorizationTokenService;
        this.authProperties = authProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //todo realize this via security config api
        //https://www.baeldung.com/spring-exclude-filter
        if (request.getRequestURI().equals(authProperties.getLoginUrl())){
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Inside TokenAuthenticationFilter ...");

        String rawToken = request.getHeader(HttpHeaders.AUTHORIZATION);
//		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (rawToken != null) { // token present

            log.debug("Found a jwt");
            String token = rawToken;

            if (rawToken.startsWith(JwtService.TOKEN_PREFIX)) {
                 token = rawToken.substring(7);
            }

            try {
                RapidPrincipal principal = authorizationTokenService.parseToken(token);
                RapidSecurityContext.setAuthenticated(principal);
                log.debug("Token authentication successful");
                log.debug("Principal: " + principal + " logged in");

            } catch (Exception e) {

                log.debug("Token authentication failed - ",e);

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Authentication Failed: " + e.getMessage());

                return;
            }

        } else {
            // no token found -> let downstream filters log user in or block this request
            log.debug("No token found in request -> authenticate as anon user");
            RapidSecurityContext.setAnonAuthenticated();
        }
        filterChain.doFilter(request, response);
    }
}
