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

import com.config.Headers;
import com.config.Origin;

/**
 * Servlet implementation class ExamDashboardCards
 */
@WebServlet("/ExamDashboardCards")
public class ExamDashboardCards extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getExam());

		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		if(session.getAttribute("attemptId") != null) {
			Integer attemptId = (Integer) session.getAttribute("attemptId");
			try {
				json.put("dashboardCardData", ExamDashboardCards.data(attemptId));
				success = "Dashboard card data fetched successfully";
				
			} catch(Exception e) {
				error = "Something went wrong while fetching card details";
			}
		}
		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		out.println(json.toString());
	}
	
	public static JSONObject data(Integer attemptId) throws ClassNotFoundException, SQLException {
		JSONObject dashboardCards = new JSONObject();
		try {
			dashboardCards.put("totalQuestions", QuestionNavigation.totalQuestionsCount(attemptId));
			dashboardCards.put("attemptedQuestions", QuestionNavigation.attemptedQuestionsCount(attemptId));
			dashboardCards.put("unattemptedQuestions", QuestionNavigation.unattemptedQuestionsCount(attemptId));
			dashboardCards.put("markedAsReviewQuestions", QuestionNavigation.makredAsReviewQuestionsCount(attemptId));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return dashboardCards;
	}

}
