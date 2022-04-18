package com.exam;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.admin.Exam;
import com.admin.Section;
import com.admin.ViewExams;
import com.admin.ViewSections;
import com.config.Headers;
import com.config.Origin;
import com.questions.Question;
import com.student.Student;
import com.util.FisherYatesShuffle;

/**
 * Servlet implementation class EndExam
 */
@WebServlet("/EndExam")
public class EndExam extends HttpServlet {
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
			Integer attemptId = (Integer) session.getAttribute("attemptId");
			try {
				Boolean result = false;
				result = Attempt.endExam(attemptId);
			
				if(result) {
					SectionNavigation.revokeAccessFromAllSectionsOfExam(attemptId);
					QuestionNavigation.revokeAccessFromAllQuestionsOfExam(attemptId);
					Attempt.updateExamSubmitTime(attemptId, System.currentTimeMillis()/1000);
					success = "Exam submitted successfully";
					session.invalidate();
				}
				else {
					error = "Something went wrong while submitting exam";
				}
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong";
			}
		}
		else {
			error = "You must log in to end exam";
		}

		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		out.println(json.toString());
	}

}
