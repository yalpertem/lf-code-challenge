package com.labforward.api.core.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;

public class BeanValidationUtils {

	public static final String OBJECT_ERROR_DELIMITER = "#";

	public static boolean setErrorMessageAndReturnFalse(ConstraintValidatorContext constraintValidatorContext,
	                                                    String field, String message) {
		String sanitizedMessage = StringUtils.removePattern(message, "(\\{|\\}|\\[|\\])");
		StringBuilder errorMessage = new StringBuilder().append(field)
		                                                .append(OBJECT_ERROR_DELIMITER)
		                                                .append(sanitizedMessage);
		constraintValidatorContext.disableDefaultConstraintViolation();
		constraintValidatorContext.buildConstraintViolationWithTemplate(errorMessage.toString())
		                          .addConstraintViolation();
		return false;
	}

	public static void addValidationError(ConstraintValidatorContext constraintValidatorContext,
	                                      String field, String message) {
		constraintValidatorContext.disableDefaultConstraintViolation();
		constraintValidatorContext.buildConstraintViolationWithTemplate(message)
		                          .addNode(field)
		                          .addConstraintViolation();
	}

	// This class exists to provide "bean" validation on request bodies that are of type List<T>.
	public static class ValidList<T> implements List<T> {

		@Valid
		private List<T> list = new ArrayList<>();

		public List<T> getList() {
			return list;
		}

		public void setList(List<T> list) {
			if (list == null) {
				list = new ArrayList<>();
			}

			this.list = list;
		}

		@Override
		public int size() {
			return list.size();
		}

		@Override
		public boolean isEmpty() {
			return list.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return list.contains(o);
		}

		@Override
		public Iterator<T> iterator() {
			return list.iterator();
		}

		@Override
		public Object[] toArray() {
			return list.toArray();
		}

		@Override
		public <T1> T1[] toArray(T1[] t1s) {
			return list.toArray(t1s);
		}

		@Override
		public boolean add(T t) {
			return list.add(t);
		}

		@Override
		public boolean remove(Object o) {
			return list.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> collection) {
			return list.containsAll(collection);
		}

		@Override
		public boolean addAll(Collection<? extends T> collection) {
			return list.addAll(collection);
		}

		@Override
		public boolean addAll(int i, Collection<? extends T> collection) {
			return list.addAll(i, collection);
		}

		@Override
		public boolean removeAll(Collection<?> collection) {
			return list.removeAll(collection);
		}

		@Override
		public boolean retainAll(Collection<?> collection) {
			return list.retainAll(collection);
		}

		@Override
		public void clear() {
			list.clear();
		}

		@Override
		public T get(int i) {
			return list.get(i);
		}

		@Override
		public T set(int i, T t) {
			return list.set(i, t);
		}

		@Override
		public void add(int i, T t) {
			list.add(i, t);
		}

		@Override
		public T remove(int i) {
			return list.remove(i);
		}

		@Override
		public int indexOf(Object o) {
			return list.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return list.lastIndexOf(o);
		}

		@Override
		public ListIterator<T> listIterator() {
			return list.listIterator();
		}

		@Override
		public ListIterator<T> listIterator(int i) {
			return list.listIterator(i);
		}

		@Override
		public List<T> subList(int i, int i1) {
			return list.subList(i, i1);
		}
	}
}
