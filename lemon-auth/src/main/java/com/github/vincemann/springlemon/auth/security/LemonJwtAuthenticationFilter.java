package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springlemon.auth.service.token.HttpTokenService;
import com.github.vincemann.springlemon.auth.service.token.JwtService;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
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
public class LemonJwtAuthenticationFilter extends OncePerRequestFilter {



	private HttpTokenService httpTokenService;
    private AuthorizationTokenService<LemonAuthenticatedPrincipal> authorizationTokenService;
    private RapidSecurityContext<LemonAuthenticatedPrincipal> securityContext;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        log.debug("Inside LemonTokenAuthenticationFilter ...");

        String rawToken = httpTokenService.extractToken(request);
//		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (rawToken != null) { // token present

            log.debug("Found a jwt");
            String token = rawToken;

            if (rawToken.startsWith(JwtService.TOKEN_PREFIX)) {
                 token = rawToken.substring(7);
            }

            try {
                LemonAuthenticatedPrincipal principal = authorizationTokenService.parseToken(token);
                securityContext.login(principal);
                log.debug("Token authentication successful");
                log.debug("Principal: " + principal + " logged in");

            } catch (Exception e) {

                log.debug("Token authentication failed - " + e.getMessage());

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Authentication Failed: " + e.getMessage());

                return;
            }

        } else {
            //no token found -> let downstream filters log user in or block this request
            log.debug("No token found in request -> anon user");
            loginAnon();
        }
        filterChain.doFilter(request, response);
    }

    protected void loginAnon() {

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
//				LexUtils.getMessage("com.naturalprogrammer.spring.userClaimAbsent"));
//	}
}
