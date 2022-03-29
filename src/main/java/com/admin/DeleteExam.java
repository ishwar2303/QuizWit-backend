package com.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
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


@WebServlet("/DeleteExam")
public class DeleteExam extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		String success = "", error = "";
		
		JSONObject json = new JSONObject();
		
		if(adminId == null)
			return;
		
		String examIdString = request.getParameter("examId");
		String permanentlyDelete = request.getParameter("permanentlyDelete");
		if(examIdString != null && Validation.onlyDigits(examIdString) && permanentlyDelete != null && Validation.onlyDigits(permanentlyDelete) && permanentlyDelete.matches("[01]")) {
			try {
				Boolean access = Roles.authorized("DeleteExam", userId);
				if(userId == 0 || access) {
					Integer examId = Integer.parseInt(examIdString);
					try {
						boolean result = false;
						if(permanentlyDelete.equals("1"))
							result = DeleteExam.delete(examId, adminId);
						else result = DeleteExam.changeStatus(examId, adminId);
						if(result)
							success = "Exam deleted successfully";
						else error = "Something went wrong in database while deleting exam";
					} catch(Exception e) {
						e.printStackTrace();
						error = "Something went wrong in database while deleting exam";
					}
				}
				else {
					error = "Access not granted";
				}
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong";
			}
		} 
		else {
			error = "Exam Id required";
		}
		
		json.put("success", success);
		json.put("error", error);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}

	public static boolean delete(Integer examId, Integer adminId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "DELETE FROM Exams WHERE examId = ? AND administratorId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		st.setInt(2, adminId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static boolean changeStatus(Integer examId, Integer adminId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "UPDATE Exams SET isDeleted = ? WHERE examId = ? AND administratorId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, 1);
		st.setInt(2, examId);
		st.setInt(3, adminId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
}
