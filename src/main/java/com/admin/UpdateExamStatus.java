package com.admin;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

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
 * Servlet implementation class UpdateExamStatus
 */
@WebServlet("/UpdateExamStatus")
public class UpdateExamStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		String success = "", error = "";
		JSONObject json = new JSONObject();
		ArrayList<String> errorLog = new ArrayList<String>();
		if(adminId == null)
			return;

		try {
			Boolean access = Roles.authorized("UpdateExamStatus", userId);
			if(userId == 0 || access) {
				String examIdString = request.getParameter("examId");
				String statusString = request.getParameter("status");
				if(examIdString != null && statusString != null) {
					if(Validation.onlyDigits(examIdString) && Validation.onlyDigits(statusString)) {
						Integer examId = Integer.parseInt(examIdString);
						Integer status = Integer.parseInt(statusString);
						Boolean result = false;
						ArrayList<JSONObject> sections = ViewSections.fetchAllSections(examId);
						boolean control = true;
						
						if(sections.size() == 0) {
							control = false;
							errorLog.add("Exam doesn't contain any section");
						}
						if(sections.size() > 0) {
							for(int i=0; i<sections.size(); i++) {
								JSONObject section = sections.get(i);
								Integer sectionId = Integer.parseInt((String) section.get("sectionId"));
								Integer questionCount = Question.count(sectionId);
								if(questionCount == 0) {
									control = false;
									errorLog.add(section.get("title") + " section doesn't contain any question.");
								}
							}
						}
						

						if(control)
						{
							result = UpdateExamStatus.updateStatus(adminId, examId, status);
							if(result)
								success = "Status changed";
							else error = "Something went wrong in database while changing exam status";
						}
						else {
							error = "Please do all required actions on exam to activate it.";
						}
					}
					else {
						error = "Invalid id or status code";
					}
				}
				else {
					error = "Exam Id and status required to update";
				}
			}
			else {
				error = "Access not granted";
			}
		} catch(Exception e) {
			e.printStackTrace();
			error = "Something went wrong";
		}

		json.put("errorLog", errorLog);
		json.put("success", success);
		json.put("error", error);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
		
		
	}

	public static boolean updateStatus(Integer adminId, Integer examId, Integer status) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "UPDATE Exams SET isActive = ? WHERE examId = ? AND administratorId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, status);
		st.setInt(2, examId);
		st.setInt(3, adminId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
		
	}
}
