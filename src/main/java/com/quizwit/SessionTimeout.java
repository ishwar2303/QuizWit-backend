package com.quizwit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

public class SessionTimeout {
	public static boolean check(HttpServletRequest request) {
		HttpSession session = request.getSession();
		JSONObject adminDetailsInSession = (JSONObject) session.getAttribute("details");
		Long currentTime = System.currentTimeMillis()/1000;
		Long loginTime = (Long) adminDetailsInSession.get("loginTime");
		if(currentTime - loginTime > 10) {
			session.invalidate();
			return true;
		}
		return false;
	}
}
