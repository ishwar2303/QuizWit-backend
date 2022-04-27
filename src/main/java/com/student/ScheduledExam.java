package com.student;

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
import com.database.StudentDatabaseConnectivity;
import com.exam.Attempt;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import com.util.Validation;

/**
 * Servlet implementation class ScheduledExam
 */
@WebServlet("/ScheduledExam")
public class ScheduledExam extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		HttpSession session = request.getSession();

		Headers.setRequiredHeaders(response, Origin.getStudent());
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		
		Student student = new Student();
		
		Boolean control = true;

		if(session.getAttribute("StudentLoggedIn") != null && (boolean) session.getAttribute("StudentLoggedIn")) { // student logged in
			Integer studentId = (Integer) session.getAttribute("studentId");
			JSONObject studentDetails = (JSONObject) session.getAttribute("details");
			String email = (String) studentDetails.get("email");
			try {
				ArrayList<JSONObject> exams = ScheduledExam.exams(email, System.currentTimeMillis());
				json.put("exams", exams);
				success = "Fetched successfully";
				
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong";
			}
		}
		else {
			error = "Student not logged in";
		}
		
		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		out.println(json.toString());
	}

	
	public static ArrayList<JSONObject> exams(String email, Long currentTime) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select e.title as title, e.examId as examId, difficultyLevel, startTime, endTime, numberOfAttempts from Exams e inner join StudentGroupOfExam sge on e.examId = sge.examId inner join Students s on s.email = sge.email where endTime > ? and sge.email = ? and e.isActive = 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setLong(1, currentTime);
		st.setString(2, email);
		ResultSet rs = st.executeQuery();
		ArrayList<JSONObject> exams = new ArrayList<JSONObject>();
		while(rs.next()) {
			JSONObject exam = new JSONObject();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            exam.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
	        exams.add(exam);
		}
		rs.close();
		st.close();
		con.close();
		return exams;
	}
	
}
