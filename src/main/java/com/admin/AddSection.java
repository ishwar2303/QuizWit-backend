package com.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import com.util.Validation;

/**
 * Servlet implementation class AddSection
 */
@WebServlet("/AddSection")
public class AddSection extends HttpServlet {
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
			Boolean access = Roles.authorized("AddSection", userId);
			if(userId == 0 || access) {
				String examIdString = request.getParameter("examId");
				String title = request.getParameter("title");
				String description = request.getParameter("description");
				String questionNavigation = request.getParameter("questionNavigation"); // radio
				String timerType = request.getParameter("timerType"); // radio
				String timeDuration = request.getParameter("timeDuration"); // radio
				
				Integer examId = 0;
				Integer sectionTimer = 0;
				Integer questionTimer = 0;
				Integer questionNavigationValue = 1;
				Integer visibilityValue = 0;
				Integer timeDurationValue = 0;
				Integer timerTypeValue = 1;
				
				Boolean control = true;
				if(examId != null && title != null && description != null) {
					if(Validation.onlyDigits(examIdString)) {
						examId = Integer.parseInt(examIdString);
						try {
							boolean correctExamId = false;
							correctExamId = AddSection.examExists(adminId, examId);
							if(!correctExamId) {
								errorLog.put("examId", "Exam doesn't belongs to this account");
								control = false;
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							error = "Something went wrong in database";
						}
					}
					else {
						errorLog.put("examId", "Please select proper exam");
						control = false;
					}
					
					if(title.length() == 0) {
						errorLog.put("title", "Title required");
						control = false;
					}
					
					if(description.length() == 0) {
						errorLog.put("description", "Description required");
						control = false;
					}
					
					if(questionNavigation == null) {
						errorLog.put("questionNavigation", "Select navigation setting");
						control = false;
					}
					else if(!questionNavigation.matches("[01]")) {
						errorLog.put("questionNavigation", "Invalid navigation type");
						control = false;
					}
					else {
						questionNavigationValue  = Integer.parseInt(questionNavigation);
					}
					
					
					if(timerType != null) {
						if(!timerType.matches("[12]")) {
							errorLog.put("timerType", "Invalid timer type");
							control = false;
						}
						else {
							timerTypeValue = Integer.parseInt(timerType);
							if(timerTypeValue == 1) {
								sectionTimer = 1;
							}
							if(timerTypeValue == 2)
							{
								questionTimer = 1;
								timeDuration = "0";
							}
							if(timeDuration == null) {
								errorLog.put("timerDuration", "Select time duration");
								control = false;
							}
							else if(!Validation.onlyDigits(timeDuration)) {
								errorLog.put("timerDuration", "Invalid time duration");
								control = false;
							}
							else {
								timeDurationValue = Integer.parseInt(timeDuration);
							}
						}
					}
					else {
						errorLog.put("timerType", "Select timer type");
						control = false;
					}
					
					
					if(control) {
						Boolean result = false;
						try {
							result =  AddSection.add(examId, title, description, questionNavigationValue, sectionTimer, questionTimer, timeDurationValue);
						} catch(Exception e) {
							e.printStackTrace();
							error = "Something went wrong in database";
						}
						if(result) {
							success = "Section added successfully";
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
	
	public static boolean add(Integer examId, String title, String description, Integer questionNavigation, Integer sectionTimer , Integer questionTimer, Integer timeDurationValue) throws ClassNotFoundException, SQLException {

		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Insert into Sections values (NULL, ?, ?, ?, ?, ?, ?, ?, 0)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		st.setString(2, title);
		st.setString(3, description);
		st.setInt(4, sectionTimer);
		st.setInt(5, timeDurationValue);
		st.setInt(6, questionTimer);
		st.setInt(7, questionNavigation);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
		
	}

}
