package com.trafficcast.operator.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.trafficcast.operator.pojo.User;

/**
 * 
 * @author dell
 */
public class LoginFilter implements Filter {

    private String encoding;

    public void init(FilterConfig config) throws ServletException {
	encoding = config.getInitParameter("requestEncoding");
	if (encoding == null) {
	    encoding = "UTF-8";
	}
    }

    public void doFilter(ServletRequest request, ServletResponse response,
	    FilterChain next) throws IOException, ServletException {
	request.setCharacterEncoding(encoding);
	response.setContentType("text/html;charset=" + encoding);

	// check login
	HttpServletRequest req = (HttpServletRequest) request;
	HttpServletResponse res = (HttpServletResponse) response;
	HttpSession session = req.getSession(true);
	String username = null;
	User user = (User) session.getAttribute("user");
	if (user != null) {
	    username = user.getName();
	}
	String url = req.getRequestURI();
	
	if ((username == null || "".equals(username)) && !url.contains("login.action")) {
	    if (url.contains(".ajax")) {
		PrintWriter out = response.getWriter();
		out.println("{\"tcisessiontimeout\":\"true\"}");
		out.flush();
		out.close();
		return;
	    } else if (url.contains(".action")) {
		request.getRequestDispatcher("/login.action").forward(request, response);
		return;
	    }
	} else {
	    next.doFilter(request, response);
	}	
	// end check login
    }

    public void destroy() {
    }
}
