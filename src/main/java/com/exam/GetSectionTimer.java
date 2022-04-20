package com.exam;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.config.Headers;
import com.config.Origin;
import com.student.Student;

/**
 * Servlet implementation class GetSectionTimer
 */
@WebServlet("/GetSectionTimer")
public class GetSectionTimer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getExam());
		String examIdString = request.getParameter("examId");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		
		Student student = new Student();
		
		Boolean control = true;
		JSONObject errorLog = new JSONObject();
		Long currentTime = System.currentTimeMillis()/1000;
		

		if(session.getAttribute("attemptId") != null) { 
			Integer studentId = (Integer) session.getAttribute("studentId");
			Integer examId = (Integer) session.getAttribute("examId"); 
			Integer attemptId = (Integer) session.getAttribute("attemptId");
			
			try {
				JSONObject sectionTimer = SectionNavigation.getTimer(attemptId);
				if((Boolean) sectionTimer.get("fetched")) {
					json.put("setSectionTimer", true);
					json.put("navigationId", Integer.parseInt((String) sectionTimer.get("navigationId")));
					Integer timeDuration = (int) (Long.parseLong((String) sectionTimer.get("endTime")) - System.currentTimeMillis()/1000);
					json.put("timeDuration", timeDuration);
				}
				else {
					json.put("setSectionTimer", false);
				}
				success = "success";
				
			} catch(Exception e) {
				error = "Something went wrong";
			}
			
		}
		else {
			error = "Invalid attempt";
		}

		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		out.println(json.toString());
	}
	
}
