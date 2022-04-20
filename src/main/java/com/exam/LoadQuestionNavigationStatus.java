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

@WebServlet("/LoadQuestionNavigationStatus")
public class LoadQuestionNavigationStatus extends HttpServlet {
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
				json.put("questions", QuestionNavigation.questionNavigationStatus(attemptId));
				success = "Question Navigation status fetched successfully";
				
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong while fetching question navigation status details";
			}
		}
		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		out.println(json.toString());
	}

}
