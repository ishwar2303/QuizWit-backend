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

import com.admin.Exam;
import com.config.Headers;
import com.config.Origin;
import com.student.Student;
import com.util.Validation;

/**
 * Servlet implementation class LoginExam
 */
@WebServlet("/LoginStudent")
public class LoginStudent extends HttpServlet {

	
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();

		Headers.setRequiredHeaders(response, Origin.getStudent());
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		
		Student student = new Student();
		
		Boolean control = true;
		JSONObject errorLog = new JSONObject();

		

		if(session.getAttribute("AdminLoggedIn") != null && (boolean) session.getAttribute("AdminLoggedIn")) {
			session.invalidate();
		}
		
		if(session.getAttribute("ExamLoggedIn") != null && (boolean) session.getAttribute("ExamLoggedIn")) {
			session.invalidate();
		}
		
		
		if(session.getAttribute("StudentLoggedIn") != null && (boolean) session.getAttribute("StudentLoggedIn")) { // student logged in
			JSONObject studentDetails = (JSONObject) session.getAttribute("details");
			studentDetails.put("currentTime", System.currentTimeMillis()/1000);
			json.put("details", studentDetails);
			success = "Logged in successfully";
		}
		else {
			if(email != null && password != null) {
				
				if(email == "") {
					control = false;
		 			errorLog.put("email", "E-mail/Username required");
				}
				else {
					try {
						if(!student.exists(email)) {
							control = false;
							errorLog.put("email", "E-mail not registered");
						}
					}catch(Exception e) {
						e.printStackTrace();
						error = "Something went wrong while checking if student account exists or not";
					}
				}
				
				if(password == "") {
					control = false;
		 			errorLog.put("password", "Password required");
				}
				
				if(control) {
					Integer id = 0;
					try {
						id = student.login(email, password);
						if(id != 0) {
							JSONObject details = student.details(id);
							details.put("userType", "Student");
							session.setAttribute("studentId", id);
							session.setAttribute("StudentLoggedIn", true);
							session.setAttribute("details", details);
							details.put("loginTime", System.currentTimeMillis()/1000);
							details.put("currentTime", System.currentTimeMillis()/1000);
							session.setAttribute("details", details);
							json.put("details", details);
							success = "Logged in successfully";
						}
						else {
							error = "Invalid credentials or Your account is block";
						}
					} catch(Exception e) {
						e.printStackTrace();
						error = "Something went wrong while logging in student";
					}
				}
				else {
					error = "Please fill required fields appropriately";
				}
			}
			else {
				error = "Please fill required fields appropriately | check name attributes in request";
			}
			
		}
		
		
		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		out.println(json.toString());
	}

}
