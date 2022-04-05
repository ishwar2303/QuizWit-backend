package com.questions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.database.AdminDatabaseConnectivity;

public class Question {
	public static boolean add(Integer sectionId, Integer categoryId, String question, Double score, Double negative, String explanation, Integer timeDuration) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "insert into Questions values (null, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		st.setInt(2, categoryId);
		st.setString(3, question);
		st.setDouble(4, score);
		st.setDouble(5, negative);
		st.setString(6, explanation);
		st.setInt(7, timeDuration);
		Integer count = st.executeUpdate();
		st.close();
		return count > 0 ? true : false;
	}
	
	public static boolean update(Integer questionId, String question, Double score, Double negative, String explanation, Integer timeDuration) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update Questions set question = ?, score = ?, negative = ?, explanation = ?, timeDuration = ? where questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, question);
		st.setDouble(2, score);
		st.setDouble(3, negative);
		st.setString(4, explanation);
		st.setInt(5, timeDuration);
		st.setInt(6, questionId);
		Integer count = st.executeUpdate();
		st.close();
		return count > 0 ? true : false;
	}
	
	public static boolean delete(Integer questionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "DELETE FROM Questions WHERE questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static Integer authorized(Integer questionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Select e.administratorId from Questions q INNER JOIN Sections s on q.sectionId = s.sectionId INNER JOIN Exams e on e.examId = s.examId where q.questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer adminId = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return adminId;
	}
	
	public static boolean setQuestionTimer(Integer sectionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select setQuestionTimer from Sections where sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer timer = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return timer == 1 ? true : false;
	}

}
