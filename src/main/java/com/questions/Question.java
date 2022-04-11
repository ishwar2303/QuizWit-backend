package com.questions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.database.AdminDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class Question {
	public static Integer add(Integer sectionId, Integer categoryId, String question, Double score, Double negative, String explanation, Integer timeDuration) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "insert into Questions values (null, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		st.setInt(1, sectionId);
		st.setInt(2, categoryId);
		st.setString(3, question);
		st.setDouble(4, score);
		st.setDouble(5, negative);
		st.setString(6, explanation);
		st.setInt(7, timeDuration);
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
		con.close();
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
	
	public static Integer getSectionId(Integer questionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Select sectionId from Questions where questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer sectionId = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return sectionId;
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

	
	public static JSONObject fetch(Integer sectionId, Integer page) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select * from Questions where sectionId = ? order by questionId desc LIMIT 1 OFFSET ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		st.setInt(2, page);
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

	public static ArrayList<JSONObject> fetchTitle(Integer sectionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select SUBSTRING(question, 1, 51) as question from Questions where sectionId = ? order by questionId desc";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
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
		return questions;
	}

	public static Integer count(Integer sectionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(questionId) FROM `Questions` WHERE sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer records = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return records;
	}
	
	public static Integer categoryId(Integer questionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT categoryId FROM `Questions` WHERE questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer categoryId = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return categoryId;
	}
	public static boolean offQuestionTimer(Integer sectionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update Questions set timeDuration = 0 where sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	
}
