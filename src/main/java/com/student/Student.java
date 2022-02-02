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
		String sql = "SELECT COUNT(studentId) FROM `student` WHERE email = ?";
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
	
	public boolean add(String firstName, String lastName, String email, String contact, String gender, String institution, String dateOfBirth, String password) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "INSERT INTO `student` VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, firstName);
		st.setString(2, lastName);
		st.setString(3, email);
		st.setString(4, contact);
		st.setInt(5, Integer.parseInt(gender));
		st.setString(6, institution);
		st.setString(7, dateOfBirth);
		st.setString(8, password);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public JSONObject details(Integer studentId) throws SQLException, ClassNotFoundException {
		StudentDatabaseConnectivity adc = new StudentDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select s.studentId, firstName, path from Student s INNER JOIN StudentImage si on s.studentId = si.studentId WHERE s.studentId = ?";
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
		
		String sql = "select a.studentId from Student s INNER JOIN StudentImage si on s.studentId = si.studentId WHERE email = ? AND password = ?";
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