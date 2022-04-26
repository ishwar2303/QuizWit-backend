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
import com.exam.Attempt;

/**
 * Servlet implementation class DashBoardCardData
 */
@WebServlet("/DashBoardCardData")
public class DashBoardCardData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		String userIdString = (String) session.getAttribute("userId");
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		JSONObject json = new JSONObject();
		String success = "", error = "";
		
		Integer totalExam = 0;
		Integer endedExam = 0;
		Integer activeUsers = 0;
		Integer noOfUsers = 0;
		Integer totalAttempts = 0;
		Integer noOfActiveExams = 0;
		Integer scheduledExam = 0;
		
		try {
			noOfUsers = ManagementUser.count(adminId);
			totalAttempts = Attempt.count(adminId);
			totalExam = Exam.totalExam(adminId);
			Integer currentTime = (int) (System.currentTimeMillis()/1000);
			endedExam = Exam.endedExam(adminId, currentTime);
			activeUsers = Admin.active(adminId);
			noOfActiveExams = Exam.runningExam(currentTime, adminId);
			scheduledExam = Exam.scheduledExam(currentTime, adminId);
			
			json.put("noOfUsers", noOfUsers);
			json.put("noOfActiveExams", noOfActiveExams);
			json.put("scheduledExam", scheduledExam);
			json.put("totalAttempts", totalAttempts);
			json.put("totalExam", totalExam);
			json.put("endedExam", endedExam);
			json.put("activeUsers", activeUsers);
			success = "Successfully fetched";
			
		} catch (Exception e) {
			e.printStackTrace();
			error = "Something went wrong while fetching details";
		}
		
		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		out.println(json.toString());
	}
	

}
