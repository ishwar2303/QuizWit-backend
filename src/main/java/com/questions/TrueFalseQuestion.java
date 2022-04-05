package com.questions;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.admin.AddSection;
import com.admin.Roles;
import com.config.Headers;
import com.config.Origin;
import com.util.Validation;

/**
 * Servlet implementation class TrueFalseQuestion
 */
@WebServlet("/TrueFalseQuestion")
public class TrueFalseQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		String success = "", error = "";
		JSONObject errorLog = new JSONObject();
		
		if(adminId == null)
			return;
		
		
		try {
			Boolean access = Roles.authorized("AddQuestion", userId);
			if(userId == 0 || access) {
				String sectionIdString = request.getParameter("sectionId").trim();
				String categoryIdString = request.getParameter("categoryId").trim();
				String questionString = request.getParameter("question").trim();
				String answerString = request.getParameter("trueFalseAnswer").trim();
				String scoreString = request.getParameter("score").trim();
				String negativeMarkingString = request.getParameter("negativeMarking").trim();
				String explanationString = request.getParameter("explanation").trim();
				String timeDurationString = request.getParameter("timeDuration").trim();
				
				Integer sectionId = 0;
				Integer categoryId = 0;
				Double score = 0.0;
				Double negativeMarking = 0.0;
				Integer timeDuration = 0;
				System.out.println(sectionIdString);
				System.out.println(questionString);
				System.out.println(answerString);
				System.out.println(scoreString);
				System.out.println(negativeMarkingString);
				System.out.println(explanationString);
				System.out.println(timeDurationString);
				System.out.println(categoryIdString);
				
//				Boolean control = true;
//				if(sectionIdString != null && categoryIdString != null && questionString != null && answerString != null && scoreString != null && negativeMarkingString != null) {
//					if(Validation.onlyDigits(sectionIdString)) {
//						sectionId = Integer.parseInt(sectionIdString);
//						Integer adminIdTemp = AddSection.authorized(sectionId);
//						if(adminId != adminIdTemp) {
//							control = false;
//							error = "Invalid Section";
//						}
//					}
//					else {
//						errorLog.put("sectionId", "Please select proper section");
//						control = false;
//					}
//					
//					if(Validation.onlyDigits(categoryIdString)) {
//						categoryId = Integer.parseInt(categoryIdString);
//					}
//					else {
//						errorLog.put("categoryId", "Please select proper question category");
//						control = false;
//					}
//					
//					if(questionString.length() == 0) {
//						errorLog.put("question", "question required");
//						control = false;
//					}
//					
//					if(answerString.length() == 0) {
//						errorLog.put("answer", "answer required");
//						control = false;
//					}
//					
//					if(Validation.onlyDigits(scoreString)) {
//						score = Double.parseDouble(scoreString);
//						if(score < 0) {
//							errorLog.put("score", "please enter positive value");
//							control = false;
//						}
//					}
//					else {
//						errorLog.put("score", "invalid score");
//						control = false;
//					}
//					
//					if(Validation.onlyDigits(negativeMarkingString)) {
//						negativeMarking = Double.parseDouble(negativeMarkingString);
//						if(negativeMarking < 0) {
//							errorLog.put("negativemarking", "please enter positive value");
//							control = false;
//						}
//					}
//					else {
//						errorLog.put("negativemarking", "invalid input");
//						control = false;
//					}
//					
//					Boolean timeBoolean = Question.setQuestionTimer(sectionId);
//					if(timeBoolean) {
//						if(timeDurationString != null) {
//							if(Validation.onlyDigits(timeDurationString)) {
//								timeDuration = Integer.parseInt(timeDurationString);
//								
//							}
//							else {
//								errorLog.put("timeDuration", "please enter valid input");
//								control = false;
//							}
//							
//						}
//						else {
//							errorLog.put("Time", "please choose time duration");
//							control = false;
//						}
//					}
//					
//					if(control) {
//						Boolean result = Question.add(sectionId, categoryId, questionString, score, negativeMarking, explanationString, timeDuration);
//					}
//					
					
				//}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
