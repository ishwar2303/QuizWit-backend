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
 * Servlet implementation class AddStudentInGroup
 */
@WebServlet("/AddStudentInGroup")
public class AddStudentInGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		
		String success = "", error = "";
		JSONObject errorLog = new JSONObject();
		
		if(adminId == null)
			return;
		
		try {
			Boolean access = Roles.authorized("AddStudentInExamGroup", userId);
			if(userId == 0 || access) {
				Boolean control = true;
				String examIdString = request.getParameter("examId");
				String email = request.getParameter("email");
				String confirmEmail = request.getParameter("confirmEmail");
				if(examIdString != null && Validation.onlyDigits(examIdString) && email != null && confirmEmail != null) {
					Integer examId = Integer.parseInt(examIdString);
					
					if(CreateExam.examExists(adminId, examId)) {
						
						if(!Validation.email(email)) {
							errorLog.put("email", "Invalid email");
							control = false;
						}
						else if(StudentGroup.exist(examId, confirmEmail)) {
							errorLog.put("email", "E-mail already exists");
							control = false;
						}
						
						if(!email.matches(confirmEmail)) {
							errorLog.put("confirmEmail", "E-mail doesn't match");
							control = false;
						}
						
						
						if(control) {
							Boolean result = false;
							try {
								result =  StudentGroup.add(examId, email);
							} catch(Exception e) {
								e.printStackTrace();
								error = "Something went wrong in database";
							}
							if(result) {
								success = "Student added in group successfully";
							}
						}
						else error = "Please fill required fields appropriately";
						
					}
					else {
						error = "Exam doesn't belongs to this account";
					}
				}
				else error = "Please fill required fields appropriately";
				
			}
			else {
				error = "Access not granted";
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		JSONObject json = new JSONObject();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}

}
