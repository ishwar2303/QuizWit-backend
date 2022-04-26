package com.admin;

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
import com.util.Validation;

/**
 * Servlet implementation class FetchStudentGroup
 */
@WebServlet("/FetchStudentGroup")
public class FetchStudentGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		
		String success = "", error = "";
		JSONObject errorLog = new JSONObject();
		JSONObject json = new JSONObject();
		
		if(adminId == null)
			return;
		
		try {
			String examIdString = request.getParameter("examId");
			if(examIdString != null && Validation.onlyDigits(examIdString)) {
				Integer examId = Integer.parseInt(examIdString);
				
				if(CreateExam.examExists(adminId, examId)) {
					json.put("group", StudentGroup.fetch(examId));
					success = "Fetched successfully";
				}
				else {
					error = "Exam doesn't belongs to this account";
				}
			}
			else error = "Exam Id required";
				
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}

}
