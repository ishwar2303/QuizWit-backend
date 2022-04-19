package com.exam;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SaveAnswer
 */
@WebServlet("/SaveAnswer")
public class SaveAnswer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	

	public static void save(HttpServletRequest request, Integer attemptId, Integer questionId, Integer categoryId) {
		try {
			
			String clear = request.getParameter("clear");
			if(categoryId == 3) { // save respone of true or false question
				if(clear == null) {
					String trueFalseAnswer = request.getParameter("trueFalseAnswer");
					if(trueFalseAnswer != null) {
						System.out.println(trueFalseAnswer);
						if(trueFalseAnswer.matches("(TRUE|FALSE)")) {
							if(!StudentTrueFalseAnswers.exists(attemptId, questionId)) {
								StudentTrueFalseAnswers.add(attemptId, questionId, trueFalseAnswer);
							}
							else {
								StudentTrueFalseAnswers.update(attemptId, questionId, trueFalseAnswer);
							}
						}
					}
				}
				else {
					if(StudentTrueFalseAnswers.exists(attemptId, questionId))
						StudentTrueFalseAnswers.delete(attemptId, questionId);
				}
			}
			
			if(categoryId == 1) {
				if(clear == null) {
					
				}
				else {
					
				}
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
