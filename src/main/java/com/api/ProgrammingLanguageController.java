
package com.api;

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

import com.config.Headers;
import com.config.Origin;
import com.database.AdminDatabaseConnectivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.ProgrammingLanguage;


@WebServlet("/ProgrammingLanguageController")
public class ProgrammingLanguageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArrayList<ProgrammingLanguage> list = new ArrayList<ProgrammingLanguage>();
		try {
			list = getProgrammingLanguages();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		PrintWriter out = response.getWriter();
		out.println(mapper.writeValueAsString(list));
	}
	
	private ArrayList<ProgrammingLanguage> getProgrammingLanguages() throws ClassNotFoundException, SQLException {
		
		AdminDatabaseConnectivity dao = new AdminDatabaseConnectivity();
		Connection con = dao.connection();
		
		String sql = "SELECT * FROM ProgrammingLanguage";
		PreparedStatement st = con.prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		
		ArrayList<ProgrammingLanguage> list = new ArrayList<ProgrammingLanguage>();
		while(rs.next()) {
			ProgrammingLanguage language = new ProgrammingLanguage();
			language.setLanguageId(rs.getLong("languageId"));
			language.setCode(rs.getString("code"));
			language.setVersion(rs.getString("version"));
			language.setDescription(rs.getString("description"));
			list.add(language);
		}
		
		return list;
		
	}

}
