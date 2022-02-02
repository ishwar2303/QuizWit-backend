package com.config;

import javax.servlet.http.HttpServletResponse;

public class Headers {
	public static void setRequiredHeaders(HttpServletResponse response, String origin) {
		response.setHeader("Access-Control-Allow-Origin", origin);
		response.setHeader("Access-Control-Allow-Credentials", "true");
	}
}
