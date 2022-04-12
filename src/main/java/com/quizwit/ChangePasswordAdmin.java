package com.quizwit;

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

import com.admin.CreateExam;
import com.admin.Exam;
import com.admin.Roles;
import com.admin.UpdateExamDetails;
import com.config.Headers;
import com.config.Origin;
import com.database.AdminDatabaseConnectivity;
import com.questions.Question;
import com.util.Validation;

/**
 * Servlet implementation class ChangePasswordAdmin
 */
@WebServlet("/ChangePasswordAdmin")
public class ChangePasswordAdmin extends HttpServlet {
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
			String oldPassword = request.getParameter("oldPassword");
			String newPassword = request.getParameter("newPassword");
			String confirmPassword = request.getParameter("newConfirmPassword");
			Boolean control = true;
			if(oldPassword != null && newPassword != null && confirmPassword != null) {
				oldPassword = oldPassword.trim();
				newPassword = newPassword.trim();
				confirmPassword = confirmPassword.trim();

				if(oldPassword.equals("")) {
					control = false;
					errorLog.put("oldPassword", "Old password required");
				}
				else {
					Boolean passwordMatched = false;
					if(userId == 0) { // admin
						passwordMatched = ChangePasswordAdmin.checkAdminPassword(adminId, oldPassword);
					}
					else { // management user
						passwordMatched = ChangePasswordAdmin.checkUserPassword(userId, oldPassword);
					}
					if(!passwordMatched) {
						control = false;
						errorLog.put("oldPassword", "Incorrect old password");
					}
				}
				if(newPassword.equals("")) {
					control = false;
					errorLog.put("newPassword", "New password required");
				}
				else if(newPassword.length() < 8) {
					control = false;
					errorLog.put("newPassword", "Password must contain atleast 8 characters");
				}
				if(confirmPassword.equals("")) {
					control = false;
					errorLog.put("confirmPassword", "Confirm password required");
				}
				else if(!confirmPassword.matches(newPassword)) {
					control = false;
					errorLog.put("confirmPassword", "Password doesn't match");
				}
				
				if(control) { // update password
					Boolean result = false;
					if(userId == 0) {
						result = ChangePasswordAdmin.updateAdminPassword(adminId, newPassword);
					}
					else {
						result = ChangePasswordAdmin.updateUserPassword(userId, newPassword);
					}
					if(result) {
						success = "Password changed successfully";
					}
					else {
						error = "Something went wrong while updating password";
					}
				}
				else {
					error = "Please fill required fields appropriately";
				}
			}
			else {
				error = "Please fill required fields appropriately";
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

	public static boolean checkAdminPassword(Integer adminId, String password) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(administratorId) FROM Administrators WHERE administratorId = ? AND password = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		st.setString(2, password);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer count = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static boolean checkUserPassword(Integer userId, String password) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(userId) FROM Users WHERE userId = ? AND password = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, userId);
		st.setString(2, password);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer count = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	

	public static boolean updateAdminPassword(Integer adminId, String password) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update Administrators SET password = ? WHERE administratorId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, password);
		st.setInt(2, adminId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static boolean updateUserPassword(Integer userId, String password) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update Users SET password = ? WHERE userId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, password);
		st.setInt(2, userId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
}
