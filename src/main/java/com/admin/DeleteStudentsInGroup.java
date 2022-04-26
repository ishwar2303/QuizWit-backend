package com.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.config.Headers;
import com.config.Origin;
import com.util.Validation;

/**
 * Servlet implementation class DeleteStudentsInGroup
 */
@WebServlet("/DeleteStudentsInGroup")
public class DeleteStudentsInGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		Headers.setRequiredHeaders(response, Origin.getAdmin());
		Integer adminId = Integer.parseInt((String) session.getAttribute("administratorId"));
		Integer userId  = Integer.parseInt((String) session.getAttribute("userId"));
		
		String success = "", error = "";
		JSONObject errorLog = new JSONObject();
		
		if(adminId == null)
			return;
		
		try {
			Boolean access = Roles.authorized("DeleteStudentFromExamGroup", userId);
			if(userId == 0 || access) {
				Boolean control = false;
				String groupIdString = request.getParameter("groupId");
				String email = request.getParameter("email");
				String confirmEmail = request.getParameter("confirmEmail");
				if(groupIdString != null) {
					String[] groupIds = groupIdString.split(",");
					for(int i=0; i<groupIds.length; i++) {
						if(Validation.onlyDigits(groupIds[i])){
							Integer groupId = Integer.parseInt(groupIds[i]);
							Integer examId = StudentGroup.getExamId(groupId);
							if(examId != 0) {
								if(CreateExam.examExists(adminId, examId)) {
									StudentGroup.delete(groupId);
									control = true;
								}
							}
						}
					}
					if(control)
						success = "Deleted successfully";
					else error = "Something went wrong";
				}
				else error = "Please fill required fields appropriately";
				
			}
			else {
				error = "Access not granted";
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		JSONObject json = new JSONObject();
		json.put("success", success);
		json.put("error", error);
		json.put("errorLog", errorLog);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
	}

}
