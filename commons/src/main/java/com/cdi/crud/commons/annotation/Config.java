package com.cdi.crud.commons.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import javax.interceptor.InterceptorBinding;

@Inherited
@Qualifier
@Retention(RUNTIME)
@Target({ FIELD, METHOD, TYPE })
public @interface Config {
  


}
