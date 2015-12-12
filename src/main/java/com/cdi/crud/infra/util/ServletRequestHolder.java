package com.cdi.crud.infra.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Created by RAFAEL-PESTANO on 29/12/2014.
 *
 * just to enable HttpServletRequest injection
 * Note that DeltaSpike already provide this feature but it does not work in Jboss AS 7.1
 */
@RequestScoped
public class ServletRequestHolder implements Serializable{

  private HttpServletRequest currentRequest;

  public HttpServletRequest getCurrentRequest(){
    return currentRequest;
  }

  public void setCurrentRequest(HttpServletRequest currentRequest) {
    this.currentRequest = currentRequest;
  }
}
