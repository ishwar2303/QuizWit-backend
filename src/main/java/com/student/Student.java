package com.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import com.database.StudentDatabaseConnectivity;
import com.database.StudentDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class Student {

	public boolean exists(String email) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(studentId) FROM `Students` WHERE email = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, email);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer records = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return records > 0 ? true : false;
	}
	
	public boolean add(String fullName, String email, String contact, String password) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "INSERT INTO `Students` VALUES (NULL, ?, ?, ?, ?, 1)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, fullName);
		st.setString(2, email);
		st.setString(3, contact);
		st.setString(4, password);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public JSONObject details(Integer studentId) throws SQLException, ClassNotFoundException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select fullName, email, contact from `Students` where studentId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, studentId);
		
		ResultSet rs = st.executeQuery();
		rs.next();
		ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
		JSONObject json = new JSONObject();
        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
        }
		
		return json;
	}
	
	public Integer login(String email, String password) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select studentId from `Students` WHERE email = ? AND password = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, email);
		st.setString(2, password);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer studentId = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return studentId;
	}
}