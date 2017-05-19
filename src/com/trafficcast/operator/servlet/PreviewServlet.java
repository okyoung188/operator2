package com.trafficcast.operator.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class PreviewServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(PreviewServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String type = request.getParameter("type");
			String mainst = request.getParameter("mainst");
			String maindir = request.getParameter("maindir");
			String crossfrom = request.getParameter("crossfrom");
			String crossto = request.getParameter("crossto");
			
			this.getServletConfig().getServletContext().getRequestDispatcher(
					"/WEB-INF/jsp/preview.jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}
