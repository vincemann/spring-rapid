package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.RapidAuthAuthenticatedPrincipal;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.token.HttpTokenService;
import com.github.vincemann.springrapid.auth.service.token.JwtService;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter for token authentication
 */
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {



	private HttpTokenService httpTokenService;
    private AuthorizationTokenService<RapidAuthAuthenticatedPrincipal> authorizationTokenService;
    private RapidSecurityContext<RapidAuthAuthenticatedPrincipal> securityContext;
    private AuthProperties authProperties;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //todo realize this via security config api
        //https://www.baeldung.com/spring-exclude-filter
        if (request.getRequestURI().equals(authProperties.getController().getLoginUrl())){
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Inside TokenAuthenticationFilter ...");

        String rawToken = httpTokenService.extractToken(request);
//		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (rawToken != null) { // token present

            log.debug("Found a jwt");
            String token = rawToken;

            if (rawToken.startsWith(JwtService.TOKEN_PREFIX)) {
                 token = rawToken.substring(7);
            }

            try {
                RapidAuthAuthenticatedPrincipal principal = authorizationTokenService.parseToken(token);
                securityContext.login(principal);
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
            log.debug("No token found in request -> anon user");
            loginAnon();
        }
        filterChain.doFilter(request, response);
    }

    protected void loginAnon() {
        RapidAuthAuthenticatedPrincipal anon = new RapidAuthAuthenticatedPrincipal("anonymousUser",null, Sets.newHashSet(AuthRoles.ANON),null);
        securityContext.login(anon);
    }

//	////@LogInteraction(level = LogInteraction.Level.TRACE)
//	public Authentication createAuthToken(String token) {
//
//
////		LemonUserDto lemonUserDto = LemonValidationUtils.getUserDto(claims);
////		if (lemonUserDto == null)
////			lemonUserDto = fetchUserDto(claims);
////
////        LemonAuthenticatedPrincipal principal = new LemonAuthenticatedPrincipal(lemonUserDto);
//        return new UsernamePasswordAuthenticationToken(authenticatedPrincipal, token, authenticatedPrincipal.getAuthorities());
//	}

//	/**
//	 * Default behaviour is to throw error. To be overridden in auth service.
//	 *
//	 * @return
//	 */
//	protected LemonUserDto fetchUserDto(JWTClaimsSet claims) {
//		throw new AuthenticationCredentialsNotFoundException(
//				LexUtils.getMessage("com.github.vincemann.userClaimAbsent"));
//	}
}
