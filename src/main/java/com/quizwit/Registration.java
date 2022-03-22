package com.quizwit;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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


@WebServlet("/Registration")
public class Registration extends HttpServlet {

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String user      = req.getParameter("user");
		String fullName = req.getParameter("fullName");
		String email     = req.getParameter("email");
		String contact   = req.getParameter("contact");
		String password = req.getParameter("password");
		String confirmPassword = req.getParameter("confirmPassword");
		String success = "";
		String error = "";
		JSONObject errorLog = new JSONObject();
		
		HttpSession session = req.getSession();
		Boolean verifiedEmail = (Boolean) session.getAttribute("verifiedEmail");
		String verifiedEmailDesc = (String) session.getAttribute("verifiedEmailDesc");
		
		Admin admin = new Admin();
		Student student = new Student();
		
		if(fullName != null && email != null && contact != null && password != null && confirmPassword != null) {
			Boolean control = true;
			
			if(user == null) {
				control = false;
				errorLog.put("user", "Select your role");
			}
			else if(!user.matches("[12]")) {
				control = false;
				errorLog.put("user", "Invalid role");
			}
			
			if(fullName == "") {
				control = false;
	 			errorLog.put("firstName", "Full Name required");
			}
			
			
			if(email == "") {
				control = false;
	 			errorLog.put("email", "E-mail required");
			}
			else if(!Validation.email(email)) {
				control = false;
	 			errorLog.put("email", "Invalid E-mail");
			}
//			else if(verifiedEmail != null) {
//				System.out.println(verifiedEmailDesc);
//				if(verifiedEmail && !verifiedEmailDesc.equals(email)) {
//					control = false;
//		 			errorLog.put("email", "E-mail changed verify again");
//				}
//				else email = verifiedEmailDesc;
//			}
//			else {
//				control = false;
//	 			errorLog.put("email", "E-mail not verified");
//			}

			try {
				Boolean result = false;
				if(user != null) {
					if(user.equals("1")) {
						result = admin.exists(email);
					}
					else {
						result = student.exists(email);
					}
					if(result) {
						control = false;
			 			errorLog.put("email", "Already registered with this E-mail");
					}
				}
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(contact != "" && !contact.matches("[0-9]{1,10}")) {
				control = false;
	 			errorLog.put("contact", "Invalid contactct");
			}
			
			
			if(password == "" ) {
				control = false;
	 			errorLog.put("password", "Password required");
			}
			else if(password.length() < 8) {
				control = false;
	 			errorLog.put("password", "Password must contain atleast 8 characters");
			}
			
			if(confirmPassword == "" ) {
				control = false;
	 			errorLog.put("confirmPassword", "Confirm Password required");
			}
			else if(!password.equals(confirmPassword)) {
				control = false;
	 			errorLog.put("confirmPassword", "Password not matched");
			}

			if(control) {
				Boolean result = false;
				try {
					if(user.equals("1"))
						result = admin.add(fullName, email, contact, password);
					else result = student.add(fullName, email, contact, password);
					
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(result) {
					session.removeAttribute("verifiedEmail");
					session.removeAttribute("verifiedEmailDesc");
					session.setAttribute("flashSuccess", "Registration Successfull");
					success = "Registration Successfull";
				}
				else error = "Something went wrong";
			}
			else {
				error = "Please fill required fields appropriately";
			}
			
		}
		else {
			error = "Please fill required fields appropriately";
		}
		JSONObject json = new JSONObject();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		Headers.setRequiredHeaders(res, Origin.getQuizWit());
		PrintWriter printWriter = res.getWriter();
		printWriter.println(json.toString());
		
	}

}
