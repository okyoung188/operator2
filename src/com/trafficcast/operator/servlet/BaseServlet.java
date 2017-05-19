package com.trafficcast.operator.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	try {
	    executeServlet(request, response);
	} catch (Throwable e) {
	    response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
	    request.setAttribute("operatorException", e);
	    this.getServletConfig().getServletContext()
		    .getRequestDispatcher("/WEB-INF/jsp/error.jsp")
		    .forward(request, response);
	}
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	this.doGet(request, response);
    }
    
    protected abstract void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
