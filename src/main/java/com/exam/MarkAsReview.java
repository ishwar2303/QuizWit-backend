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

import com.admin.Exam;
import com.admin.Section;
import com.admin.ViewSections;
import com.config.Headers;
import com.config.Origin;
import com.questions.Question;
import com.student.Student;
import com.util.Validation;

/**
 * Servlet implementation class MarkAsReview
 */
@WebServlet("/MarkAsReview")
public class MarkAsReview extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
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
		Long currentTime = System.currentTimeMillis()/1000;
		
		
		if(session.getAttribute("ExamLoggedIn") != null && (boolean) session.getAttribute("ExamLoggedIn")) { 
			Integer examId = (Integer) session.getAttribute("examId");
			Integer attemptId = (Integer) session.getAttribute("attemptId");
			Integer studentId = (Integer) session.getAttribute("studentId");
			
			String fetchQuestionNavigationIdString = request.getParameter("questionNavigationId");
			String statusString = request.getParameter("status");
			if(fetchQuestionNavigationIdString != null && Validation.onlyDigits(fetchQuestionNavigationIdString)) {
				try {
					Integer fetchQuestionNavigationId = Integer.parseInt(fetchQuestionNavigationIdString);
					if(QuestionNavigation.validQuestionNavigationId(fetchQuestionNavigationId, attemptId)) {
						if(statusString.matches("[01]")) {
							Integer status = Integer.parseInt(statusString);
							QuestionNavigation.toggleMarkAsReview(fetchQuestionNavigationId, status);
							success = "Status changed successfully";
						}
						else {
							error = "Invalid Status";
						}
						
					}
					else {
						error = "Invalid NavigationId";
					}
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