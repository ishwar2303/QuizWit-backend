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
import com.questions.Question;
import com.util.Validation;

/**
 * Servlet implementation class UpdateSectionDetails
 */
@WebServlet("/UpdateSectionDetails")
public class UpdateSectionDetails extends HttpServlet {
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
				String sectionIdString = request.getParameter("sectionId");
				String title = request.getParameter("title");
				String description = request.getParameter("description");
				String questionNavigation = request.getParameter("questionNavigation"); // radio
				String timerType = request.getParameter("timerType"); // radio
				String timeDuration = request.getParameter("timeDuration"); // radio
				String shuffleQuestionsString = request.getParameter("shuffleQuestions");
				System.out.println(shuffleQuestionsString+" "+sectionIdString);
				Integer sectionId = 0;
				Integer examId = 0;
				Integer sectionTimer = 0;
				Integer questionTimer = 0;
				Integer questionNavigationValue = 1;
				Integer visibilityValue = 0;
				Integer timeDurationValue = 0;
				Integer timerTypeValue = 1;
				Integer shuffleQuestions = 0;
				
				Boolean control = true;
				if(sectionIdString != null && title != null && description != null) {
					if(Validation.onlyDigits(sectionIdString)) {
						sectionId = Integer.parseInt(sectionIdString);
						try {
							boolean correctSectionId = false;
							examId =  AddSection.getExamId(sectionId);
							correctSectionId = CreateExam.examExists(adminId, examId);
							if(!correctSectionId) {
								errorLog.put("sectionId", "Section doesn't belongs to this account");
								control = false;
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							error = "Something went wrong in database";
						}
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
					
					

					Boolean setSectionTimer = Exam.setSectionTimer(examId);
					if(setSectionTimer) {
						if(timerType != null) {
							if(!timerType.matches("[12]")) {
								errorLog.put("timerType", "Invalid timer type");
								control = false;
							}
							else {
								timerTypeValue = Integer.parseInt(timerType);
								
								
								if(timerTypeValue == 1) { // set timer on section
									sectionTimer = 1;
									questionTimer = 0;
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
										Question.offQuestionTimer(sectionId);
									}
								}
								else if(timerTypeValue == 2) { // set timer on questions
									sectionTimer = 0;
									questionTimer = 1;
									timeDurationValue = 0;
									questionNavigationValue = 0;
									Section.offSectionTimer(sectionId);
								}
								else {
									sectionTimer = 0;
									questionTimer = 0;
									timeDurationValue = 0;
								}
							}
						}
						else {
							errorLog.put("timerType", "Select timer type");
							control = false;
						}
					}

					
					
					if(shuffleQuestionsString != null)
					{
						if(!shuffleQuestionsString.matches("[01]")) {
							errorLog.put("shuffleQuestions", "Invalid shuffleQuestions type");
							control = false;
						}
						else {
							shuffleQuestions = Integer.parseInt(shuffleQuestionsString);
						}
					}
					else {
						errorLog.put("shuffleQuestions", "Select ShuffleQuestion type");
						control = false;
					}
					
					
					
					if(control) {
						Boolean result = false;
						try {
							result =  UpdateSectionDetails.update(sectionId, examId, title, description, questionNavigationValue, sectionTimer, questionTimer, timeDurationValue, shuffleQuestions);
							Exam.inActiveExam(examId);
						} catch(Exception e) {
							e.printStackTrace();
							error = "Something went wrong in database";
						}
						if(result) {
							success = "Section updated successfully";
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
	
	
	public static boolean update(Integer sectionId, Integer examId, String title, String description, Integer questionNavigation, Integer setSectionTimer , Integer setQuestionTimer, Integer timeDurationValue, Integer shuffleQuestions) throws ClassNotFoundException, SQLException {

		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update Sections set title = ?, description = ?, questionNavigation = ?, setSectionTimer = ?, timeDuration = ?, setQuestionTimer = ?, shuffleQuestions = ? where sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, title);
		st.setString(2, description);
		st.setInt(3, questionNavigation);
		st.setInt(4, setSectionTimer);
		st.setInt(5, timeDurationValue);
		st.setInt(6, setQuestionTimer);
		st.setInt(7, shuffleQuestions);
		st.setInt(8, sectionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
		
	}
	
	public static Integer getExamId(Integer sectionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT examId FROM `Sections` WHERE sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer examId = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return examId;
	}
}