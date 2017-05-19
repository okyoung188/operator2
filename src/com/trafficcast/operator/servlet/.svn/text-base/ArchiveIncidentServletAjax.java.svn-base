package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.dao.LinkDAO;
import com.trafficcast.operator.dao.ReportDAO;
import com.trafficcast.operator.pojo.Incident;
import com.trafficcast.operator.pojo.Report;
import com.trafficcast.operator.pojo.User;
import com.trafficcast.operator.utils.Utils;

public class ArchiveIncidentServletAjax extends BaseServlet {
    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	String id = request.getParameter("id") == null ? "" : request.getParameter("id").trim();
	HttpSession session = request.getSession(true);	
	String userRole = (String) session.getAttribute("userRole") == null ? "" :(String) session.getAttribute("userRole");
	String fromTab = request.getParameter("fromTab") == null ? "" : request.getParameter("fromTab").trim();
	if(fromTab.equals("")) {
	    fromTab = "-1";
	}
	
	User user = (User) session.getAttribute("user");
	
	if (userRole.equals("guest")) {
	    this.getServletConfig().getServletContext()
		.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp")
		.forward(request, response);
	    return;
	}	
	
	Incident inc = IncidentDAO.getIncidentByID(Long.parseLong(id));
	
	if (!userRole.equals("manager") && inc.getLocked() == 1) {
	    this.getServletConfig().getServletContext()
		.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp")
		.forward(request, response);
	    return;
	}
	
	IncidentDAO.archiveIncidentById(Long.parseLong(id));
	//insert report info
	Report report = new Report();	
	report.setUser_id(user.getId());
	report.setType(inc.getType());
	report.setIncident_id(inc.getId());
	report.setAction(4);
	report.setMarket(inc.getCity());
	report.setIp(Utils.getClientIpAddr(request));
	report.setLast_reader_id(inc.getReader_id());
	report.setLasttimestamp((byte) -1);
	report.setFromTab(Byte.parseByte(fromTab));
	
	int linkidlog = 0; 
	String linkdirlog = "";
	if (inc.getLink_id() != 0) {
		linkidlog = inc.getLink_id();
		linkdirlog = inc.getLink_dir();
	} else if (inc.getEnd_link_id() != 0) {
		linkidlog = inc.getEnd_link_id();
		linkdirlog = inc.getEnd_link_dir();
	}
	String country = inc.getCountry();
	if (inc.getCountry() == null || inc.getCountry().equals("")) {
	    country =  "USA";
	}
	report.setFunc_class((byte) LinkDAO.getFuncClassByLinkIDAndDir(linkidlog, linkdirlog, country));
	
	ReportDAO.addReport(report);

	PrintWriter out = response.getWriter();
	out.println("{\"success\":\"true\"}");
	out.flush();
	out.close();
    }

}
