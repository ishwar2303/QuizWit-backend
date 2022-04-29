package com.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import com.database.AdminDatabaseConnectivity;
import com.database.StudentDatabaseConnectivity;

public class Exam {
	public static boolean setEntireExamTimer(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select setEntireExamTimer from Exams where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer timer = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return timer == 1 ? true : false;
	}
	public static boolean setSectionTimer(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select setSectionTimer from Exams where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer timer = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return timer == 1 ? true : false;
	}
	
	public static boolean offSectionTimer(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update Sections set setSectionTimer = 0, setQuestionTimer = 0, timeDuration = 0 where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static boolean visibilityPrivate(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select private from Exams where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer visibility = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return visibility == 1 ? true : false;
	}

	public static boolean exists(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select COUNT(examId) from Exams where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer id = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return id > 0 ? true : false;
	}

	public static boolean isActive(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select isActive from Exams where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer status = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return status == 1 ? true : false;
	}
	
	public static Integer duration(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select timeDuration from Exams where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer time = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return time;
	}
	
	public static Integer totalSectionTimerDuration(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select SUM(timeDuration) from Sections where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer time = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return time;
	}
	
	public static Integer totalQuestionTimeInSectionOnQuestion(Integer sectionId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select SUM(q.timeDuration) from Questions q INNER JOIN Sections s on q.sectionId = s.sectionId where s.sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer time = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return time;
	}
	
	public static Integer totalQuestionTimeDuration(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select SUM(q.timeDuration) FROM Questions q INNER JOIN Sections s on q.sectionId = s.sectionId INNER JOIN Exams e on e.examId = s.examId WHERE e.examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer time = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return time;
	}
	

	public static Boolean sectionNavigation(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select sectionNavigation from Exams where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer nav = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return nav == 1 ? true : false;
	}
	
	public static String fetchTitle(Integer examId) throws ClassNotFoundException, SQLException {

		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select title from Exams where examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		ResultSet rs = st.executeQuery();
		rs.next();
		String title = rs.getString(1);
		rs.close();
		st.close();
		con.close();
		return title;
	}
	

	public static int runningExam(Integer currentTime, Integer adminId) throws ClassNotFoundException, SQLException
	{
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select count(examId) from exams where examId IN (select examId from exams where administratorId = ?) AND endTime > ? AND startTime < ? AND isActive = 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		st.setInt(2, currentTime);
		st.setInt(3, currentTime);
		ResultSet rs = st.executeQuery();
		Integer count = 0;

		if(rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return count;
		
	}
	
	public static int scheduledExam(Integer currentTime, Integer adminId) throws ClassNotFoundException, SQLException
	{
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select count(e.examId) from exams as e where e.examId IN (select examId from exams where administratorId = ?) AND e.endTime > ? AND e.startTime > ? AND e.isActive = 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		st.setInt(2, currentTime);
		st.setInt(3, currentTime);
		ResultSet rs = st.executeQuery();
		Integer count = 0;

		if(rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return count;
		
	}
	
	public static int totalExams(Integer adminId) throws ClassNotFoundException, SQLException
	{
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select count(examId) from exams where examId IN (select examId from exams where administratorId = ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		ResultSet rs = st.executeQuery();
		Integer count = 0;

		if(rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return count;
		
	}
	
	public static int endedExams(Integer adminId, Integer currentTime) throws ClassNotFoundException, SQLException
	{
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select count(examId) from exams where examId IN (select examId from exams where administratorId = ?) AND endTime < ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		st.setInt(2, currentTime);
		ResultSet rs = st.executeQuery();
		Integer count = 0;

		if(rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		st.close();
		con.close();
		return count;
		
	}
	
	public static boolean inActiveExam(Integer examId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "Update Exams set isActive = 0 WHERE examId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, examId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	
	
}
