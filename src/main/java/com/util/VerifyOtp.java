package com.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.config.Headers;
import com.config.Origin;

@WebServlet("/VerifyOtp")
public class VerifyOtp extends HttpServlet {
	
	private static final long serialVersionUID = -8401615954530139645L;

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		String type = req.getParameter("type");
		System.out.println(type);
		String otp = (String)req.getParameter("otp");
		if(otp == "" || type.equals(""))
			error = "OTP required";
		HttpSession session = req.getSession();

		String orgOtp = (String) session.getAttribute(type);
		
		System.out.println("Entered otp  = " + otp);
		System.out.println("Original otp = " + orgOtp);
		
		if(orgOtp != null && orgOtp.equals(otp)) {
			success = "Verified";
			session.setAttribute("verifiedEmail", true);
		}
		else error = "Incorrect OTP";
		Headers.setRequiredHeaders(res, Origin.getQuizWit());
		PrintWriter printWriter = res.getWriter();
		json.put("success", success);
		json.put("error", error);
		printWriter.println(json.toString());
	}
	
}