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

import com.config.Headers;
import com.config.Origin;

@WebServlet("/Logout")
public class Logout extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String user = request.getParameter("user");
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		
		if(user != null) {
			if(user != "" && user.matches("[12]")) {
				if(user.equals("1")) // Admin
					Headers.setRequiredHeaders(response, Origin.getAdmin());
				else Headers.setRequiredHeaders(response, Origin.getStudent()); // Student
				
				success = "Logout successfully";
				session.invalidate();
			}
			else error = "Invalid role";
		
		}
		else {
			error = "Role not set";
		}
		
		

		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		out.println(json.toString());
	}

}
