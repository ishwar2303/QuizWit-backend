package com.evaluation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.database.AdminDatabaseConnectivity;
import com.database.StudentDatabaseConnectivity;

public class EvaluateMultipleChoiceAnswer {
	public static ArrayList<Integer> selectedOptions(Integer questionId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select optionId from StudentMcqAnswers where questionId = ? AND attemptId = ? ORDER BY optionId ASC";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		st.setInt(2, attemptId);
		ResultSet rs = st.executeQuery();
		ArrayList<Integer> options = new ArrayList<Integer>();
		
		while(rs.next()) {
			options.add(rs.getInt(1));
		}
		rs.close();
		st.close();
		con.close();
		return options;
	}
	public static ArrayList<Integer> correctOptions(Integer questionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select optionId from McqAnswers where questionId = ? ORDER BY optionId ASC";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		ResultSet rs = st.executeQuery();
		ArrayList<Integer> options = new ArrayList<Integer>();
		
		while(rs.next()) {
			options.add(rs.getInt(1));
		}
		rs.close();
		st.close();
		con.close();
		return options;
	}
	
}
