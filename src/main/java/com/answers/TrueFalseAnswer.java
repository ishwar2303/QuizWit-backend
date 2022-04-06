package com.answers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import com.database.AdminDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class TrueFalseAnswer {
	public static boolean add(Integer questionId, Integer answer) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "insert into TrueFalseAnswers values (null, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		st.setInt(2, answer);
		Integer count = st.executeUpdate();
		st.close();
		return count > 0 ? true : false;
	}
	
	public static boolean update(Integer questionId, Integer answer) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update TrueFalseAnswers set answer = ? where questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, answer);
		st.setInt(2, questionId);
		Integer count = st.executeUpdate();
		st.close();
		return count > 0 ? true : false;
	}


	public static JSONObject fetch(Integer questionId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select * from `TrueFalseAnswers` where questionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
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
	
	
}
