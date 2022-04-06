package com.answers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.database.AdminDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class MultipleChoiceAnswer {
	public static boolean add(Integer questionId, Integer optionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "insert into McqAnswers values (null, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		st.setInt(2, optionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static boolean delete(Integer answerId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Delete From McqAnswers where answerId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, answerId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static ArrayList<JSONObject> fetch(Integer questionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select * from `McqAnswers` where questionId = ?";
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
