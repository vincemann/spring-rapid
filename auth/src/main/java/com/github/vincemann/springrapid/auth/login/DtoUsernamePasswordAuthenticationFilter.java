package com.github.vincemann.springrapid.auth.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.auth.dto.LoginDto;
import org.springframework.lang.Nullable;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Expects credentials from body and not from url param, which is considered safer.
 */
public class DtoUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private ObjectMapper objectMapper;
    private static final String LOGIN_DTO_REQUEST_ATTRIBUTE = "LOGIN_DTO";

    public DtoUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nullable
    @Override
    protected String obtainPassword(HttpServletRequest request) {
        LoginDto dto = getLoginDtoFromRequest(request);
        return dto != null ? dto.getPassword() : null;
    }

    @Nullable
    @Override
    protected String obtainUsername(HttpServletRequest request) {
        LoginDto dto = getLoginDtoFromRequest(request);
        return dto != null ? dto.getContactInformation() : null;
    }

    private LoginDto getLoginDtoFromRequest(HttpServletRequest request) {
        // Check if the DTO has already been parsed and stored in the request
        LoginDto dto = (LoginDto) request.getAttribute(LOGIN_DTO_REQUEST_ATTRIBUTE);
        if (dto == null) {
            // If not, parse it from the request body and store it
            try {
                String body = readBody(request);
                if (!StringUtils.isEmpty(body)) {
                    dto = objectMapper.readValue(body, LoginDto.class);
                    request.setAttribute(LOGIN_DTO_REQUEST_ATTRIBUTE, dto);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return dto;
    }

    protected String readBody(HttpServletRequest request) {
        if (request.getAttribute(LOGIN_DTO_REQUEST_ATTRIBUTE) != null) {
            // Body has been read, no need to read again
            return "";
        }

        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            // Store the raw body in the request for potential debugging or logging
            request.setAttribute("RAW_BODY", body);
            return body;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read request body", e);
        }
    }
}
