package com.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import com.database.StudentDatabaseConnectivity;
import com.database.StudentDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class Student {

	public boolean exists(String email) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(studentId) FROM `Students` WHERE email = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, email);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer records = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return records > 0 ? true : false;
	}
	
	public static Integer attempts(Integer studentId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(attemptId) from attempts where examSubmitted = 1 AND studentId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, studentId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer attempts = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return attempts;
	}
	
	public static Integer endedExams(String email, Integer currentTime) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(e.examId) from exams as e INNER JOIN studentGroupOfExam as sge on e.examId = sge.examId where email = ? AND endTime > ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, email);
		st.setInt(2, currentTime);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer endedExams = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return endedExams;
	}
	
	public static String getEmail(Integer studentId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT email from Students where studentId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, studentId);
		ResultSet rs = st.executeQuery();
		rs.next();
		String email = rs.getString(1);
		rs.close();
		st.close();
		con.close();
		return email;
	}
	
	
	public static Integer scheduledExams(String email, Integer currentTime) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(e.examId) from exams as e INNER JOIN studentGroupOfExam as sge on e.examId = sge.examId where email = ? AND endTime < ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, email);
		st.setInt(2, currentTime);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer scheduledExams = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return scheduledExams;
	}
	
	public boolean add(String fullName, String email, String contact, String password) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "INSERT INTO `Students` VALUES (NULL, ?, ?, ?, ?, 1)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, fullName);
		st.setString(2, email);
		st.setString(3, contact);
		st.setString(4, password);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public JSONObject details(Integer studentId) throws SQLException, ClassNotFoundException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select fullName, email, contact from `Students` where studentId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, studentId);
		
		ResultSet rs = st.executeQuery();
		rs.next();
		ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
		JSONObject json = new JSONObject();
        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
        }
		
		return json;
	}
	
	public Integer login(String email, String password) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select studentId from `Students` WHERE email = ? AND password = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, email);
		st.setString(2, password);
		ResultSet rs = st.executeQuery();
		Integer studentId = 0;
		if(rs.next()) {
			studentId = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return studentId;
	}
}