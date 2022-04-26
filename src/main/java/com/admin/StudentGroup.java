package com.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.database.AdminDatabaseConnectivity;
import com.database.StudentDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class StudentGroup {
	public static Boolean add(Integer examId, String email) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Insert into StudentGroupOfExam values (NULL, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, email);
		st.setInt(2, examId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	public static Boolean exist(Integer examId, String email) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT count(groupId) from StudentGroupOfExam where examId = ? AND email = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		st.setString(2, email);
		ResultSet rs = st.executeQuery();
		Integer count = 0;
		if(rs.next())
			count = rs.getInt(1);
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	public static Integer getExamId(Integer groupId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT examId from StudentGroupOfExam where groupId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, groupId);
		ResultSet rs = st.executeQuery();
		Integer id = 0;
		if(rs.next())
			id = rs.getInt(1);
		st.close();
		con.close();
		return id;
	}
	public static Boolean delete(Integer groupId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Delete from StudentGroupOfExam where groupId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, groupId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static ArrayList<JSONObject> fetch(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT * FROM StudentGroupOfExam WHERE examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		ArrayList<JSONObject> group = new ArrayList<JSONObject>();
		while(rs.next()) {
			JSONObject json = new JSONObject();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
	       group.add(json);
		}
		rs.close();
		st.close();
		con.close();
		return group;
	}
	
}
