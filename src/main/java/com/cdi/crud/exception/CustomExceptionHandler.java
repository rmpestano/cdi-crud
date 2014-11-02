package com.cdi.crud.exception;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import java.util.Iterator;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {


	private ExceptionHandler wrapped;

    public CustomExceptionHandler(ExceptionHandler exceptionHandler) {
        this.wrapped = exceptionHandler;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {
        Iterator<ExceptionQueuedEvent> unhandledExceptionQueuedEvents = getUnhandledExceptionQueuedEvents().iterator();

        if (unhandledExceptionQueuedEvents.hasNext()) {
            Throwable exception = unhandledExceptionQueuedEvents.next().getContext().getException();
            unhandledExceptionQueuedEvents.remove();

            Throwable rootCause = this.unwrap(exception);

            if (rootCause instanceof CustomException) {
                FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, rootCause.getMessage(), null));
            }
            FacesContext.getCurrentInstance().renderResponse();
            return;
        }
        wrapped.handle();
    }

    //from omnifaces https://code.google.com/p/omnifaces/source/browse/src/org/omnifaces/util/Exceptions.java
    private <T extends Throwable> Throwable unwrap(Throwable exception, Class<T> type) {
        while (type.isInstance(exception) && exception.getCause() != null) {
            exception = exception.getCause();
        }

        return exception;
    }

    private <T extends Throwable> Throwable unwrap(Throwable exception) {
        return unwrap(unwrap(exception, FacesException.class), ELException.class);
    }

}
