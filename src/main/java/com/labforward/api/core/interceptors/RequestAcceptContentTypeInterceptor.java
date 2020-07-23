package com.labforward.api.core.interceptors;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.labforward.api.core.exception.UnsupportedMediaTypeException;

/**
 * Interceptor to validate that client's incoming requests meet guidelines for
 * acceptable MIME Types
 */
public class RequestAcceptContentTypeInterceptor extends HandlerInterceptorAdapter {

	private final static String REQUIRED_HEADER_ACCEPT = "Accept";

	private final static String WILDCARD_ACCEPT_HEADER = "*/*";

	private final static String CONTENT_TYPE_JSON = "application/json";

	private static boolean isValidAcceptHeaderProvided(String headerValue) {
		if (StringUtils.isEmpty(headerValue)) {
			return true;
		}

		// extract single content type values,
		// since header values can come chained up with additional requirement defined after colon, i.e.:
		// text/html,application/xml;q=0.9,image/webp,image/png,*/*;q=0.8,application/signed-exchange;v=b3
		Set<String> headerValues = Arrays.stream(headerValue.split(","))
		                                 .map(val -> val.split(";")[0])
		                                 .collect(Collectors.toSet());

		return (headerValues.contains(WILDCARD_ACCEPT_HEADER) || headerValues.contains(CONTENT_TYPE_JSON));
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		String contentType = request.getHeader(REQUIRED_HEADER_ACCEPT);

		if (!isValidAcceptHeaderProvided(contentType)) {
			throw new UnsupportedMediaTypeException();
		}

		return true;
	}
}
