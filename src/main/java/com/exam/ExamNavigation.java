package com.exam;

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
import com.student.Student;
import com.util.Validation;

/**
 * Servlet implementation class ExamNavigation
 */
@WebServlet("/ExamNavigation")
public class ExamNavigation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
		Long currentTime = System.currentTimeMillis()/1000;
		
		
		if(session.getAttribute("ExamLoggedIn") != null && (boolean) session.getAttribute("ExamLoggedIn")) { 
			Integer examId = (Integer) session.getAttribute("examId");
			Integer attemptId = (Integer) session.getAttribute("attemptId");
			Integer studentId = (Integer) session.getAttribute("studentId");
			
			String fetchQuestionNavigationIdString = request.getParameter("fetchQuestionNavigationId");
			if(fetchQuestionNavigationIdString != null && Validation.onlyDigits(fetchQuestionNavigationIdString)) {
				try {
					Integer fetchQuestionNavigationId = Integer.parseInt(fetchQuestionNavigationIdString);
					JSONObject questionData = FetchQuestion.prepare(fetchQuestionNavigationId, attemptId, examId);
					json.put("data", questionData);
					success = "Question fetched successfully";
				} catch(Exception e) {
					error = "Something went wrong while fetching question";
					e.printStackTrace();
				}
			}
			
		}
		else {
			error = "You must log in to start exam";
		}

		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		out.println(json.toString());
	}


}
