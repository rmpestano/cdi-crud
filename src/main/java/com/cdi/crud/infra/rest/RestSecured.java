package com.cdi.crud.infra.rest;

/**
 * Created by rmpestano on 12/20/14.
 */

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Inherited
@InterceptorBinding
public @interface RestSecured {

}
