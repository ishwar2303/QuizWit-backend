package com.exam;

import org.json.simple.JSONObject;

public class LoadQuestionAfterStart {
	public static JSONObject prepare(Integer attemptId, Integer examId) {
		try {
			Integer navigationId = QuestionNavigation.getAccessibleNavigationId(attemptId);
			JSONObject questionData = FetchQuestion.prepare(navigationId, attemptId, examId);
			return questionData;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
