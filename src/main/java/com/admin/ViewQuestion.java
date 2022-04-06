package com.admin;

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

import com.answers.MultipleChoiceAnswer;
import com.answers.TrueFalseAnswer;
import com.config.Headers;
import com.config.Origin;
import com.questions.MultipleChoiceQuestionOption;
import com.questions.Question;
import com.util.Validation;

/**
 * Servlet implementation class ViewQuestion
 */
@WebServlet("/ViewQuestion")
public class ViewQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		String success = "", error = "";
		JSONObject json = new JSONObject();
		JSONObject question = new JSONObject();
		if(adminId == null)
			return;
		
		String sectionIdString = request.getParameter("sectionId");
		String pageString = request.getParameter("page");
		Integer page = 1;
		Boolean control = true;
		if(sectionIdString != null && Validation.onlyDigits(sectionIdString)) {
			try {
				Integer sectionId = Integer.parseInt(sectionIdString);
				if(pageString != null) {
					page = Integer.parseInt(pageString);
				}
				page -= 1;
				Integer totalQuestions = Question.count(sectionId);
				json.put("totalQuestions", totalQuestions);
				if(page < 0 || page >= totalQuestions) {
					control = false;
					error = "Select correct page number";
				}
				
				if(control) {
					question = Question.fetch(sectionId, page);
					success = "Details fetched successfully";
					json.put("questionDetails", question);
					
					String categoryIdString = (String) question.get("categoryId");
					Integer categoryId = Integer.parseInt(categoryIdString);
					String questionIdString = (String) question.get("questionId");
					Integer questionId = Integer.parseInt(questionIdString);
					if(categoryId == 3) { // True False Question
						JSONObject answer = TrueFalseAnswer.fetch(questionId);
						question.put("answerDetails", answer);
					}
					else if(categoryId == 1 || categoryId == 2) {
						ArrayList<JSONObject> options = MultipleChoiceQuestionOption.fetch(questionId);
						question.put("mcqOptions", options);
						ArrayList<JSONObject> mcqAnswers = MultipleChoiceAnswer.fetch(questionId);
						question.put("mcqAnswers", mcqAnswers);
					}
					
				}
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong while fetching question details from database";
			}
		}

		json.put("success", success);
		json.put("error", error);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}

}
