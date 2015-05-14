package com.cdi.crud.infra.security;

import com.cdi.crud.infra.exception.CustomException;
import org.apache.deltaspike.security.api.authorization.Secures;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by rmpestano on 12/20/14.
 */
@ApplicationScoped
@Named("authorizer")
public class CustomAuthorizer implements Serializable {

    Map<String, String> currentUser = new HashMap<>();

    @Secures
    @Admin
    public boolean doAdminCheck(InvocationContext invocationContext, BeanManager manager) throws Exception {
        boolean allowed = currentUser.containsKey("user") && currentUser.get("user").equals("admin");
        if(!allowed){
            throw new CustomException("Access denied");
        }
        return allowed;
    }

    @Secures
    @Guest
    public boolean doGuestCheck(InvocationContext invocationContext, BeanManager manager) throws Exception {
        boolean allowed = currentUser.containsKey("user") && currentUser.get("user").equals("guest") || doAdminCheck(null, null);
        if(!allowed){
            throw new CustomException("Access denied");
        }
        return allowed;
    }

    public void login(String username) {
        currentUser.put("user", username);
        if(FacesContext.getCurrentInstance() != null){
            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Logged in sucessfully as <b>"+username+"</b>"));
        }
        Logger.getLogger(getClass().getSimpleName()).info("Logged in with user: "+username);
    }

    public Map<String, String> getCurrentUser() {
        return currentUser;
    }
}
