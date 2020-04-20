package com.naturalprogrammer.spring.lemon.auth.util;

import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonUserDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class LecwUtils {

	private static final Log log = LogFactory.getLog(LecwUtils.class);

	public LecwUtils() {
		
		log.info("Created");
	}

	/**
	 * Fetches a cookie from the request
	 */
	public static Optional<Cookie> fetchCookie(HttpServletRequest request, String name) {
		
		Cookie[] cookies = request.getCookies();
	
		if (cookies != null && cookies.length > 0)
			for (int i = 0; i < cookies.length; i++)
				if (cookies[i].getName().equals(name))
					return Optional.of(cookies[i]);
		
		return Optional.empty();
	}

	/**
	 * Gets the current-user
	 */
	public static LemonUserDto currentUser() {
		
		return LecUtils.currentUser(SecurityContextHolder.getContext());
	}

}
