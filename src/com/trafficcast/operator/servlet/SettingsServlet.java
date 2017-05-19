package com.trafficcast.operator.servlet;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.pojo.Market;
import com.trafficcast.operator.utils.TOMIConfig;

public class SettingsServlet extends BaseServlet { 

    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {	
	    List<Market> markets = IncidentDAO.getIncidentsDisplayedMarkets(TOMIConfig.getInstance().getTomiCountries());
	    
	    request.setAttribute("markets", markets);
	  	    
	    this.getServletConfig().getServletContext()
		    .getRequestDispatcher("/WEB-INF/jsp/settings.jsp")
		    .forward(request, response);
    }
   
}
