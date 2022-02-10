package com.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AdminDatabaseConnectivity {

	private final String api = "jdbc:mysql://";
	private final String server = "localhost";
	private final String port = "3306";
	private final String database = "quizwit";
	private final String username = "root";
	private final String password = "23031999";
	
	public Connection connection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = api + server + ":" + port + "/" + database;
		Connection con = DriverManager.getConnection(url, username, password);
		return con;
	}
}
