package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.trafficcast.operator.dao.*;
import com.trafficcast.operator.pojo.Note;

public class NoteInfoServletAjax extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String comment = request.getParameter("content") == null ? ""
				: request.getParameter("content");
		String incidentid = request.getParameter("id") == null ? ""
				: request.getParameter("id");
		
		String username = request.getParameter("username") == null ? ""
				: request.getParameter("username");
		
		String flag = request.getParameter("flag") == null ? ""
				: request.getParameter("flag");
		
		int userid = ReportDAO.getUserIDByName(username);
		
		int noteid = 0;
		
		if (comment != null && incidentid != null && userid != 0) {
			if (comment.trim().equals("")) {
				return;
			}
			Note note = new Note();
			note.setIncident_id(Long.parseLong(incidentid));
			note.setUser_id(userid);
			note.setUsername(username);
			java.util.regex.Pattern p = java.util.regex.Pattern.compile("\r\n");      
			java.util.regex.Matcher m = p.matcher(comment);
			if (m.find()) {
				String strNoBlank = m.replaceAll("\n");
				comment = strNoBlank;
			}
			note.setComment(comment);
			noteid = NoteDAO.addNote(note);
		}
		
		PrintWriter out = response.getWriter();
		if (noteid != 0 || flag.equals("true")) {
			List<Note> notes = NoteDAO.getNotesByIncidentID(incidentid);
			JSONArray jsonArray = JSONArray.fromObject(notes);
			out.println(jsonArray.toString());
		}
		
		out.flush();
		out.close();
	}
}
