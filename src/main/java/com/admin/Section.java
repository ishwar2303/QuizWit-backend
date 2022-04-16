package com.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.database.AdminDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class Section {

	public static boolean offSectionTimer(Integer sectionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update Sections set setSectionTimer = 0, timeDuration = 0 where sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static boolean setQuestionTimer(Integer sectionId) throws ClassNotFoundException, SQLException {
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
	
	public static boolean setSectionTimer(Integer sectionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select setSectionTimer from Sections where sectionId = ?";
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
	
	public static ArrayList<Integer> getAllQuestionsId(Integer sectionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select questionId from Questions where sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		ResultSet rs = st.executeQuery();
		
		ArrayList<Integer> questions = new ArrayList<Integer>();
		
		
		while(rs.next()) {
			questions.add(rs.getInt(1));
		}
		return questions;
	}
	
}
