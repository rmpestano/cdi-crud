package com.cdi.crud.infra.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by RAFAEL-PESTANO on 29/12/2014.
 */
@WebFilter(filterName = "applicationFilter", urlPatterns = { "/*" })
public class ApplicationFilter implements Filter {

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    req.setCharacterEncoding("UTF-8");
    resp.setCharacterEncoding("UTF-8");
    HttpServletResponse httpResp = (HttpServletResponse) resp;
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
