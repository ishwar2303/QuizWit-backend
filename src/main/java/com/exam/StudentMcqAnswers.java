package com.exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.database.StudentDatabaseConnectivity;

public class StudentMcqAnswers {
	public static Boolean add(Integer attemptId, Integer questionId, Integer optionId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "INSERT INTO StudentMcqAnswers VALUES (NULL, ?, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, questionId);
		st.setInt(3, optionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static Boolean delete(Integer attemptId, Integer questionId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "DELETE FROM StudentMcqAnswers WHERE attemptId = ? AND questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, questionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

}
