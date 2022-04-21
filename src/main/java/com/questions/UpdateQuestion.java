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

import com.admin.Exam;
import com.admin.Roles;
import com.admin.ViewSections;
import com.answers.MultipleChoiceAnswer;
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

					String trueFalseAnswerString = request.getParameter("trueFalseAnswer");
					String[] answerString = request.getParameterValues("mcqOption[]");
					String mcqOptionAnswerString = request.getParameter("mcqOptionAnswer");
					HashMap<Integer, Boolean> mcqOptionAnswerSelected = new HashMap<Integer, Boolean>();

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

							if(answerString != null && mcqOptionAnswerString != null) {

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
							}
							else {
								control = false;
								errorLog.put("mcqOption", "Add options");
							}
							
							
						}
						else if(categoryId == 2) {

							if(answerString != null && mcqOptionAnswerString != null) {
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
									errorLog.put("mcqOptionAnswers", "Select correct answers");
									control = false;
								}
								else {
									String[] answers = mcqOptionAnswerString.split(",");
									if(answers.length == 0) {
										errorLog.put("mcqOptionAnswers", "Select correct answers");
										control = false;
									}
									else {
										Boolean answerSelected = false;
										for(int i=0; i<answers.length; i++) {
											try {
												Integer serial = Integer.parseInt(answers[i]);
												System.out.println(serial + " serial");
												if(serial >= 1 && serial <= answerString.length) {
													mcqOptionAnswerSelected.put(serial, true);
													answerSelected = true;
												}
												else {
													errorLog.put("mcqOptionAnswers", "Invalid answer serial no");
													control = false;
												}
											} catch(Exception e) {
												errorLog.put("mcqOptionAnswers", "Invalid answer serial no");
												control = false;
											}
										}
										if(!answerSelected) {
											errorLog.put("mcqOptionAnswers", "Select correct options");
											control = false;
										}
									}
								}
							}

							
						}
						else if(categoryId == 3) { // True or False
							if(trueFalseAnswerString != null) {
								if(!trueFalseAnswerString.matches("[01]")) {
									control = false;
									errorLog.put("trueFalseAnswer", "Invalid answer");
								}
								else {
									trueFalseAnswer = Integer.parseInt(trueFalseAnswerString);
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
						JSONObject questionJSON = Question.fetch(questionId);
						Integer sectionId = Integer.parseInt((String) questionJSON.get("sectionId"));
						JSONObject section = ViewSections.fetchSection(sectionId);
						Integer examId = Integer.parseInt((String) section.get("examId"));
						Exam.inActiveExam(examId);
						if(result) {
							if(categoryId == 1 || categoryId == 2) {
								Boolean oldOptionsDeleted = MultipleChoiceQuestionOption.deleteAllOptions(questionId);
								if(oldOptionsDeleted) {
									Boolean optionsAdded = true;
									for(int i=0; i<answerString.length; i++) {
										Integer optionId = MultipleChoiceQuestionOption.add(questionId, answerString[i]);
										if(optionId == 0) { // option not inserted
											optionsAdded = false;
											break;
										}
										if(optionId > 0 && mcqOptionAnswerSelected.get(i+1)) { // insert if this opiton is correct answer
											if(!MultipleChoiceAnswer.add(questionId, optionId)) {
												optionsAdded = false;
												break;
											}
										}
									}
									if(!optionsAdded) {
										error = "Something went wrong while adding new options";
									}
								}
								else {
									error = "Something went wrong while deleting old options";
								}
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
