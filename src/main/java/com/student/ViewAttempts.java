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
import com.database.StudentDatabaseConnectivity;
import com.exam.Attempt;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import com.util.Validation;

/**
 * Servlet implementation class ViewAttempts
 */
@WebServlet("/ViewAttempts")
public class ViewAttempts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		HttpSession session = request.getSession();

		Headers.setRequiredHeaders(response, Origin.getStudent());
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		
		Student student = new Student();
		
		Boolean control = true;
		JSONObject errorLog = new JSONObject();

		if(session.getAttribute("StudentLoggedIn") != null && (boolean) session.getAttribute("StudentLoggedIn")) { // student logged in
			Integer studentId = (Integer) session.getAttribute("studentId");
			try {
				String examIdString = request.getParameter("examId");
				ArrayList<JSONObject> attempts = new ArrayList<JSONObject>();
				if(examIdString != null && Validation.onlyDigits(examIdString)) {
					Integer examId = Integer.parseInt(examIdString);
					attempts = ViewAttempts.attempts(studentId, examId);
					for(int i=0; i<attempts.size(); i++) {
						JSONObject attempt = attempts.get(i);
						Integer attemptId = Integer.parseInt((String) attempt.get("attemptId"));
						JSONObject result = AttemptReport.calculate(attemptId);
						attempts.get(i).put("scored", result.get("scored"));
						attempts.get(i).put("totalExamScore", result.get("totalExamScore"));
						Integer totalQuestions = (Integer) result.get("totalQuestions");
						Integer attemptedQuestions = (Integer) result.get("attemptedQuestions");
						Integer correctQuestions = (Integer) result.get("correctQuestions");
						Integer incorrectQuestions = (Integer) result.get("incorrectQuestions");
						attempts.get(i).put("totalQuestions", totalQuestions);
						attempts.get(i).put("attemptedQuestions", attemptedQuestions);
						attempts.get(i).put("incorrectQuestions", incorrectQuestions);
						attempts.get(i).put("correctQuestions", correctQuestions);
					}
					success = "Fetched successfully";
					json.put("attempts", attempts);
				}
				else {
					attempts = ViewAttempts.exams(studentId);
					for(int i=0; i<attempts.size(); i++) {
						JSONObject exam = attempts.get(i);
						Integer examId = Integer.parseInt((String) exam.get("examId"));
						Integer numberOfAttempts = Integer.parseInt((String) exam.get("numberOfAttempts"));
						attempts.get(i).put("givenAttempts", Attempt.countAttemptsOnExam(studentId, examId));
						attempts.get(i).put("totalAttempts", numberOfAttempts);
					}
					json.put("exams", attempts);
					success = "Fetched successfully";
				}
				
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
		json.put("errorLog", errorLog);
		out.println(json.toString());
	}

	public static ArrayList<JSONObject> exams(Integer studentId) throws ClassNotFoundException, SQLException {

		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select title, difficultyLevel, startTime, endTime, examId, numberOfAttempts from Exams e where e.examId IN (select distinct examId from attempts where examSubmitted = 1 and studentId = ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, studentId);
		ResultSet rs = st.executeQuery();
		ArrayList<JSONObject> questions = new ArrayList<JSONObject>();
		while(rs.next()) {
			JSONObject json = new JSONObject();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
	        questions.add(json);
		}
		rs.close();
		st.close();
		con.close();
		return questions;
		
	}
	
	public static ArrayList<JSONObject> attempts(Integer studentId, Integer examId) throws ClassNotFoundException, SQLException {

		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select a.examStartTime as examStartTime, a.examSubmitTime as examSubmitTime, a.attemptId as attemptId from Attempts a where a.examSubmitted = 1 and a.studentId = ? and a.examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, studentId);
		st.setInt(2, examId);
		ResultSet rs = st.executeQuery();
		ArrayList<JSONObject> questions = new ArrayList<JSONObject>();
		while(rs.next()) {
			JSONObject json = new JSONObject();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
	        questions.add(json);
		}
		rs.close();
		st.close();
		con.close();
		return questions;
		
	}
	

}
