package com.cdi.crud.infra.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Inherited
@Qualifier
@Retention(RUNTIME)
@Target({ FIELD, METHOD, TYPE })
public @interface Config {
  


}
