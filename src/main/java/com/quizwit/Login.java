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
import com.config.Headers;
import com.config.Origin;
import com.student.Student;
import com.util.Validation;


@WebServlet("/Login")
public class Login extends HttpServlet {
	
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		
		String user = request.getParameter("user");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		JSONObject json = new JSONObject();
		json.put("admin", "");
		String success = "";
		String error = "";
		
		Admin admin = new Admin();
		Student student = new Student();
		
		Boolean control = true;

		if(user != null) {
			if(user != "" && user.matches("[12]")) {
				if(user.equals("1")) // Admin
					Headers.setRequiredHeaders(response, Origin.getAdmin());
				else Headers.setRequiredHeaders(response, Origin.getStudent()); // Student
			}
			else {
				error = "Invalid Role";
			}
		}
		
		if(email != null && password != null && user != null) {
			
			JSONObject errorLog = new JSONObject();

			if(email == "") {
				control = false;
	 			errorLog.put("email", "E-mail required");
			}
			else if(!Validation.email(email)) {
				control = false;
	 			errorLog.put("email", "Invalid E-mail");
			}
			
			if(password == "") {
				control = false;
	 			errorLog.put("password", "Password required");
			}
			
			if(control) {
				Integer id = 0;
				if(user.equals("1")) { // Admin
					try {
						id = admin.login(email, password);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
				else { // Student
					try {
						id = student.login(email, password);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				if(id != 0) {
					JSONObject details = new JSONObject();
					if(user.equals("1")) { // Admin
						try {
							details = admin.details(id);
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
					else { // Student
						try {
							details = student.details(id);
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
					
					// set info in session
					session.setAttribute("loggedIn", true);
					session.setAttribute("details", details);
				}
				else {
					error = "Invalid credentials";
				}
			}
			else {
				error = "Please fill required fields appropriately";
			}
		}
		else {
			error = "Please fill required fields appropriately";
		}
		
		if(session.getAttribute("loggedIn") != null && (boolean) session.getAttribute("loggedIn")) { // user logged in
			json.put("details", (JSONObject) session.getAttribute("details"));
			success = "Logged in successfully";
		}
		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		out.println(json.toString());
	}
	
	

}
