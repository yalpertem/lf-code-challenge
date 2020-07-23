package com.labforward.api.core.validation;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

/*
 * Used for manually validating method parameter beans (JSR-303) in service layer
 */
@Configuration
public class ValidationConfig {

	@Bean
	@Autowired
	public EntityValidator preconditionValidator(MessageSource messageSource) {
		SpringValidatorAdapter springValidator = springValidatorAdapter(messageSource);

		return new EntityValidator(springValidator);
	}

	@Bean
	public SpringValidatorAdapter springValidatorAdapter(MessageSource messageSource) {
		Validator localValidator = localValidatorFactoryBean(messageSource);

		return new SpringValidatorAdapter(localValidator);
	}

	@Bean
	public Validator localValidatorFactoryBean(MessageSource messageSource) {
		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();

		// use the application message bundle for validation messages
		bean.setValidationMessageSource(messageSource);
		return bean;
	}

}
