package com.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import com.util.Validation;

/**
 * Servlet implementation class ViewSections
 */
@WebServlet("/ViewSections")
public class ViewSections extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		String success = "", error = "";
		JSONObject json = new JSONObject();
		JSONObject section = new JSONObject();
		ArrayList<JSONObject> sectionDetails =  new ArrayList<JSONObject>();
		if(adminId == null)
			return;
		
		String sectionIdString = request.getParameter("sectionId");
		String examIdString = request.getParameter("examId");
		if(sectionIdString != null && Validation.onlyDigits(sectionIdString)) {
			try {
				Integer sectionId = Integer.parseInt(sectionIdString);
				
				Integer sectionExamId = AddSection.getExamId(sectionId);
				boolean correctExamId = false;
				correctExamId = CreateExam.examExists(adminId, sectionExamId);
				if(!correctExamId) {
					error = "Exam doesn't belongs to this account";
				}
				else {
					section = ViewSections.fetchSection(sectionId);
					success = "Details fetched successfully";
					json.put("sectionDetails", section);
				}
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong while fetching section details from database";
			}
		}
		else if(examIdString != null && Validation.onlyDigits(examIdString)) {
			Integer examId = Integer.parseInt(examIdString);
			try {
				if(CreateExam.examExists(adminId, examId)) {
					sectionDetails = ViewSections.fetchAllSections(examId);
					success = "Details fetched successfully";
					json.put("sectionDetails", sectionDetails);
				}
				else {
					error = "Exam doesn't belongs to this account";
				}
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong while fetching exam details from database";
			}
			
		}
		else {
			error = "Exam Id or Section Id required to fetch section details";
		}

		json.put("success", success);
		json.put("error", error);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}

	public static ArrayList<JSONObject> fetchAllSections(Integer examId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select * from `Sections` where examId = ? order by sectionId desc";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		ArrayList<JSONObject> details = new ArrayList<JSONObject>();
		while(rs.next()) {
			JSONObject json = new JSONObject();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
	        details.add(json);
		}
		return details;
	}

	public static JSONObject fetchSection(Integer sectionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select * from `Sections` where sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		ResultSet rs = st.executeQuery();
		JSONObject json = new JSONObject();
		if(rs.next()) {
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
		}
		return json;
	}
	


}
