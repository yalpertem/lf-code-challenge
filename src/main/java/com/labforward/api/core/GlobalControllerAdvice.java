package com.labforward.api.core;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.common.base.Throwables;
import com.labforward.api.core.creation.EntityCreatedResponse;
import com.labforward.api.core.deletion.NoContentResponse;
import com.labforward.api.core.domain.ApiMessage;
import com.labforward.api.core.domain.ValidationErrorMessage;
import com.labforward.api.core.exception.BadRequestException;
import com.labforward.api.core.exception.EntityValidationException;
import com.labforward.api.core.exception.ResourceNotAccessibleException;
import com.labforward.api.core.exception.ResourceNotFoundException;
import com.labforward.api.core.exception.ServiceUnavailableException;
import com.labforward.api.core.exception.UnsupportedMediaTypeException;
import com.labforward.api.core.exception.UserAgentRequiredException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

import static com.labforward.api.core.validation.BeanValidationUtils.OBJECT_ERROR_DELIMITER;

/**
 * Global exception handler
 * <p>
 * Useful for mapping all exception paths / error conditions to
 * the appropriate HTTP Status codes and user-friendly messages
 */
@ControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler implements ResponseBodyAdvice<Object> {

	public static final String MESSAGE_UNRECOGNIZED_PROPERTY = "Unrecognized property: ";

	private static final String MESSAGE_BAD_REQUEST = "Client error: server will not process request";

	private static final ApiMessage GENERIC_NOT_FOUND_MESSAGE = new ApiMessage("Entity not found.");

	public GlobalControllerAdvice() {
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(UserAgentRequiredException.class)
	@ResponseBody
	public ApiMessage handleNoUserAgent(UserAgentRequiredException ex) {
		return getApiErrorMessage(ex);
	}

	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ExceptionHandler(UnsupportedMediaTypeException.class)
	public ResponseEntity<ApiMessage> handleUnsupportedMediaType(UnsupportedMediaTypeException ex) {
		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
	}

	/*
	 * Returns not found codes on not found exception.
	 */
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseBody
	public ApiMessage handleNotFound(ResourceNotFoundException e) {
		return getApiErrorMessage(e);
	}

	/*
	 * Returns bad request when @RequestParameter required values are missing
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler(BadRequestException.class)
	public Object missingParameterHandler(BadRequestException e) {
		return getApiErrorMessage(e);
	}

	/*
	 * Handler for validation errors
	 * */
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	@ResponseBody
	@ExceptionHandler(EntityValidationException.class)
	public Object entityValidationException(EntityValidationException ex) {

		List<FieldError> fieldErrors = ex.getBindingResult()
		                                 .getFieldErrors();
		List<ObjectError> objectErrors = ex.getBindingResult()
		                                   .getGlobalErrors();

		ValidationErrorMessage message = new ValidationErrorMessage(ex.getMessage());

		for (FieldError fieldError : fieldErrors) {
			String[] info = fieldError.getDefaultMessage().split(OBJECT_ERROR_DELIMITER);
			if (info.length == 2) {
				message.addError(info[0], info[1]);
			} else {
				message.addError(fieldError.getField(), fieldError.getDefaultMessage());
			}
		}

		for (ObjectError objectError : objectErrors) {
			String[] info = objectError.getDefaultMessage().split(OBJECT_ERROR_DELIMITER);
			if (info.length == 2) {
				message.addError(info[0], info[1]);
			} else {
				message.addError(objectError.getObjectName(), objectError.getDefaultMessage());
			}
		}

		return message;
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	@ExceptionHandler(ResourceNotAccessibleException.class)
	public Object featureNotEnabledException(ResourceNotAccessibleException ex) {
		return new ApiMessage(ex.getMessage());
	}

	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	@ResponseBody
	@ExceptionHandler(ServiceUnavailableException.class)
	public Object serviceUnavailableException(ServiceUnavailableException ex) {
		return new ApiMessage(ex.getMessage());
	}

	/*
	 * Generic handler for unspecified errors.
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	@ExceptionHandler(Exception.class)
	public Object genericExceptionHandler(Exception e) {
		return getApiErrorMessage(e);
	}

	@Override
	public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
	                              Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
	                              ServerHttpResponse serverHttpResponse) {

		if (o instanceof NoContentResponse) {
			serverHttpResponse.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
			return o;
		}

		handleCacheHeaders(serverHttpResponse);
		final Object created = handleObjectCreated(o, (ServletServerHttpResponse) serverHttpResponse);

		return created;
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
	                                                    HttpStatus status, WebRequest request) {
		// return 404 when invalid UUID is provided in request
		if (ex.getRequiredType().equals(UUID.class)) {
			return new ResponseEntity<>(GENERIC_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
		}
		return super.handleTypeMismatch(ex, headers, status, request);
	}

	/*
	 * Override default Spring handling to return details of failed
	 * validation attempt on request body.
	 *
	 * How: the exception is generated when the incoming request body does not
	 * conform to the expected object
	 *
	 * Note: Spring defaults to 400, Bad Request, which is what we want, but we'd like
	 * to send a message as well
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
	                                                              HttpHeaders headers, HttpStatus status,
	                                                              WebRequest request) {
		StringBuilder sb = new StringBuilder();
		if (ex.getCause() instanceof UnrecognizedPropertyException) {
			UnrecognizedPropertyException e = (UnrecognizedPropertyException) ex.getCause();
			sb.append(MESSAGE_UNRECOGNIZED_PROPERTY);
			sb.append(e.getPropertyName());
		} else {
			sb.append(Throwables.getRootCause(ex).getMessage());
		}

		ApiMessage message = new ApiMessage(sb.toString());

		return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
	}

	/*
	 * Override default Spring handling to return details of failed
	 * validation attempt on request body.
	 *
	 * How: the exception is generated when validation fails on
	 * an @Valid @RequestBody input to controller.
	 *
	 * Note: Spring defaults to 400, Bad Request, which is what we want
	 *
	 * "The 400 (Bad Request) status code indicates that the server cannot or
	 * will not process the request due to something which is perceived to
	 * be a client error (e.g., malformed request syntax, invalid request
	 * message framing, or deceptive request routing)."
	 *
	 * https://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-26#section-6.5.1
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	                                                              HttpHeaders headers, HttpStatus status,
	                                                              WebRequest request) {
		List<FieldError> fieldErrors = ex.getBindingResult()
		                                 .getFieldErrors();
		List<ObjectError> globalErrors = ex.getBindingResult()
		                                   .getGlobalErrors();

		ValidationErrorMessage message = new ValidationErrorMessage(MESSAGE_BAD_REQUEST);

		for (FieldError fieldError : fieldErrors) {
			message.addError(fieldError.getField(), fieldError.getDefaultMessage());
		}
		for (ObjectError objectError : globalErrors) {
			message.addError(objectError.getObjectName(), objectError.getDefaultMessage());
		}

		return new ResponseEntity(message, headers, status);
	}

	private void handleCacheHeaders(ServerHttpResponse serverHttpResponse) {
		serverHttpResponse.getHeaders()
		                  .setCacheControl(CacheControl.noCache()
		                                               .getHeaderValue());
	}

	/*
	 * Global implementation for handling newly created entities
	 *
	 * Synchronous object creation:
	 * HTTP.CREATED (201) is returned along with 'Location' header of new object
	 *
	 * Asynchronous result creation:
	 * HTTP.ACCEPTED (202) is returned along with a 'Location' header to further track the status of the operation
	 */
	private Object handleObjectCreated(Object o, ServletServerHttpResponse serverHttpResponse) {

		if (o instanceof EntityCreatedResponse) {
			EntityCreatedResponse creationResponse = (EntityCreatedResponse) o;
			HttpServletResponse servletResponse = serverHttpResponse.getServletResponse();

			// Add location header for new object
			serverHttpResponse.getHeaders()
			                  .set(HttpHeaders.LOCATION, creationResponse.getLocation()
			                                                             .toString());

			servletResponse.setStatus(HttpStatus.CREATED.value());


			return creationResponse.getEntity();
		}

		return o;
	}

	private ApiMessage getApiErrorMessage(Exception ex) {
		return new ApiMessage(ex.getMessage());
	}
}
