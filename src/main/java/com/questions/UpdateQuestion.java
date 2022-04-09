package com.questions;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.admin.Roles;
import com.answers.TrueFalseAnswer;
import com.config.Headers;
import com.config.Origin;
import com.util.Validation;

/**
 * Servlet implementation class UpdateQuestion
 */
@WebServlet("/UpdateQuestion")
public class UpdateQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		String success = "", error = "";
		JSONObject errorLog = new JSONObject();
		JSONObject json = new JSONObject();
		
		if(adminId == null)
			return;
		
		String questionIdString = request.getParameter("questionId");
		try {
			Boolean access = Roles.authorized("UpdateQuestion", userId);
			if(userId == 0 || access) {
				Integer questionId = Integer.parseInt(questionIdString);
				Integer authorized = Question.authorized(questionId);
				if(authorized == adminId) {
					Integer categoryId = Question.categoryId(questionId);
					String categoryIdString = Integer.toString(categoryId);

					Boolean control = true;
					// Common question details validation

					String question = request.getParameter("question");
					String scoreString = request.getParameter("score");
					String negativeMarkingString = request.getParameter("negativeMarking");
					String explanation = request.getParameter("explanation");
					String timeDurationString = request.getParameter("timeDuration");
					
					Double score = 0.0;
					Double negative = 0.0;
					Integer timeDuration = 0;
					
					Integer trueFalseAnswer = 0;
					

					if(categoryIdString.matches("[123]")) {
						
						if(question != null && scoreString != null && negativeMarkingString != null && explanation != null && timeDurationString != null) {
							question = question.trim();
							explanation = explanation.trim();
							if(question.equals("")) {
								control = false;
								errorLog.put("question", "Question required");
							}

							if(Validation.isNumeric(scoreString)) {
								score = Double.parseDouble(scoreString);
								if(score <= 0) {
									control = false;
									errorLog.put("score", "Invalid score");
								}
							}
							else {
								control = false;
								errorLog.put("score", "Invalid score");
							}

							if(Validation.isNumeric(negativeMarkingString)) {
								negative = Double.parseDouble(negativeMarkingString);
								if(score <= 0) {
									control = false;
									errorLog.put("negative", "Invalid negative marking");
								}
							}
							else {
								control = false;
								errorLog.put("negative", "Invalid negative marking");
							}
							
							if(Validation.onlyDigits(timeDurationString)) {
								timeDuration = Integer.parseInt(timeDurationString);

								Integer sectionId = Question.getSectionId(questionId);
								if(!Question.setQuestionTimer(sectionId))
									timeDuration = 0;
							}
							else {
								control = false;
								errorLog.put("timeDuration", "Invalid time duration");
							}
							
						}

						if(categoryId == 1) {
							
						}
						else if(categoryId == 2) {
							
						}
						else if(categoryId == 3) { // True or False
							String answerString = request.getParameter("trueFalseAnswer");
							if(answerString != null) {
								if(answerString.matches("[01]")) {
									trueFalseAnswer = Integer.parseInt(answerString);
									Boolean suc =  TrueFalseAnswer.update(questionId, trueFalseAnswer);
									if(suc)
									{
										success = "Question Updated SuccessFully";
									}
								}
								else {
									control = false;
									errorLog.put("trueFalseAnswer", "Invalid answer");
								}
							}
							else {
								control = false;
								errorLog.put("trueFalseAnswer", "Select answer");
							}
						}
					}
					else {
						control = false;
						error = "Invalid question category";
					}
					
					if(control) {

						Boolean result = Question.update(questionId, question, score, negative, explanation, timeDuration);
						if(result) {
							if(categoryId == 1) {
								
							}
							else if(categoryId == 2) {
								
							}
							else if(categoryId == 3) {
								TrueFalseAnswer.update(questionId, trueFalseAnswer);
							}
							success = "Question updated successfully";
						}
						else {
							error = "Something went wrong while updating question";
						}
					}
					
				}
				else {
					error = "Question doesn't belongs to this account";
				}
			}
			else {
				error = "Access not granted";
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			error = "Something went wrong";
		}
		
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
		
	}

}
