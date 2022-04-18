package com.exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.database.AdminDatabaseConnectivity;
import com.database.StudentDatabaseConnectivity;

public class SectionNavigation {

	public static Boolean access(Integer navigationId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select access from SectionNavigation where navigationId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, navigationId);
		st.setInt(2, attemptId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer val = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return val == 1 ? true : false;
	}

	public static Boolean grantAccess(Integer navigationId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update SectionNavigation set access = 1 where navigationId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, navigationId);
		st.setInt(2, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static Boolean revokeAccess(Integer sectionId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update SectionNavigation set access = 0 where sectionId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		st.setInt(2, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}


	public static Boolean updateSubmittedTime(Integer sectionId, Integer attemptId, Long time) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update SectionNavigation set submittedTime = ?, submitted = 1 where sectionId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setLong(1, time);
		st.setLong(2, sectionId);
		st.setInt(3, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	

	
	
	public static Boolean validSectionNavigationId(Integer sectionNavigationId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select sn.navigationId from SectionNavigation sn Where navigationId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionNavigationId);
		st.setInt(2,  attemptId);
		ResultSet rs = st.executeQuery();
		Integer id = 0;
		if(rs.next())
			id = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return id != 0 ? true : false;
	}
	
	
	public static Integer getNavigationId(Integer sectionId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select navigationId from SectionNavigation where sectionId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		st.setInt(2, attemptId);
		ResultSet rs = st.executeQuery();
		
		Integer id = 0;
		if(rs.next())
			id= rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return id;
	}

	public static Boolean updateEndTime(Integer navigationId, Long endTime) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update SectionNavigation set endTime = ? where navigationId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setLong(1, endTime);
		st.setInt(2, navigationId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static Boolean timerIsSet(Integer navigationId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select sn.endTime from SectionNavigation sn where navigationId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, navigationId);
		ResultSet rs = st.executeQuery();
		Long time = (long) -1;
		if(rs.next())
			time = rs.getLong(1);
		rs.close();
		st.close();
		con.close();
		return time == -1 ? false : true;
	}

	public static Boolean revokeAccessFromAllSectionsOfExam(Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "UPDATE SectionNavigation SET access = 0 WHERE attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	

	
}
