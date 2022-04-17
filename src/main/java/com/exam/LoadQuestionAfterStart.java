package com.exam;

import org.json.simple.JSONObject;

public class LoadQuestionAfterStart {
	public static JSONObject prepare(Integer attemptId) {
		try {
			Integer navigationId = QuestionNavigation.getAccessibleNavigationId(attemptId);
			JSONObject questionData = FetchQuestion.prepare(navigationId, attemptId);
			return questionData;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
