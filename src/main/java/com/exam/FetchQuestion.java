package com.exam;

import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.admin.Exam;
import com.admin.Section;
import com.questions.MultipleChoiceQuestionOption;
import com.questions.Question;

public class FetchQuestion {
	public static JSONObject prepare(Integer questionNavigationId, Integer attemptId, Integer examId) throws ClassNotFoundException, SQLException {
		JSONObject data = new JSONObject();
		if(QuestionNavigation.access(questionNavigationId, attemptId)) {
			Integer questionId = QuestionNavigation.getQuestionId(questionNavigationId, attemptId);
			JSONObject question = Question.fetch(questionId);
			Integer sectionId = Integer.parseInt((String) question.get("sectionId"));
			Integer categoryId = Integer.parseInt((String) question.get("categoryId"));
			Integer sectionNavigationId = SectionNavigation.getNavigationId(sectionId, attemptId);
			if(SectionNavigation.access(sectionNavigationId, attemptId)) {

				Integer questionTimer = -1;
			
				if(Question.setQuestionTimer(sectionId) && !QuestionNavigation.timerIsSet(questionNavigationId)) { // set timer on question
					Integer timeDuration = Integer.parseInt((String) question.get("timeDuration"));
					Long endTime = System.currentTimeMillis()/1000 + timeDuration;
					QuestionNavigation.updateEndTime(questionNavigationId, endTime);
					data.put("timeDuration", timeDuration);
					questionTimer = timeDuration;
				}
			
				if(Question.setQuestionTimer(sectionId) && QuestionNavigation.timerIsSet(questionNavigationId)) {
					questionTimer = (int) (QuestionNavigation.getEndTime(questionNavigationId) - System.currentTimeMillis()/1000);
					data.put("timeDuration", questionTimer);
				}
				
				Integer firstNavigationIdInSection = QuestionNavigation.firstQuestionOfSection(attemptId, sectionId);
				Integer serialNo = questionNavigationId - firstNavigationIdInSection + 1;
				question.put("serialNo", serialNo);
				question.put("navigationId", questionNavigationId);
				if((Question.setQuestionTimer(sectionId) && questionTimer > 0) || !Question.setQuestionTimer(sectionId)) {

					question.put("setQuestionTimer", Question.setQuestionTimer(sectionId));
					data.put("question", question);
					
		
					if(categoryId == 1 || categoryId == 2) { // MCQ
						ArrayList<JSONObject> mcqOptions = MultipleChoiceQuestionOption.fetch(questionId);
						data.put("mcqOptions", mcqOptions);
					}

				}
				else {
					JSONObject emptyQuestion = new JSONObject();
					emptyQuestion.put("setQuestionTimer", Question.setQuestionTimer(sectionId));
					data.put("question", emptyQuestion);
				}
				
				// configuration settings
				
				data.put("sectionNavigation", Exam.sectionNavigation(examId));
				
				Integer firstNavigationIdOfExam = QuestionNavigation.firstQuestionOfExam(attemptId);
				if(firstNavigationIdOfExam.toString().equals(questionNavigationId.toString())) {
					data.put("firstQuestion", true);
				}
				
				Integer lastNavigationId = QuestionNavigation.lastQuestionOfExam(attemptId);
				System.out.println("Last Navigation Id: " + lastNavigationId);
				System.out.println("fetch id: " + questionNavigationId);
				if(lastNavigationId.toString().equals(questionNavigationId.toString())) { 
					data.put("lastQuestion", true);
				}
				
				Integer lastNavigationIdInSection = QuestionNavigation.lastQuestionOfSection(attemptId, sectionId);
				if(lastNavigationIdInSection.toString().equals(questionNavigationId.toString())) {
					data.put("lastQuestionOfSection", true);
				}
				
				if(Section.questionNavigation(sectionId)) {
					data.put("questionNavigation", true);
				}
				else {
					data.put("questionNavigation", false);
				}
				
				Integer nextQuestionToFetch = QuestionNavigation.validQuestionNavigationId(questionNavigationId + 1, attemptId) ? questionNavigationId + 1 : 0;
				Integer previousQuestionToFetch = QuestionNavigation.validQuestionNavigationId(questionNavigationId - 1, attemptId) ? questionNavigationId - 1 : 0;
				
				data.put("nextQuestionToFetch", nextQuestionToFetch);
				data.put("previousQuestionToFetch", previousQuestionToFetch);
				
				data.put("success", "Question fetched successfully");
			}
			else {
				data.put("error", "Section can't be accessed");
			}
		}
		else {
			data.put("error", "Question can't be accessed");
		}
		
		return data;
	}
	
	
	
}
