package com.exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.database.AdminDatabaseConnectivity;
import com.database.StudentDatabaseConnectivity;

public class Attempt {
	public static Integer add(Integer examId, Integer studentId, Integer endTime) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "INSERT INTO Attempts VALUES (NULL, ?, ?, 0, ?)";
		PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		st.setInt(1, studentId);
		st.setInt(2, examId);
		st.setInt(3, endTime);
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
		String sql = "INSERT INTO SectionNavigation VALUES (NULL, ?, ?, ?, ?)";
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
		String sql = "INSERT INTO QuestionNavigation VALUES (NULL, ?, ?, ?, ?, 0, 0)";
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
	
	public static Integer getAttemptId(Integer examId, Integer studentId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select attemptId from Attempts where examId = ? and studentId = ?";
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
	
}
