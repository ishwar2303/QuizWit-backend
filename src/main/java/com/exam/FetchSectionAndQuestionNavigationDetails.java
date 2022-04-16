package com.exam;

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

import com.admin.Exam;
import com.admin.Section;
import com.admin.ViewExams;
import com.admin.ViewSections;
import com.config.Headers;
import com.config.Origin;
import com.database.StudentDatabaseConnectivity;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import com.questions.Question;
import com.student.Student;
import com.util.FisherYatesShuffle;

/**
 * Servlet implementation class FetchSectionAndQuestionNavigationDetails
 */
@WebServlet("/FetchSectionAndQuestionNavigationDetails")
public class FetchSectionAndQuestionNavigationDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getExam());
		String examIdString = request.getParameter("examId");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		JSONObject json = new JSONObject();
		String success = "";
		String error = "";
		
		Student student = new Student();
		
		Boolean control = true;
		JSONObject errorLog = new JSONObject();
		Long currentTime = System.currentTimeMillis()/1000;
		

		if(session.getAttribute("attemptId") != null) { 
			Integer studentId = (Integer) session.getAttribute("studentId");
			Integer examId = (Integer) session.getAttribute("examId"); 
			Integer attemptId = (Integer) session.getAttribute("attemptId");
			
			try {
				ArrayList<JSONObject> sections = ViewSections.fetchAllSections(examId);
				ArrayList<JSONObject> navigationData = new ArrayList<JSONObject>();
				for(int i=0; i<sections.size(); i++) {
					JSONObject tempSection = new JSONObject();
					JSONObject section = sections.get(i);
					tempSection.put("title", section.get("title"));
					Integer sectionId = Integer.parseInt((String) section.get("sectionId"));
					ArrayList<JSONObject> questions = FetchSectionAndQuestionNavigationDetails.fetchNavigationInfoOfQuestions(attemptId, sectionId);
					tempSection.put("questions", questions);
					Integer duration = (int) (Attempt.getSectionEndTime(sectionId, attemptId) - System.currentTimeMillis()/1000);
					tempSection.put("duration", duration);
					tempSection.put("sectionId", sectionId);
					navigationData.add(tempSection);
				}
				json.put("sections", navigationData);
				success = "Navigation data fetched successfully";
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				error = "Something went wrong while preparing navigation for exam";
			}
			
		}
		else {
			error = "Invalid attempt";
		}

		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		out.println(json.toString());
	}

	public static ArrayList<JSONObject> fetchNavigationInfoOfQuestions(Integer attemptId, Integer sectionId) throws ClassNotFoundException, SQLException {
		StudentDatabaseConnectivity sdc = new StudentDatabaseConnectivity();
		Connection con = sdc.connection();
		String sql = "select q.questionId as questionId, SUBSTRING(q.question, 1, 35) as question, score, negative, markedAsReview, attempted from QuestionNavigation qn inner join Questions q on q.questionId = qn.questionId where attemptId = ? and q.sectionId = ?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, attemptId);
		st.setInt(2, sectionId);
		ResultSet rs = st.executeQuery();
		Integer id = 0;
		ArrayList<JSONObject> questions = new ArrayList<JSONObject>();
		while(rs.next()) {
			JSONObject json = new JSONObject();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        for (int j = 1; j <= rsmd.getColumnCount(); j++) {
	            json.put(rs.getMetaData().getColumnLabel(j), rs.getString(j));
	        }
	        questions.add(json);
		}
		rs.close();
		st.close();
		con.close();
		return questions;
	}
	


}
