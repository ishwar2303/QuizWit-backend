package com.exam;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.questions.MultipleChoiceQuestionOption;
import com.util.Validation;

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
							QuestionNavigation.setAttempted(questionId, attemptId);
						}
					}
				}
				else {
					if(StudentTrueFalseAnswers.exists(attemptId, questionId)) {
						StudentTrueFalseAnswers.delete(attemptId, questionId);
						QuestionNavigation.setUnAttempted(questionId, attemptId);
					}
				}
			}

			if(categoryId == 1) {
				if(clear == null) {
					String option = request.getParameter("options");
					if(option != null && Validation.onlyDigits(option)) {
						Integer optionId = Integer.parseInt(option);
						if(MultipleChoiceQuestionOption.validOptionId(questionId, optionId)) {
							StudentMcqAnswers.delete(attemptId, questionId);
							StudentMcqAnswers.add(attemptId, questionId, optionId);
							QuestionNavigation.setAttempted(questionId, attemptId);
						}
					}
				}
			}
			if(categoryId == 2) {
				if(clear == null) {
					String optionsString = request.getParameter("options");
					if(optionsString != null) {
						String[] options = optionsString.split(",");
						Boolean deleteOptionControl = true;
						for(int i=0; i<options.length; i++) {
							if(Validation.onlyDigits(options[i])) {
								Integer optionId = Integer.parseInt(options[i]);
								if(MultipleChoiceQuestionOption.validOptionId(questionId, optionId)) {
									if(deleteOptionControl) {
										StudentMcqAnswers.delete(attemptId, questionId);
										deleteOptionControl = false;
									}
									StudentMcqAnswers.add(attemptId, questionId, optionId);
									QuestionNavigation.setAttempted(questionId, attemptId);
								}
							}
						}
						
					}
				}
			}
			
			if(categoryId == 1 || categoryId == 2) {
				if(clear != null) {
					StudentMcqAnswers.delete(attemptId, questionId);
				}
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
