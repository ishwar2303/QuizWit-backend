package com.exam;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

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
 * Servlet implementation class FetchExamTitle
 */
@WebServlet("/FetchExamTitle")
public class FetchExamTitle extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Headers.setRequiredHeaders(response, Origin.getExam());
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		
		String examIdString = request.getParameter("examId");
		if(examIdString != null && Validation.onlyDigits(examIdString)) {
			Integer examId = Integer.parseInt(examIdString);
			
			try {
				if(Exam.exists(examId)) {
					json.put("examTitle", Exam.fetchTitle(examId));
					success = "Fetched successfully";
				}
				else {
					error = "Exam doesn't exists";
				}
			} catch (ClassNotFoundException | SQLException e) {
				error = "Something went wrong";
				e.printStackTrace();
			}
		}
		
		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		out.println(json.toString());
	}


}
