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
 * Servlet implementation class ViewExams
 */
@WebServlet("/ViewExams")
public class ViewExams extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		String success = "", error = "";
		JSONObject json = new JSONObject();
		JSONObject exam = new JSONObject();
		ArrayList<JSONObject> examDetails =  new ArrayList<JSONObject>();
		if(adminId == null)
			return;
		
		String examIdString = request.getParameter("examId");
		if(examIdString != null && Validation.onlyDigits(examIdString)) {
			try {
				Integer examId = Integer.parseInt(examIdString);
				exam = ViewExams.fetchExam(adminId, examId);
				success = "Details fetched successfully";
				json.put("examDetails", exam);
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong while fetching exam details from database";
			}
		}
		else {
			String limitString = request.getParameter("limit");
			String pageString = request.getParameter("page");
			Integer limit = 2;
			Integer page = 1;
			if(limitString != null) {
				limit = Integer.parseInt(limitString);
			}
			if(pageString != null) {
				page = Integer.parseInt(pageString);
			}
			try {
				Integer examCount = ViewExams.examsCount(adminId);
				Integer totalPages = (int) Math.ceil(examCount/(double)limit);
				Integer offset = (page - 1)*limit; // rows to skip
				
				examDetails = ViewExams.fetchAllExams(adminId, limit, offset);
				success = "Details fetched successfully";
				json.put("examDetails", examDetails);
				json.put("totalPages", totalPages);
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong while fetching exam details from database";
			}
			
		}

		json.put("success", success);
		json.put("error", error);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}
	
	public static ArrayList<JSONObject> fetchAllExams(Integer adminId, Integer limit, Integer offset) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select * from `Exams` where administratorId = ? order by examId desc LIMIT ? OFFSET ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		st.setInt(2, limit); // rows to fetch
		st.setInt(3, offset); // rows to skip
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

	public static JSONObject fetchExam(Integer adminId, Integer examId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select * from `Exams` where administratorId = ? AND examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		st.setInt(2, examId);
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
	

	public static Integer examsCount(Integer adminId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(examId) FROM `Exams` WHERE administratorId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer records = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return records;
	}
	
}
