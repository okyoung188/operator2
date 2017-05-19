package com.trafficcast.operator.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.trafficcast.operator.dao.UserDAO;
import com.trafficcast.operator.utils.Md5Util;

public class ModifyPassWordServletAjax extends BaseServlet {

    public void executeServlet(HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	String oldPassword = request.getParameter("oldPassword");
	String newPassword = request.getParameter("newPassword");
	String newPasswordClone = request.getParameter("newPasswordClone");

	String resultJson = null;
	HttpSession session = request.getSession();
	if (session != null && session.getAttribute("userName") != null) {
	    String name = (String) session.getAttribute("userName");
	    if (oldPassword != null && !oldPassword.equals("")
		    && newPassword != null && !oldPassword.equals("")
		    && newPasswordClone != null && !oldPassword.equals("")) {
		if (!newPasswordClone.equals(newPassword)) {
		    resultJson = "{\"success\":false,\"msg\":\"Please enter the same new password!\"}";
		} else {
		    if (oldPassword != null && newPassword != null) {
			oldPassword = Md5Util.getMD5(oldPassword);
			newPassword = Md5Util.getMD5(newPassword);
			boolean modifySucess = UserDAO.UpdatePassWord(name,
				oldPassword, newPassword);
			if (modifySucess) {
			    resultJson = "{\"success\":true,\"msg\":\"Modified Successfully\"}";
			} else {
			    resultJson = "{\"success\":false,\"msg\":\"The old password is wrong!\"}";
			}
		    }
		}
	    } else {
		resultJson = "{\"success\":false,\"msg\":\"Password can not be null!\"}";
	    }
	} else {
	    resultJson = "{\"success\":false,\"msg\":\"Password modified fialed!\"}";
	}
	response.getWriter().print(resultJson);
    }
}
