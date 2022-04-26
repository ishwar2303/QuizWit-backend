package com.evaluation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.database.AdminDatabaseConnectivity;
import com.database.StudentDatabaseConnectivity;

public class EvaluateTrueFalseAnswer {
	
	public static Boolean attempted(Integer questionId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select COUNT(answerId) from StudentTrueFalseAnswers where questionId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		st.setInt(2, attemptId);
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
	
	public static String correctAnswer(Integer questionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select answer from TrueFalseAnswers where questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		ResultSet rs = st.executeQuery();
		Integer val = 0;
		if(rs.next()) {
			val = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return val == 1 ? "TRUE" : "FALSE";
	}
	
	public static String selectedAnswer(Integer questionId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select answer from StudentTrueFalseAnswers where questionId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		st.setInt(2, attemptId);
		ResultSet rs = st.executeQuery();
		String val = "";
		if(rs.next()) {
			val = rs.getString(1);
		}
		rs.close();
		st.close();
		con.close();
		return val;
	}
	
}
