package com.cdi.crud.exception;

import javax.ejb.ApplicationException;

/**
 * Created by rmpestano on 11/1/14.
 */
@ApplicationException(rollback = true)
public class CustomException extends RuntimeException{

    public CustomException(String msg) {
        super(msg);
    }
}
