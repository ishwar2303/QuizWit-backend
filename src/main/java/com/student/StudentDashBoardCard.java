package com.student;

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
import com.exam.Attempt;
import com.util.Validation;

/**
 * Servlet implementation class StudentDashBoardCard
 */
@WebServlet("/StudentDashBoardCard")
public class StudentDashBoardCard extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		Headers.setRequiredHeaders(response, Origin.getStudent());
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		
		Student student = new Student();
		
		Boolean control = true;
		
		Integer totalExams = 0;
		Integer totalAttempts = 0;
		Integer scheduledExams = 0;
		Integer endedExams = 0;

		if(session.getAttribute("StudentLoggedIn") != null && (boolean) session.getAttribute("StudentLoggedIn")) { // student logged in
			Integer studentId = (Integer) session.getAttribute("studentId");
			try {
				
				String email = Student.getEmail(studentId);
				
				Integer currentTime = (int) (System.currentTimeMillis()/1000);
				
				totalAttempts = Student.attempts(studentId);
				
				scheduledExams = Student.scheduledExams(email, currentTime);
				
				endedExams = Student.endedExams(email, currentTime);
				System.out.println("working.....");
				totalExams = scheduledExams + endedExams;
				
				json.put("totalExams", totalExams);
				json.put("totalAttempts", totalAttempts);
				json.put("scheduledExams", scheduledExams);
				json.put("endedExams", endedExams);
				success = "Successfully fetched";
					
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong";
			}
		}
		else {
			error = "Student not logged in";
		}
		
		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		out.println(json.toString());
	}

}
