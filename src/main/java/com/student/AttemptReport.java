package com.student;

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

import com.admin.ViewSections;
import com.config.Headers;
import com.config.Origin;
import com.evaluation.EvaluateMultipleChoiceAnswer;
import com.evaluation.EvaluateTrueFalseAnswer;
import com.exam.Attempt;
import com.exam.SectionNavigation;
import com.questions.Question;
import com.util.Validation;

/**
 * Servlet implementation class AttemptReport
 */
@WebServlet("/AttemptReport")
public class AttemptReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



		HttpSession session = request.getSession();

		Headers.setRequiredHeaders(response, Origin.getStudent());
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		
		Student student = new Student();
		
		Boolean control = true;

		if(session.getAttribute("StudentLoggedIn") != null && (boolean) session.getAttribute("StudentLoggedIn")) { // student logged in
			Integer studentId = (Integer) session.getAttribute("studentId");
			try {
				String attemptIdString = request.getParameter("attemptId");
				if(attemptIdString != null && Validation.onlyDigits(attemptIdString)) {
					Integer attemptId = Integer.parseInt(attemptIdString);
					if(Attempt.validAttemptOfStudent(attemptId, studentId)) {
						JSONObject result = AttemptReport.calculate(attemptId);
						if((Boolean) result.get("success")) {
							json.put("result", result);
							success = "Report fetched successfully";
						}
						else {
							error = "Something went wrong";
						}
					}
					else {
						error = "No such attempt found";
					}
				}
				else {
					error = "Invalid attempt Id";
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong";
			}
		}
		else {
			error = "Student not logged in";
		}
		
		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		out.println(json.toString());
	}

	public static JSONObject calculate(Integer attemptId) {
		JSONObject result = new JSONObject();
		Integer attemptedQuestions = 0;
		Integer correctQuestions = 0;
		Integer totalQuestions = 0;
		try {

			ArrayList<Integer> sectionsIds = SectionNavigation.getSectionIds(attemptId);
			ArrayList<JSONObject> sections = new ArrayList<JSONObject>();
			ArrayList<JSONObject> questions = new ArrayList<JSONObject>();
			Double totalExamScore = 0.0;
			Double examScore = 0.0;
			for(int i=0; i<sectionsIds.size(); i++) {
				Integer sectionId = sectionsIds.get(i);
				JSONObject section = ViewSections.fetchSection(sectionId);
				Double totalSectionScore = 0.0;
				Double sectionScore = 0.0;
				ArrayList<Integer> questionsIds = Question.getQuestionsIds(sectionId);
				for(int j=0; j<questionsIds.size(); j++) {
					totalQuestions += 1;
					Integer questionId = questionsIds.get(j);
					JSONObject question = Question.fetch(questionId);
					Integer categoryId = Integer.parseInt((String) question.get("categoryId"));
					Double score = Double.parseDouble((String) question.get("score"));
					totalSectionScore += score;
					Double negative = Double.parseDouble((String) question.get("negative"));
					JSONObject status = new JSONObject();
					
					if(categoryId == 1 || categoryId == 2) { // MCQ
						System.out.print("MCQ => ");
						ArrayList<Integer> selectedOptions = EvaluateMultipleChoiceAnswer.selectedOptions(questionId, attemptId);
						ArrayList<Integer> correctOptions = EvaluateMultipleChoiceAnswer.correctOptions(questionId);

						System.out.print("Selected Options: ");
						for(Integer v : selectedOptions) {
							System.out.print(v + ", ");
						}
						System.out.print(" ----- Correct Options: ");
						for(Integer v : correctOptions) {
							System.out.print(v + ", ");
						}
						if(selectedOptions.size() == 0) {
							 status.put("attempted", 0);
							 System.out.println(" -----> Not attempted");
						}
						else if(selectedOptions.size() != correctOptions.size()) {
							 status.put("attempted", 1);
							 status.put("correct", 0);
							 sectionScore -= negative;
							 attemptedQuestions += 1;
							 System.out.println(" -----> Wrong");
						}
						else if(selectedOptions.size() == correctOptions.size()){
							Integer count = 0;
							for(int k=0; k<selectedOptions.size(); k++) {
								if(selectedOptions.get(k).compareTo(correctOptions.get(k)) == 0)
									count += 1;
								else break;
							}
							System.out.print("COUNT = " + count + " ");
							if(count == correctOptions.size()) {
								 status.put("attempted", 1);
								 status.put("correct", 1);
								 correctQuestions += 1;
								 sectionScore += score;
								 attemptedQuestions += 1;
								 System.out.println(" -----> Correct");
							}
							else {
								 status.put("attempted", 1);
								 status.put("correct", 0);
								 sectionScore -= negative;
								 attemptedQuestions += 1;
								 System.out.println(" -----> Wrong");
							}
						}
					}
					else if(categoryId == 3) { // true false answer
						System.out.println();
						 String selectedTrueFalseAnswer = EvaluateTrueFalseAnswer.selectedAnswer(questionId, attemptId);
						 if(selectedTrueFalseAnswer.equals("")) {
							 status.put("attempted", 0);
						 }
						 else {
							 status.put("attempted", 1);
							 attemptedQuestions += 1;
						 }
						 if(selectedTrueFalseAnswer.equals(EvaluateTrueFalseAnswer.correctAnswer(questionId))) {
							 status.put("correct", 1);
							 correctQuestions += 1;
							 sectionScore += score;
						 }
						 else {
							 status.put("correct", 0);
							 sectionScore -= negative;
						 }
					}
					question.put("status", status);
					questions.add(question);
				}
				totalExamScore += totalSectionScore;
				examScore += sectionScore;
				section.put("scored", sectionScore);
				section.put("totalSectionScore", totalSectionScore);
				section.put("questions", questions);
				sections.add(section);
			}
			result.put("scored", examScore);
			result.put("totalExamScore", totalExamScore);
			result.put("totalQuestions", totalQuestions);
			result.put("attemptedQuestions", attemptedQuestions);
			result.put("incorrectQuestions", attemptedQuestions - correctQuestions);
			result.put("correctQuestions", correctQuestions);
			result.put("sections", sections);
			result.put("success", true);
			result.put("error", false);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("error", true);
			result.put("success", false);
		}
		return result;
	}

}
