package com.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import com.database.AdminDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class Admin {

	public boolean exists(String email) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(administratorId) FROM `Administrators` WHERE email = ?";
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
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "INSERT INTO `Administrators` VALUES (NULL, ?, ?, ?, ?, 1)";
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
	
	public JSONObject details(Integer adminId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select administratorId, fullName, email, contact from `Administrators` where administratorId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
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
	
	public static Integer login(String email, String password) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select administratorId from `Administrators` WHERE email = ? AND password = ? AND isActive = 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, email);
		st.setString(2, password);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer adminId = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		System.out.println("adminId from db: " + adminId);
		return adminId;
	}
}