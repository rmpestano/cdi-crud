package com.cdi.crud.exception;

import org.apache.deltaspike.core.api.exception.control.event.ExceptionToCatchEvent;
import org.apache.deltaspike.security.api.authorization.AccessDeniedException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/**
 * Created by rmpestano on 12/20/14.
 */
@ApplicationScoped
public class DeltaspikeExceptionObserver {

    public void catchException(@Observes ExceptionToCatchEvent ex){
        if(ex.getException() instanceof AccessDeniedException){
            throw new CustomException("Access denied");
        }
    }
}
