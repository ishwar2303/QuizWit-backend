package com.questions;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.admin.AddSection;
import com.admin.Exam;
import com.admin.Roles;
import com.admin.ViewSections;
import com.answers.MultipleChoiceAnswer;
import com.answers.TrueFalseAnswer;
import com.config.Headers;
import com.config.Origin;
import com.util.Validation;

/**
 * Servlet implementation class MultipleChoiceQuestionSingleCorrect
 */
@WebServlet("/MultipleChoiceQuestionSingleCorrect")
public class MultipleChoiceQuestionSingleCorrect extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		String success = "", error = "";
		JSONObject errorLog = new JSONObject();
		HashMap<Integer, Boolean> mcqOptionAnswerSelected = new HashMap<Integer, Boolean>();
		if(adminId == null)
			return;
		
		
		try {
			Boolean access = Roles.authorized("AddQuestion", userId);
			if(userId == 0 || access) {
				String sectionIdString = request.getParameter("sectionId");
				String categoryIdString = request.getParameter("categoryId");
				String questionString = request.getParameter("question");
				String[] answerString = request.getParameterValues("mcqOption[]");
				String mcqOptionAnswerString = request.getParameter("mcqOptionAnswer");
				String scoreString = request.getParameter("score");
				String negativeMarkingString = request.getParameter("negativeMarking");
				String explanationString = request.getParameter("explanation");
				String timeDurationString = request.getParameter("timeDuration");
				
				Integer sectionId = 0;
				Integer categoryId = 0;
				Double score = 0.0;
				Double negativeMarking = 0.0;
				Integer timeDuration = 0;
				
				Boolean control = true;
				if(sectionIdString != null && categoryIdString != null && questionString != null && answerString != null && mcqOptionAnswerString != null && scoreString != null && negativeMarkingString != null && timeDurationString != null) {
					if(Validation.onlyDigits(sectionIdString)) {
						sectionId = Integer.parseInt(sectionIdString);
						Integer adminIdTemp = AddSection.authorized(sectionId);
						if(adminId != adminIdTemp) {
							errorLog.put("sectionId", "Invalid Section");
							control = false;
						}
					}
					else {
						errorLog.put("sectionId", "Please select proper section");
						control = false;
					}
					
					if(Validation.onlyDigits(categoryIdString)) {
						categoryId = Integer.parseInt(categoryIdString);
						if(categoryId != 1) {
							errorLog.put("categoryId", "Select proper question category");
							control = false;
						}
					}
					else {
						errorLog.put("categoryId", "Select proper question category");
						control = false;
					}
					questionString = questionString.trim();
					if(questionString.equals("")) {
						errorLog.put("question", "Question required");
						control = false;
					}
					
					if(answerString.length >= 1) {
						ArrayList<String> optionsError = new ArrayList<String>();
						for(int i=0; i<answerString.length; i++) {
							mcqOptionAnswerSelected.put(i+1, false);
							answerString[i] = answerString[i].trim();
							String msg = "";
							if(answerString[i].equals("")) {
								msg = "Option cannot be empty";
								control = false;
							}
							optionsError.add(msg);
						}
						errorLog.put("optionsError", optionsError);
					}
					if(answerString.length < 2) {
						errorLog.put("mcqOption", "Atleast 2 mcq options are required");
						control = false;
					}
					
					if(mcqOptionAnswerString.equals("")) {
						errorLog.put("mcqOptionAnswers", "Select correct answer");
						control = false;
					}
					else if(Validation.onlyDigits(mcqOptionAnswerString)){
						try {
							Integer serial = Integer.parseInt(mcqOptionAnswerString);
							if(serial < 1 || serial > answerString.length) {
								errorLog.put("mcqOptionAnswers", "Invalid correct answer");
								control = false;
							}
							else {
								mcqOptionAnswerSelected.put(serial, true);
							}
						} catch(Exception e) {
							errorLog.put("mcqOptionAnswers", "Invalid correct answer");
							control = false;
						}
					}
					else {
						errorLog.put("mcqOptionAnswers", "Invalid correct answer");
						control = false;
					}
					
					if(Validation.onlyDigits(scoreString)) {
						score = Double.parseDouble(scoreString);
						if(score < 0) {
							errorLog.put("score", "Enter positive value");
							control = false;
						}
					}
					else {
						errorLog.put("score", "Invalid score");
						control = false;
					}
					
					if(Validation.onlyDigits(negativeMarkingString)) {
						negativeMarking = Double.parseDouble(negativeMarkingString);
						if(negativeMarking < 0) {
							errorLog.put("negativeMarking", "Enter positive value");
							control = false;
						}
					}
					else {
						errorLog.put("negativeMarking", "Invalid negative marking");
						control = false;
					}
					
					Boolean timeBoolean = Question.setQuestionTimer(sectionId);
					if(timeBoolean) {
						if(timeDurationString.length() != 0) {
							if(Validation.onlyDigits(timeDurationString)) {
								timeDuration = Integer.parseInt(timeDurationString);
							}
							else {
								errorLog.put("timeDuration", "Invalid time duration");
								control = false;
							}
							
						}
						else {
							errorLog.put("timeDuration", "Time duration required");
							control = false;
						}
					}
					else {
						timeDuration = 0;
					}
					System.out.println("oops");
					if(control) {
						System.out.println("Everything is fine");
						Integer questionId = Question.add(sectionId, categoryId, questionString, score, negativeMarking, explanationString, timeDuration);

						JSONObject section = ViewSections.fetchSection(sectionId);
						Integer examId = Integer.parseInt((String) section.get("examId"));
						Exam.inActiveExam(examId);
						if(questionId > 0) {
							// insert mcq options
							Boolean result = true;
							for(int i=0; i<answerString.length; i++) {
								Integer optionId = MultipleChoiceQuestionOption.add(questionId, answerString[i]);
								if(optionId == 0) { // option not inserted
									result = false;
									break;
								}
								if(optionId > 0 && mcqOptionAnswerSelected.get(i+1)) { // insert if this opiton is correct answer
									if(!MultipleChoiceAnswer.add(questionId, optionId)) {
										result = false;
										break;
									}
								}
							}
							if(result) {
								success = "Question added successfully";
							}
							else {
								Question.delete(questionId);
								error = "Something went wrong while adding question | Please try again";
							}
						}
						else {
							error = "Something went wrong";
						}
					}
					else {
						error = "Please fill required fields appropriately";
					}
					
					
				}
				else {
					error = "Please fill required fields appropriately";
				}
			}
			else {
				error = "Access not granted";
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			error = "Something went wrong";
		}

		JSONObject json = new JSONObject();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}

}
