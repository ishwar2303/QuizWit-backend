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
 * Servlet implementation class SubmitSection
 */
@WebServlet("/SubmitSection")
public class SubmitSection extends HttpServlet {
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
					Integer saveResponseQuestionNavigationId = Integer.parseInt(saveResponseQuestionNavigationIdString);
					if(QuestionNavigation.validQuestionNavigationId(saveResponseQuestionNavigationId, attemptId)) {
						Integer questionId = QuestionNavigation.getQuestionId(saveResponseQuestionNavigationId, attemptId);
						JSONObject question = Question.fetch(questionId);
						Integer sectionId = Integer.parseInt((String) question.get("sectionId"));
						// save response
						Integer sectionNavigationId = SectionNavigation.getNavigationId(sectionId, attemptId);
						Boolean sectionNavigation = Exam.sectionNavigation(examId);
						if(!sectionNavigation) { // section navigation is false we need to revoke access of this section
							SectionNavigation.revokeAccess(sectionId, attemptId);
							SectionNavigation.updateSubmittedTime(sectionId, attemptId, System.currentTimeMillis()/1000);
							QuestionNavigation.revokeAccessFromAllQuestionsOfSection(sectionId, attemptId);
							Integer nextSectionToFetchId = sectionNavigationId + 1;
							System.out.println("Next section to fetch: " + nextSectionToFetchId);
							if(SectionNavigation.validSectionNavigationId(nextSectionToFetchId, attemptId) && !SectionNavigation.timerIsSet(nextSectionToFetchId)) {
								SectionNavigation.grantAccess(nextSectionToFetchId, attemptId);
								Integer nextSectionSectionId = SectionNavigation.getSectionId(nextSectionToFetchId, attemptId);
								if(Section.setSectionTimer(nextSectionSectionId)) {
									JSONObject section = ViewSections.fetchSection(nextSectionSectionId);
									Integer timeDuration = Integer.parseInt((String) section.get("timeDuration"));
									SectionNavigation.updateEndTime(nextSectionToFetchId, System.currentTimeMillis()/1000 + timeDuration);
								}

							}
							else {
								json.put("endExam", true);
							}
						}
						
						success = "Section navigation settings updated";
						// question Navigation true
					}
					else {
						error = "Invalid response question id";
					}
				} catch(Exception e) {
					error = "Something went wrong while setting section navigation info";
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
