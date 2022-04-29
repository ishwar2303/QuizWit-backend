package com.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.config.Headers;
import com.config.Origin;
import com.database.AdminDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

@WebServlet("/ManagementUser")
public class ManagementUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		String userIdString = (String) session.getAttribute("userId");
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		JSONObject json = new JSONObject();
		String success = "", error = "";
		ArrayList<JSONObject> users = new ArrayList<JSONObject>();
		try {
			Integer userId = 0;
			if(userIdString != null && userIdString.matches("[0-9]+")) {
				userId = Integer.parseInt(userIdString);
			}
			users = ManagementUser.getAllUserDetails(adminId, userId);
			success = "Management users fetched successfully";
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			error = "Something went wrong while fetching management users";
			e.printStackTrace();
		}
		json.put("users", users);
		json.put("success", success);
		json.put("error", error);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String operation = request.getParameter("operation");
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		Boolean control = true;
		JSONObject errorLog = new JSONObject();
		String success = "";
		String error = "";
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		JSONObject json = new JSONObject();
		if(operation != null) {
			if(operation.equals("CreateUser")) {
				Boolean access = false;
				try {
					access = Roles.authorized("CreateUser", userId);
					if(userId == 0 || access) {
						String username = request.getParameter("username");
						String password = request.getParameter("password");
						String confirmPassword = request.getParameter("confirmPassword");
						if(username != null && password != null && confirmPassword != null) {
							if(username != "") {
								username = username.toLowerCase();			
								if(!username.matches("[a-z]{1}[a-z0-9]{5,29}")) {
									control = false;
									errorLog.put("username", "Username must start with alphabet and it should contain only alphabets and numbers, minimum length 6 characters");
								}
								else {
									try {
										Boolean result = true;
										result = ManagementUser.exists(username);
										if(result) {
											control = false;
											errorLog.put("username", "Username already taken");
										}
									}catch(Exception e) {
										e.printStackTrace();
									}
								}
							}
							else {
								control = false;
								errorLog.put("username", "Username required");
							}
							if(password != null) {
								if(password.length() < 8) {
									control = false;
									errorLog.put("password", "Password must contain atleast 8 characters");
								}
							}
							if(!password.equals(confirmPassword)) {
								control = false;
								errorLog.put("confirmPassword", "Password not matched");
							}
							if(control) {
								try {
									boolean result = ManagementUser.add(username, password, adminId);
									if(result) {
										success = "User added successfully";
									}
									else {
										error = "Something went wrong";
									}
								}catch(Exception e) {
									e.printStackTrace();
								}
							}
							else {
								error = "Please fill details appropriately";
							}
						}
						else {
							error = "Please fill details appropriately";
						}
					}
					else {
						error = "You are not authorized to create a user";
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(operation.equals("UpdateUserStatus")) {

				Boolean access = false;
				try {
					access = Roles.authorized("UpdateUserStatus", userId);
					if(userId == 0 || access) {
						String status = request.getParameter("status");
						String userIdString = request.getParameter("userId");
						if(status != null && userIdString != null) {
							if(!status.matches("[01]")) {
								error = "Status invalid";
							}
							if(!userIdString.matches("[0-9]+")) {
								error = "User Id Invalid";
							}
							else {
								try {
									Integer isActive = Integer.parseInt(status) == 1 ? 0 : 1;
									Integer userIdTemp = Integer.parseInt(userIdString);
									Boolean result = ManagementUser.toggleUserStatus(isActive, userIdTemp, adminId);
									if(result)
										success = "Status changed";
									else error = "Something went wrong";
								}catch(Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					else {
						error = "You are not authorized to update User Status";
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(operation.equals("DeleteUser")) {
				Boolean access = false;
				try {
					access = Roles.authorized("DeleteUser", userId);
					if(userId == 0 || access) {
						String userIdString = request.getParameter("userId");
						String loggedInUserId = (String) session.getAttribute("userId");
						Boolean controlDelete = true;
						if(loggedInUserId != null) {
							if(userIdString == loggedInUserId) {
								control = false;
							}
						}
						if(userIdString != null && controlDelete) {
							if(!userIdString.matches("[0-9]+")) {
								error = "Invalid User Id";
							}
							else {
								try {
									Integer userIdTemp = Integer.parseInt(userIdString);
									Boolean result = ManagementUser.delete(userIdTemp, adminId);
									if(result)
										success = "User deleted successfully";
									else error = "Something went wrong";
								}catch(Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					else {
						error = "You are not authorized to delete a User";
					}
				}catch(Exception e) {
					e.printStackTrace();
				}

			}
			
		}
		else {
			error = "operation attribute null";
		}
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
		
	}
	
	public static boolean add(String username, String password, Integer adminId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "INSERT INTO `Users` VALUES (NULL, ?, ?, ?, 1)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		st.setString(2, username);
		st.setString(3, password);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

	public static boolean toggleUserStatus(Integer status, Integer userId, Integer adminId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "UPDATE Users SET isActive = ? WHERE userId = ? AND administratorId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, status);
		st.setInt(2, userId);
		st.setInt(3, adminId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	public static boolean delete(Integer userId, Integer adminId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "DELETE FROM Users WHERE userId = ? AND administratorId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, userId);
		st.setInt(2, adminId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static ArrayList<JSONObject> getAllUserDetails(Integer adminId, Integer userId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		System.out.println("userId: " + userId);
		String sql = "select userId, username, isActive from `Users` where administratorId = ? AND userId <> ? ORDER BY userId DESC";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		st.setInt(2,  userId);
		ResultSet rs = st.executeQuery();
		ArrayList<JSONObject> users = new ArrayList<JSONObject>();
		while(rs.next()) {
			JSONObject json = new JSONObject();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
	        users.add(json);
		}
		return users;
	}
	
	public static JSONObject getDetails(Integer userId) throws SQLException, ClassNotFoundException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select administratorId, userId, username from `Users` where userId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, userId);
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
	
	
	public static Integer login(String username, String password) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select userId from `Users` WHERE username = ? AND password = ? AND isActive = 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, username);
		st.setString(2, password);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer userId = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		System.out.println("userId from db: " + userId);
		return userId;
	}

	public static boolean exists(String username) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "SELECT COUNT(userId) FROM `Users` WHERE username = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, username);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer records = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return records > 0 ? true : false;
	}
	
	public static Integer count(Integer adminId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select COUNT(userId) from `Users` where administratorId = ? ";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer users = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return users;
	}
	
	public static Integer countActiveUsers(Integer adminId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select COUNT(userId) from `Users` where administratorId = ? AND isActive = 1";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, adminId);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer users = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return users;
	}
	
	
	
}
