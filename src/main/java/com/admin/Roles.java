package com.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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

@WebServlet("/Roles")
public class Roles extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		HttpSession session = request.getSession();
		String roleType = request.getParameter("roleType");
		String userIdString = request.getParameter("userId");
		System.out.println(roleType);
		System.out.println(userIdString);
		Integer userId = 0;
		String success = "";
		String error = "";
		JSONObject json = new JSONObject();
		Boolean control = true;
		if(userId != null) {
			try {
				userId = Integer.parseInt(userIdString);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			control = false;
			error = "User Id required";
		}
		if(roleType != null) {
			if(!roleType.equals("Assigned") && !roleType.equals("Available") && !roleType.equals("AssignedAvailable")) {
				control = false;
				error = "Invalid role type";
			}
		}
		else {
			control = false;
			error = "Role Type required";
		}
		if(control) {

			if(roleType.equals("Available")) {
				ArrayList<JSONObject> roles = new ArrayList<JSONObject>();
				try {
					roles = Roles.getAllAvailableRoles(userId);
					success = "Roles Fetched Successfully";
					json.put("roles", roles);
					
				}catch(Exception e) {
					error = "Something went wrong while fetching roles of user";
					e.printStackTrace();
				}
			}
			else if(roleType.equals("Assigned")) {
				ArrayList<JSONObject> roles = new ArrayList<JSONObject>();
				try {
					roles = Roles.getAllAssignedRoles(userId);
					success = "Roles Fetched Successfully";
					json.put("roles", roles);
					
				}catch(Exception e) {
					error = "Something went wrong while fetching roles of user";
					e.printStackTrace();
				}
			}
			else if(roleType.equals("AssignedAvailable")) {
				try {
					ArrayList<JSONObject> roles = new ArrayList<JSONObject>();
					roles = Roles.getAllAssignedRoles(userId);
					json.put("assignedRoles", roles);
					roles = Roles.getAllAvailableRoles(userId);
					json.put("availableRoles", roles);
					success = "Roles Fetched Successfully";
					
				} catch(Exception e) {
					error = "Something went wrong while fetching roles of user";
					e.printStackTrace();
				}
			}
		}
		json.put("success", success);
		json.put("error", error);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		HttpSession session = request.getSession();
		String userIdString = request.getParameter("userId");
		String rolesString = request.getParameter("rolesString");
		List<String> roles = new ArrayList<String>();
		Integer userId = 0;
		String success = "";
		String error = "";
		JSONObject json = new JSONObject();
		Boolean control = true;
		
		if(rolesString != null) {
			String[] rolesStringArray = rolesString.split(",");
			roles = Arrays.asList(rolesStringArray);
		}
		else {
			control = false;
			error = "Roles required";
		}
		if(userId != null) {
			try {
				userId = Integer.parseInt(userIdString);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			control = false;
			error = "User Id required";
		}

		Integer count = 0;
		if(control) {
			try {
				Roles.revokeRoles(userId);
				for(int i=0; i<roles.size(); i++) {
					try {
						System.out.println("roleId: " + roles.get(i));
						Boolean result = Roles.addRole(Integer.parseInt(roles.get(i)), userId);
						if(result)
							count++;
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			success = count + " roles are assigned to user";
		}
	
		json.put("success", success);
		json.put("error", error);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
		
	}
	
	public static boolean authorized(String roleCode, Integer userId) throws SQLException, ClassNotFoundException {
		
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "select count(ur.roleId) from AssignedRolesToUsers a inner join userroles ur on ur.roleId = a.roleId where a.userId = ? AND ur.code = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, userId);
		st.setString(2, roleCode);
		ResultSet rs = st.executeQuery();
		rs.next();
		Integer records = rs.getInt(1);
		rs.close();
		st.close();
		con.close();
		return records > 0 ? true : false;
	}

	public static ArrayList<JSONObject> getAllAssignedRoles(Integer userId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		System.out.println("userId: " + userId);
		String sql = "select ur.roleId as roleId, ur.code as code from AssignedRolesToUsers a inner join userroles ur on ur.roleId = a.roleId where a.userId = ? ORDER BY ur.code ASC";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1,  userId);
		ResultSet rs = st.executeQuery();
		ArrayList<JSONObject> roles = new ArrayList<JSONObject>();
		while(rs.next()) {
			JSONObject json = new JSONObject();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
	        roles.add(json);
		}
		return roles;
	}
	
	public static ArrayList<JSONObject> getAllAvailableRoles(Integer userId) throws ClassNotFoundException, SQLException {

		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		System.out.println("userId: " + userId);
		String sql = "select roleId, code from UserRoles Where roleId not in ( select ur.roleId as roleId from AssignedRolesToUsers a inner join userroles ur on ur.roleId = a.roleId where a.userId = ? ) ORDER BY code ASC";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1,  userId);
		ResultSet rs = st.executeQuery();
		ArrayList<JSONObject> roles = new ArrayList<JSONObject>();
		while(rs.next()) {
			JSONObject json = new JSONObject();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
	        roles.add(json);
		}
		return roles;
	}
	
	public static Boolean revokeRoles(Integer userId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "delete from AssignedRolesToUsers where userId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, userId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}
	
	public static Boolean addRole(Integer roleId, Integer userId) throws ClassNotFoundException, SQLException {
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		String sql = "INSERT INTO `AssignedRolesToUsers` VALUES (NULL, ?, ?)";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, roleId);
		st.setInt(2, userId);
		Integer count = st.executeUpdate();
		st.close();
		con.close();
		return count > 0 ? true : false;
	}

}
