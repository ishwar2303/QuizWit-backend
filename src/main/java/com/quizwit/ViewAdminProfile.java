package com.quizwit;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.admin.CreateExam;
import com.admin.Exam;
import com.admin.Roles;
import com.admin.Admin;
import com.admin.UpdateExamDetails;
import com.config.Headers;
import com.config.Origin;
import com.questions.Question;
import com.util.Validation;

/**
 * Servlet implementation class ViewAdminProfile
 */
@WebServlet("/ViewAdminProfile")
public class ViewAdminProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));

		JSONObject json = new JSONObject();
		String success = "", error = "";
		JSONObject errorLog = new JSONObject();
		
		if(adminId == null)
			return;
		
		try {
			if(userId == 0) { // only admin
				Admin admin = new Admin();
				JSONObject adminDetails = admin.details(adminId);
				json.put("adminDetails", adminDetails);
				success = "Admin details fetched successfully";
			}
			else {
				error = "Access not granted";
			}
			
		}catch(Exception e) {
			error = "Something went wrong while fetching admin details";
			e.printStackTrace();
		}
		
		
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}
	
	

}
