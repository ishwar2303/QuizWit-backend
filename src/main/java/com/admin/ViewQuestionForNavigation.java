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
 * Servlet implementation class ViewQuestionTitlesForNavigation
 */
@WebServlet("/ViewQuestionForNavigation")
public class ViewQuestionForNavigation extends HttpServlet {
	
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
		Boolean control = true;
		if(sectionIdString != null && Validation.onlyDigits(sectionIdString)) {
			try {
				Integer sectionId = Integer.parseInt(sectionIdString);
				
				if(control) {
					ArrayList<JSONObject> titles = Question.fetchTitle(sectionId);
					json.put("questionTitles", titles);
					success = "Details fetched successfully";
				}
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong while fetching question titles from database";
			}
		}

		json.put("success", success);
		json.put("error", error);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}


}
