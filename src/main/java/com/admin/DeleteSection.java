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

/**
 * Servlet implementation class DeleteSection
 */
@WebServlet("/DeleteSection")
public class DeleteSection extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		String success = "", error = "";
		
		JSONObject json = new JSONObject();
		
		if(adminId == null)
			return;
		
		String sectionIdString = request.getParameter("sectionId");
		if(sectionIdString != null && Validation.onlyDigits(sectionIdString)) {
			try {
				Boolean access = Roles.authorized("DeleteSection", userId);
				if(userId == 0 || access) {
					Integer sectionId = Integer.parseInt(sectionIdString);
					try {
						Integer sectionExamId = AddSection.getExamId(sectionId);
						boolean correctExamId = false;
						correctExamId = CreateExam.examExists(adminId, sectionExamId);
						if(!correctExamId) {
							error = "Exam doesn't belongs to this account";
						}
						else {
							boolean result = false;
							result = DeleteSection.delete(sectionId);
							if(result)
								success = "Section deleted successfully";
							else error = "Something went wrong in database while deleting section";
						}
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

	public static boolean delete(Integer sectionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "DELETE FROM Sections WHERE sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
}
