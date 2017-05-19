package com.trafficcast.operator.servlet;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.dao.UserDAO;
import com.trafficcast.operator.pojo.Market;
import com.trafficcast.operator.utils.TOMIConfig;
import com.trafficcast.operator.utils.Utils;

public class IncidentsServlet extends BaseServlet { 

    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {	
	    List<String> readerIDList = IncidentDAO.getAllLiveReaderID();
	    List<String> users = UserDAO.getAllUsers();
	    List<Market> markets = IncidentDAO.getIncidentsDisplayedMarkets(TOMIConfig.getInstance().getTomiCountries());
	    
	    request.setAttribute("readerIDList", readerIDList);
	    request.setAttribute("users", users);
	    request.setAttribute("NTVersion", Utils.getNTMapVersion());
	    request.setAttribute("markets", markets);
	    request.setAttribute("countries", TOMIConfig.getInstance().getTomiCountries());
	    
	    this.getServletConfig().getServletContext()
		    .getRequestDispatcher("/WEB-INF/jsp/incidents.jsp")
		    .forward(request, response);
    }
   
}
