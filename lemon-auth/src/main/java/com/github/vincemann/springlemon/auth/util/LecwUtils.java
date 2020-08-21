package com.github.vincemann.springlemon.auth.util;

import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public class LecwUtils {

	private static final Log log = LogFactory.getLog(LecwUtils.class);

	public LecwUtils() {
		log.info("Created");
	}

//	/**
//	 * Fetches a cookie from the request
//	 */
//	public static Optional<Cookie> fetchCookie(HttpServletRequest request, String name) {
//
//		Cookie[] cookies = request.getCookies();
//
//		if (cookies != null && cookies.length > 0)
//			for (int i = 0; i < cookies.length; i++)
//				if (cookies[i].getName().equals(name))
//					return Optional.of(cookies[i]);
//
//		return Optional.empty();
//	}


}
