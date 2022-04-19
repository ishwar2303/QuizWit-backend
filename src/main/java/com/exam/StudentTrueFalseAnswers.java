package com.exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.database.StudentDatabaseConnectivity;

public class StudentTrueFalseAnswers {
	
	public static String selected(Integer attemptId, Integer questionId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select answer FROM StudentTrueFalseAnswers WHERE attemptId = ? AND questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, questionId);
		ResultSet rs = st.executeQuery();
		String answer = "";
		if(rs.next())
			answer = rs.getString(1);
		rs.close();
		st.close();
		con.close();
		return answer;
	}
	public static Boolean exists(Integer attemptId, Integer questionId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "SELECT COUNT(attemptId) FROM StudentTrueFalseAnswers WHERE attemptId = ? AND questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, questionId);
		ResultSet rs = st.executeQuery();
		Integer count = 0;
		if(rs.next())
			count = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return count != 0 ? true : false;
	}
	public static Boolean add(Integer attemptId, Integer questionId, String answer) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "INSERT INTO StudentTrueFalseAnswers VALUES (NULL, ?, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, questionId);
		st.setString(3, answer);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static Boolean update(Integer attemptId, Integer questionId, String answer) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update StudentTrueFalseAnswers SET answer = ? WHERE attemptId = ? AND questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, answer);
		st.setInt(2, attemptId);
		st.setInt(3, questionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	public static Boolean delete(Integer attemptId, Integer questionId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "DELETE FROM StudentTrueFalseAnswers WHERE attemptId = ? AND questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, questionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
}
