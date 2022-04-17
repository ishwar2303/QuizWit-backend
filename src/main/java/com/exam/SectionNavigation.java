package com.exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
