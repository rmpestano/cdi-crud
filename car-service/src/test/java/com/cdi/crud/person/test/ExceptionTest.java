package com.cdi.crud.person.test;

import com.cdi.crud.car.bean.CarBean;
import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.apache.deltaspike.testcontrol.api.TestControl;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * Created by rmpestano on 11/1/14.
 */
@RunWith(CdiTestRunner.class)
@TestControl(startScopes = {RequestScoped.class,ViewAccessScoped.class})
public class ExceptionTest {

    @Inject
    CarBean carBean;

    @Test
    public void shouldAddFacesMessageOnCustomException(){
        try{
             carBean.findCarById(null);
        }finally {
            assertEquals(1, FacesContext.getCurrentInstance().getMessageList().size());
        }
    }
}
