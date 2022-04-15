package com.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.config.Headers;
import com.config.Origin;
import com.database.AdminDatabaseConnectivity;
import com.google.protobuf.Timestamp;
import com.util.Validation;

@WebServlet("/CreateExam")
public class CreateExam extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		
		String success = "", error = "";
		JSONObject errorLog = new JSONObject();
		
		if(adminId == null)
			return;
		
		try {
			Boolean access = Roles.authorized("CreateExam", userId);
			if(userId == 0 || access) {
				String title = request.getParameter("title");
				String description = request.getParameter("description");
				String difficultyLevel = request.getParameter("difficultyLevel"); // radio
				String visibility = request.getParameter("visibility"); // radio
				String sectionNavigation = request.getParameter("sectionNavigation"); // radio
				String startTimeString = request.getParameter("startTime");
				String endTime = request.getParameter("endTime");
				String windowTime = request.getParameter("windowTime");
				String numberOfAttempts = request.getParameter("numberOfAttempts");
				String timerType = request.getParameter("timerType"); // radio
				String timeDuration = request.getParameter("timeDuration"); // radio
				String instruction = request.getParameter("instructions");
				
				Integer numberOfAttemptsValue = 1;
				Integer timeDurationValue = 0;
				Integer timerTypeValue = 1;
				Integer windowTimeValue = 0;
				Integer examTimer = 0;
				Integer sectionTimer = 0;
				Integer sectionNavigationValue = 1;
				Integer visibilityValue = 0;
				Long 	startTime = (long) 0;
				String 	createdOn = Long.toString(System.currentTimeMillis());
				Boolean control = true;
				if(title != null && description != null && startTimeString != null && windowTime != null && numberOfAttempts != null && instruction != null) {
					if(title.length() == 0) {
						errorLog.put("title", "Title required");
						control = false;
					}
					
					if(description.length() == 0) {
						errorLog.put("description", "Description required");
						control = false;
					}
					
					if(difficultyLevel == null) {
						errorLog.put("difficultyLevel", "Choose difficulty level");
						control = false;
					}
					else if(!difficultyLevel.matches("[123]")){
						errorLog.put("difficultyLevel", "Invalid difficulty level");
						control = false;
					}
					else {
						if(difficultyLevel.equals("1")) 
							difficultyLevel = "Beginner";
						if(difficultyLevel.equals("2")) 
							difficultyLevel = "Intermediate";
						if(difficultyLevel.equals("3")) 
							difficultyLevel = "Advance";
						
					}
					
					if(visibility == null) {
						errorLog.put("visibility", "Choose visibility");
						control = false;
					}
					else if(!visibility.matches("[01]")) {
						errorLog.put("visibility", "Invalid visibility");
						control = false;
					}
					else {
						visibilityValue = Integer.parseInt(visibility);
					}
					
					if(sectionNavigation == null) {
						errorLog.put("sectionNavigation", "Select navigation setting");
						control = false;
					}
					else if(!sectionNavigation.matches("[01]")) {
						errorLog.put("sectionNavigation", "Invalid navigation type");
						control = false;
					}
					else {
						sectionNavigationValue  = Integer.parseInt(sectionNavigation);
					}
					
					if(!Validation.onlyDigits(windowTime)) {
						errorLog.put("windowTime", "Invalid window time");
						control = false;
					}
					
					if(startTimeString.equals("")) {
						errorLog.put("startTime", "Start time required");
						control = false;
					}
					else if(!Validation.onlyDigits(startTimeString)) {
						errorLog.put("startTime", "Invalid start time");
						control = false;
					}
					else {
						startTime = Long.parseLong(startTimeString)/1000;
						if(startTime < System.currentTimeMillis()/1000) {
							errorLog.put("startTime", "Invalid start time");
							control = false;
						}
					}
					
					if(!Validation.onlyDigits(windowTime)) {
						errorLog.put("windowTime", "Invalid window time");
						control = false;
					}
					else {
						windowTimeValue = Integer.parseInt(windowTime);
					}
					
					if(numberOfAttempts.equals("")) {
						errorLog.put("numberOfAttempts", "Number of attempts required");
						control = false;
					}
					if(!Validation.onlyDigits(numberOfAttempts)) {
						errorLog.put("numberOfAttempts", "Number greater than zero");
						control = false;
					}
					else {
						numberOfAttemptsValue = Integer.parseInt(numberOfAttempts);
						if(numberOfAttemptsValue <= 0) {
							errorLog.put("numberOfAttempts", "Number must be greater than zero");
						}
					}
					
					if(timerType != null) {
						if(!timerType.matches("[12]")) {
							errorLog.put("timerType", "Invalid timer type");
							control = false;
						}
						else {
							timerTypeValue = Integer.parseInt(timerType);
							if(timerTypeValue == 1) { // exam Timer
								examTimer = 1;
								if(timeDuration == null) {
									errorLog.put("timerDuration", "Select time duration");
									control = false;
								}
								else if(timeDuration.equals("")) {
									control = false;
									errorLog.put("timerDuration", "Time duration required");
								}
								else if(!Validation.onlyDigits(timeDuration)) {
									errorLog.put("timerDuration", "Invalid time duration");
									control = false;
								}
								else {
									timeDurationValue = Integer.parseInt(timeDuration);
									if(timeDurationValue == 0) {
										control = false;
										errorLog.put("timerDuration", "Invalid time duration");
									}
								}
							}
							if(timerTypeValue == 2) // section Timer
							{
								sectionTimer = 1;
								timeDurationValue = 0;
								examTimer = 0;
								sectionNavigationValue = 0; // off section navigation
							}
							
						}
					}
					else {
						errorLog.put("timerType", "Select timer type");
						control = false;
					}
					
					
					if(control) {
						System.out.println(startTime);
						System.out.println(endTime);
						System.out.println("sectionNavigation: " + sectionNavigation);
						Boolean result = false;
						try {
							result =  CreateExam.add(adminId, title, description, instruction, difficultyLevel, visibilityValue, sectionNavigationValue, startTimeString, windowTimeValue, numberOfAttemptsValue, examTimer, sectionTimer, timeDurationValue, createdOn);
						} catch(Exception e) {
							e.printStackTrace();
							error = "Something went wrong in database";
						}
						if(result) {
							success = "Exam created successfully";
						}
					}
				}
				else error = "Please fill required fields appropriately";
				
			}
			else {
				error = "Access not granted";
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		JSONObject json = new JSONObject();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
		
	}
	
	public static boolean add(Integer adminId, String title, String description, String instructions, String difficultyLevel, Integer visibility, Integer sectionNavigation, String startTime, Integer windowTimeValue, Integer numberOfAttempts, Integer examTimer, Integer sectionTimer , Integer timeDurationValue, String createdOn) throws ClassNotFoundException, SQLException {

		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Insert into Exams values (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		st.setString(2, title);
		st.setString(3, description);
		st.setString(4, difficultyLevel);
		st.setString(5, instructions);
		st.setInt(6, visibility);
		st.setInt(7, 0); // default hidden publish result
		st.setInt(8, examTimer);
		st.setInt(9, timeDurationValue);
		st.setInt(10, sectionTimer);
		st.setInt(11, sectionNavigation);
		st.setInt(12, 0); // default not deleted
		st.setString(13, startTime);
		st.setInt(14, 0); // default inactive
		st.setInt(15, windowTimeValue);
		st.setInt(16, numberOfAttempts);
		st.setString(17, createdOn);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
		
	}
	
	public static boolean examExists(Integer adminId, Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(examId) FROM `Exams` WHERE administratorId = ? AND examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		st.setInt(2, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer records = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return records > 0 ? true : false;
	}

}
