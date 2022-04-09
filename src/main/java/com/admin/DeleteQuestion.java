package com.admin;

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
import com.questions.Question;
import com.util.Validation;

/**
 * Servlet implementation class DeleteQuestion
 */
@WebServlet("/DeleteQuestion")
public class DeleteQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		String success = "", error = "";
		
		JSONObject json = new JSONObject();
		
		if(adminId == null)
			return;
		
		String questionIdString = request.getParameter("questionId");
		if(questionIdString != null && Validation.onlyDigits(questionIdString)) {
			try {
				Boolean access = Roles.authorized("DeleteQuestion", userId);
				if(userId == 0 || access) {
					Integer questionId = Integer.parseInt(questionIdString);
					Integer requestedAdminId = Question.authorized(questionId);
					if(requestedAdminId == adminId) {
						boolean result = false;
						result = Question.delete(questionId);
						if(result)
							success = "Question deleted successfully";
						else error = "Something went wrong in database while deleting question";
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
		} 
		else {
			error = "Question Id required";
		}
		
		json.put("success", success);
		json.put("error", error);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}
}
