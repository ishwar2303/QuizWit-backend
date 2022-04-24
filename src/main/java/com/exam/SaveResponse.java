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

import com.admin.Section;
import com.admin.ViewExams;
import com.config.Headers;
import com.config.Origin;
import com.questions.Question;
import com.student.Student;
import com.util.Validation;

/**
 * Servlet implementation class SaveResponse
 */
@WebServlet("/SaveResponse")
public class SaveResponse extends HttpServlet {
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
			
			String saveResponseQuestionNavigationIdString = request.getParameter("saveResponseQuestionNavigationId");
			if(saveResponseQuestionNavigationIdString != null && Validation.onlyDigits(saveResponseQuestionNavigationIdString)) {
				try {

					JSONObject exam = ViewExams.fetchExam(examId);
					json.put("examTitle", exam.get("title"));
					Long examEndTime = Long.parseLong((String) exam.get("endTime"))/1000;
					if(currentTime < examEndTime) {
						Integer saveResponseQuestionNavigationId = Integer.parseInt(saveResponseQuestionNavigationIdString);
						if(QuestionNavigation.validQuestionNavigationId(saveResponseQuestionNavigationId, attemptId)) {
							Integer questionId = QuestionNavigation.getQuestionId(saveResponseQuestionNavigationId, attemptId);
							JSONObject question = Question.fetch(questionId);
							Integer categoryId = Integer.parseInt((String) question.get("categoryId"));
							Integer sectionId = Integer.parseInt((String) question.get("sectionId"));

							// save response of current question
							SaveAnswer.save(request, attemptId, questionId, categoryId);
							if(request.getParameter("onlySave") == null && request.getParameter("clear") == null) {
								Boolean questionNavigation = Section.questionNavigation(sectionId);
								if(!questionNavigation) {
									QuestionNavigation.revokeAccess(saveResponseQuestionNavigationId, attemptId);
								}
								
								
								if(Question.setQuestionTimer(sectionId)) {
									QuestionNavigation.updateSubmittedTime(saveResponseQuestionNavigationId, System.currentTimeMillis()/1000);
								}

								Integer nextQuestionToFetchId = saveResponseQuestionNavigationId + 1;
								Integer nextQuestionId = QuestionNavigation.getQuestionId(nextQuestionToFetchId, attemptId);
								JSONObject nextQuestion = Question.fetch(nextQuestionId);
								Integer nextSectionId = Integer.parseInt((String) nextQuestion.get("sectionId"));
								Integer nextSectionNavigationId = SectionNavigation.getNavigationId(nextSectionId, attemptId);
								if(SectionNavigation.access(nextSectionNavigationId, attemptId)) { // section can be accessed
									if(QuestionNavigation.validQuestionNavigationId(nextQuestionToFetchId, attemptId)) {
										QuestionNavigation.grantAccess(nextQuestionToFetchId, attemptId);
									}
								}
							}
							
							success = "Question Navigation settings done";
						}
						else {
							error = "Invalid response question id";
						}
						
					}
					else {
						json.put("endExam", true);
					}
				} catch(Exception e) {
					error = "Something went wrong while saving response";
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
