package com.cdi.crud.commons.util;

import com.cdi.crud.commons.annotation.Config;

import javax.enterprise.context.ApplicationScoped;
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
@ApplicationScoped
public class ServletRequestHolder implements Serializable{

  private HttpServletRequest currentRequest;

  @Produces
  @RequestScoped
  @Config
  public HttpServletRequest getCurrentRequest(){
    return currentRequest;
  }

  public void setCurrentRequest(HttpServletRequest currentRequest) {
    this.currentRequest = currentRequest;
  }
}
