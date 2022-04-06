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

public class MultipleChoiceQuestionOption {
	public static Integer add(Integer questionId, String option) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Insert into McqOptions values (null, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		st.setInt(1, questionId);
		st.setString(2, option);
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

	public static boolean update(Integer optionId, String option) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update McqOptions set option = ? where optionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, option);
		st.setInt(2, optionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static boolean delete(Integer optionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "DELETE FROM McqOptions WHERE optionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, optionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	

	public static Integer authorized(Integer optionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select e.administratorId from McqOptions m inner join Questions q on q.questionId = m.questionId inner join Sections s on s.sectionId = q.sectionId inner join Exams e on e.examId = s.examId where m.optionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, optionId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer adminId = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return adminId;
	}

	public static ArrayList<JSONObject> fetch(Integer questionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select * from `McqOptions` where questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		ResultSet rs = st.executeQuery();
		ArrayList<JSONObject> options = new ArrayList<JSONObject>();
		
		while(rs.next()) {
			JSONObject option = new JSONObject();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            option.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
	        options.add(option);
		}
		
	
		return options;
	}
	
}
