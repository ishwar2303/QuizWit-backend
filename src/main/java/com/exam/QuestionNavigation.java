package com.exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.database.AdminDatabaseConnectivity;
import com.database.StudentDatabaseConnectivity;

public class QuestionNavigation {
	public static Boolean access(Integer navigationId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select access from QuestionNavigation where navigationId = ? AND attemptId = ?";
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
	public static Integer getQuestionId(Integer navigationId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select qn.questionId from QuestionNavigation qn where navigationId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, navigationId);
		st.setInt(2, attemptId);
		ResultSet rs = st.executeQuery();
		Integer questionId = 0;
		if(rs.next())
			questionId = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return questionId;
	}
	
	
	public static Long getEndTime(Integer questionNavigationId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select qn.endTime from QuestionNavigation qn where navigationId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionNavigationId);
		ResultSet rs = st.executeQuery();
		Long time = (long) -1;
		if(rs.next())
			time = rs.getLong(1);
		rs.close();
		st.close();
		con.close();
		return time;
	}
	
	public static Boolean updateEndTime(Integer questionNavigationId, Long endTime) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update QuestionNavigation set endTime = ? where navigationId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setLong(1, endTime);
		st.setInt(2, questionNavigationId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static Boolean updateSubmittedTime(Integer questionNavigationId, Long time) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update QuestionNavigation set submittedTime = ?, submitted = 1 where navigationId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setLong(1, time);
		st.setInt(2, questionNavigationId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static Boolean timerIsSet(Integer questionNavigationId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select qn.endTime from QuestionNavigation qn where navigationId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionNavigationId);
		ResultSet rs = st.executeQuery();
		Long time = (long) -1;
		if(rs.next())
			time = rs.getLong(1);
		rs.close();
		st.close();
		con.close();
		return time == -1 ? false : true;
	}

	public static Integer lastQuestionOfExam(Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select qn.navigationId from QuestionNavigation qn where attemptId = ? ORDER BY navigationId DESC LIMIT 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		ResultSet rs = st.executeQuery();
		Integer id = 0;
		if(rs.next())
			id = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return id;
	}

	public static Integer firstQuestionOfExam(Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select qn.navigationId, qn.questionId, q.sectionId from QuestionNavigation qn INNER JOIN Questions q on q.questionId = qn.questionId Where attemptId = ? ORDER BY navigationId ASC LIMIT 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		ResultSet rs = st.executeQuery();
		Integer id = 0;
		if(rs.next())
			id = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return id;
	}
	
	public static Integer firstQuestionOfSection(Integer attemptId, Integer sectionId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select qn.navigationId, qn.questionId, q.sectionId from QuestionNavigation qn INNER JOIN Questions q on q.questionId = qn.questionId Where attemptId = ? AND q.sectionId = ? ORDER BY navigationId ASC LIMIT 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, sectionId);
		ResultSet rs = st.executeQuery();
		Integer id = 0;
		if(rs.next())
			id = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return id;
	}
	
	public static Integer lastQuestionOfSection(Integer attemptId, Integer sectionId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select qn.navigationId, qn.questionId, q.sectionId from QuestionNavigation qn INNER JOIN Questions q on q.questionId = qn.questionId Where attemptId = ? AND q.sectionId = ? ORDER BY navigationId DESC LIMIT 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, sectionId);
		ResultSet rs = st.executeQuery();
		Integer id = 0;
		if(rs.next())
			id = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return id;
	}
	
	
	public static Boolean validQuestionNavigationId(Integer questionNavigationId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select qn.navigationId from QuestionNavigation qn Where navigationId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionNavigationId);
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
	
	public static Integer getAccessibleNavigationId(Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select navigationId from QuestionNavigation Where attemptId = ? AND access = 1 ORDER BY navigationId ASC LIMIT 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		ResultSet rs = st.executeQuery();
		Integer id = 0;
		if(rs.next())
			id = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return id;
	}

	public static Boolean grantAccess(Integer navigationId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update QuestionNavigation set access = 1 where navigationId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, navigationId);
		st.setInt(2, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static Boolean revokeAccessFromAllQuestionsOfExam(Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "UPDATE QuestionNavigation SET access = 0 WHERE attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static Boolean revokeAccessFromAllQuestionsOfSection(Integer sectionId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "UPDATE QuestionNavigation SET access = 0 WHERE questionId in (SELECT questionId FROM Questions WHERE sectionId = ?) AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, sectionId);
		st.setInt(2, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static Boolean revokeAccess(Integer navigationId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update QuestionNavigation set access = 0 where navigationId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, navigationId);
		st.setInt(2, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static Boolean setAttempted(Integer questionId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update QuestionNavigation set attempted = 1 where questionId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		st.setInt(2, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static Boolean setUnAttempted(Integer questionId, Integer attemptId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "Update QuestionNavigation set attempted = 0 where questionId = ? AND attemptId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, questionId);
		st.setInt(2, attemptId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
}
