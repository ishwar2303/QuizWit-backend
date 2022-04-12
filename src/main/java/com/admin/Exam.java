package com.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.database.AdminDatabaseConnectivity;

public class Exam {
	public static boolean setEntireExamTimer(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select setEntireExamTimer from Exams where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer timer = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return timer == 1 ? true : false;
	}
	public static boolean setSectionTimer(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select setSectionTimer from Exams where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer timer = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return timer == 1 ? true : false;
	}
	
	public static boolean offSectionTimer(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update Sections set setSectionTimer = 0, setQuestionTimer = 0, timeDuration = 0 where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
}
