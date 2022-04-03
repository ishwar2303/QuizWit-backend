package com.quizwit;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.admin.Admin;
import com.admin.ManagementUser;
import com.config.Headers;
import com.config.Origin;
import com.database.AdminDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import com.student.Student;
import com.util.Validation;


@WebServlet("/LoginAdmin")
public class LoginAdmin extends HttpServlet {
	
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		
		String user = request.getParameter("user");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		
		System.out.println("user: "+ user);
		Admin admin = new Admin();
		Student student = new Student();
		
		Boolean control = true;
		JSONObject errorLog = new JSONObject();
		if(user == null) {
			errorLog.put("user", "Please select role");
		}
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		if(email != null && password != null && user != null) {
			

			if(!user.matches("[12]")) {
				control = false;
				errorLog.put("user", "Invalid Role");
			}
			
			if(email == "") {
				control = false;
	 			errorLog.put("email", "E-mail/Username required");
			}
			else if(user.equals("1")) {
				try {
					if(!admin.exists(email)) {
						control = false;
						errorLog.put("email", "E-mail not registered");
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			if(password == "") {
				control = false;
	 			errorLog.put("password", "Password required");
			}
			
			if(control) {
				Integer id = 0;
				if(user.equals("1")) { // Admin
					try {
						id = Admin.login(email, password); // returns administratorId
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
				else { // Management User
					try {
						id = ManagementUser.login(email, password); // returns userId
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				if(id != 0) {
					JSONObject details = new JSONObject();
					if(user.equals("1")) { // Admin
						try {
							LoginAdmin.setAdminRoles(request);
							details = admin.details(id);
							session.setAttribute("Administrator", true);
							session.setAttribute("userId", "0");
							details.put("userType", "Administrator");
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
					else { // Management User
						try {
							LoginAdmin.setManagementUserRoles(request, id);
							details = ManagementUser.getDetails(id);
							details.put("fullName", details.get("username"));
							details.put("userType", "Management User");
							session.setAttribute("userId", details.get("userId"));
							session.setAttribute("ManagementUser", true);
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
					System.out.println(details);
					session.setAttribute("administratorId", details.get("administratorId"));
					// set info in session
					session.setAttribute("loggedIn", true);
					session.setAttribute("details", details);
				}
				else {
					error = "Invalid credentials or Your account is block <br/> Contact your administrator if issue persist.";
				}
			}
			else {
				error = "Please fill required fields appropriately";
			}
		}
		else {
			error = "Please fill required fields appropriately | check name attributes in request";
		}
		
		if(session.getAttribute("loggedIn") != null && (boolean) session.getAttribute("loggedIn")) { // user logged in
			json.put("details", (JSONObject) session.getAttribute("details"));
			success = "Logged in successfully";
		}
		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		out.println(json.toString());
	}
	
	
	public static void setAdminRoles(HttpServletRequest request) throws SQLException, ClassNotFoundException {
		HttpSession session = request.getSession();
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();
		
		String sql = "select code from `UserRoles`";
		PreparedStatement st = con.prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		while(rs.next()) {
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	        	session.setAttribute(rs.getString(j), true);
	        }
		}
		rs.close();
		st.close();
		con.close();
	}
	
	public static void setManagementUserRoles(HttpServletRequest request, Integer userId) throws ClassNotFoundException, SQLException {
		HttpSession session = request.getSession();
		AdminDatabaseConnectivity adc = new AdminDatabaseConnectivity();
		Connection con = adc.connection();

		String sql = "SELECT code FROM AssignedRolesToUsers a INNER JOIN UserRoles u on a.roleId = u.roleId WHERE userId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, userId);
		ResultSet rs = st.executeQuery();
		while(rs.next()) {
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	        	session.setAttribute(rs.getString(j), true);
	        }
		}
		rs.close();
		st.close();
		con.close();
	}
}
