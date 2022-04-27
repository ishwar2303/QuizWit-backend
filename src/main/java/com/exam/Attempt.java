package com.exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

import com.database.AdminDatabaseConnectivity;
import com.database.StudentDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class Attempt {
	public static Integer add(Integer examId, Integer studentId, Integer endTime, Long examStartTime) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "INSERT INTO Attempts VALUES (NULL, ?, ?, 0, ?, ?, 0)";
		PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		st.setInt(1, studentId);
		st.setInt(2, examId);
		st.setInt(3, endTime);
		st.setLong(4, examStartTime);
		Integer count = st.executeUpdate();
		ResultSet rs = st.getGeneratedKeys();
		int generatedKey = 0;
		if (rs.next()) {
		    generatedKey = rs.getInt(1);
		}
		st.close();
		con.close();
		return generatedKey;
	}
	public static Boolean updateExamSubmitTime(Integer attemptId, Long examSubmitTime) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update Attempts SET examSubmitTime = ? WHERE attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setLong(1, examSubmitTime);
		st.setInt(2, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
		
	}
	public static Long duration(Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "SELECT endTime FROM `Attempts` WHERE attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Long time = rs.getLong(1);
		rs.close();
		st.close();
		con.close();
		return time;
	}
	

	public static Boolean addSectionNavigationControl(Integer attemptId, Integer sectionId, Integer access, Integer endTime) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "INSERT INTO SectionNavigation VALUES (NULL, ?, ?, ?, ?, 0, 0)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, sectionId);
		st.setInt(3, access);
		st.setInt(4, endTime);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static Boolean addQuestionNavigationControl(Integer attemptId, Integer questionId, Integer access, Integer endTime) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "INSERT INTO QuestionNavigation VALUES (NULL, ?, ?, ?, ?, 0, 0, 0, 0)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, questionId);
		st.setInt(3, access);
		st.setInt(4, endTime);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static Boolean checkIfExamAlreadySubmitted(Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select examSubmitted from Attempts where attemptId = ? ORDER BY attemptId DESC LIMIT 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		ResultSet rs = st.executeQuery();
		Integer val = 0;
		if(rs.next()) {
			val = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return val == 1 ? true : false;
	}
	
	public static Integer getAttemptId(Integer examId, Integer studentId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select attemptId from Attempts where examId = ? and studentId = ? and examSubmitted = 0 ORDER BY attemptId DESC LIMIT 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		st.setInt(2, studentId);
		ResultSet rs = st.executeQuery();
		Integer id = 0;
		if(rs.next()) {
			id = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return id;
	}
	
	public static Long getSectionEndTime(Integer sectionId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select endTime from SectionNavigation where attemptId = ? and sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, sectionId);
		ResultSet rs = st.executeQuery();
		Long time = (long) -1;
		if(rs.next()) {
			time = rs.getLong(1);
		}
		rs.close();
		st.close();
		con.close();
		return time;
	}
	
	public static Boolean endExam(Integer studentId, Integer examId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update Attempts set examSubmitted = 1 where studentId = ? AND examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, studentId);
		st.setInt(2, examId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static Boolean endExam(Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update Attempts set examSubmitted = 1 where attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static JSONObject details(Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		
		String sql = "select * from Attempts where attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
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
	
	public static Integer count(Integer examId, Integer studentId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select COUNT(attemptId) from Attempts where examId = ? and studentId = ? and examSubmitted = 1 ORDER BY attemptId DESC LIMIT 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		st.setInt(2, studentId);
		ResultSet rs = st.executeQuery();
		Integer count = 0;

		if(rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return count;
	}
	
	public static Integer count(Integer adminId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select count(attemptId) from attempts where examId IN (select examId from exams where administratorId = ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		ResultSet rs = st.executeQuery();
		Integer count = 0;

		if(rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return count;
	}
	
	public static Integer countAttemptsOnExam(Integer studentId, Integer examId) throws SQLException, ClassNotFoundException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select count(attemptId) from Attempts where examId = ? AND studentId = ? AND examSubmitted = 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		st.setInt(2, studentId);
		ResultSet rs = st.executeQuery();
		Integer count = 0;

		if(rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return count;
	}

	
	public static Boolean validAttemptOfStudent(Integer attemptId, Integer studentId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select COUNT(attemptId) from Attempts where attemptId = ? AND studentId = ? AND examSubmitted = 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, studentId);
		ResultSet rs = st.executeQuery();
		Integer val = 0;
		if(rs.next()) {
			val = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return val == 1 ? true : false;
	}
	
	public static Integer getExamId(Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select examId from Attempts where attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		ResultSet rs = st.executeQuery();
		Integer id = 0;
		if(rs.next()) {
			id = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return id;
	}
	
}
