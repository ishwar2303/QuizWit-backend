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

import com.admin.Admin;
import com.admin.Exam;
import com.admin.ManagementUser;
import com.admin.StudentGroup;
import com.config.Headers;
import com.config.Origin;
import com.student.Student;
import com.util.Validation;

/**
 * Servlet implementation class LoginExam
 */
@WebServlet("/LoginExam")
public class LoginExam extends HttpServlet {

	
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

		Integer examId = 0;
		Boolean examAccess = true;

		if(session.getAttribute("AdminLoggedIn") != null && (boolean) session.getAttribute("AdminLoggedIn")) {
			session.invalidate();
		}
		
		if(session.getAttribute("ExamLoggedIn") != null && (boolean) session.getAttribute("ExamLoggedIn")) { // user logged in
			JSONObject studentDetails = (JSONObject) session.getAttribute("details");
			json.put("details", studentDetails);
			success = "Logged in successfully";
		}
		else {
			if(email != null && password != null && examIdString != null) {
				
				if(examIdString.equals("")) {
					control = false;
		 			errorLog.put("examId", "Exam Id required");
				}
				else if(!Validation.onlyDigits(examIdString)) {
					control = false;
		 			errorLog.put("examId", "Invalid Exam Id");
				}
				else {
					examId = Integer.parseInt(examIdString);
					try {
						Boolean examExists = Exam.exists(examId);
						if(!examExists) {
							control = false;
				 			errorLog.put("examId", "Exam doesn't exists");
						}
						else {
							Boolean examActive = Exam.isActive(examId);
							if(!examActive) {
								control = false;
					 			errorLog.put("examId", "Exam is not active");
							}
						}
					} catch(Exception e) {
						e.printStackTrace();
						error = "Something went wrong while checking if exam exists or not";
					}
					
					try {
						Boolean examVisibility = Exam.visibilityPrivate(examId);
						if(examVisibility) { // check student in student group of exam
							if(!StudentGroup.exist(examId, email)) {
								control = false;
					 			errorLog.put("examId", "Exam is private, you are not authorized contact your administrator");
					 			examAccess = false;
							}
						} 
					} catch(Exception e) {
						e.printStackTrace();
						error = "Something went wrong while checking visibility of exam";
					}
				}
				
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
							details.put("examId", examId);
							session.setAttribute("studentId", id);
							session.setAttribute("ExamLoggedIn", true);
							session.setAttribute("examId", examId);
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
					if(examAccess)
						error = "Please fill required fields appropriately";
					else error = "Access not granted";
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
