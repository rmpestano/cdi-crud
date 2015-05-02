package com.cdi.crud.infra.filter;

import com.cdi.crud.infra.util.ServletRequestHolder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by RAFAEL-PESTANO on 29/12/2014.
 */
@WebFilter(filterName = "applicationFilter", urlPatterns = { "/*" })
@ApplicationScoped
public class ApplicationFilter implements Filter {

  @Inject
  ServletRequestHolder requestHolder;

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    req.setCharacterEncoding("UTF-8");
    resp.setCharacterEncoding("UTF-8");
    HttpServletResponse httpResp = (HttpServletResponse) resp;
    requestHolder.setCurrentRequest((HttpServletRequest) req);
    httpResp.setHeader("X-UA-Compatible", "IE=Edge");
    chain.doFilter(req, resp);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void destroy() {

  }

}
