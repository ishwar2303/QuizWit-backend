package com.exam;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.admin.Exam;
import com.admin.ViewExams;
import com.admin.ViewSections;
import com.config.Headers;
import com.config.Origin;

/**
 * Servlet implementation class FetchExamDetails
 */
@WebServlet("/FetchExamDetails")
public class FetchExamDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();

		Headers.setRequiredHeaders(response, Origin.getExam());
		JSONObject json = new JSONObject();
		JSONObject exam = new JSONObject();
		ArrayList<JSONObject> sections = new ArrayList<JSONObject>();
		String success = "";
		String error = "";
		if(session.getAttribute("ExamLoggedIn") != null && (boolean) session.getAttribute("ExamLoggedIn")) { // user logged in
			Integer examId = (Integer) session.getAttribute("examId");
			Integer studentId = (Integer) session.getAttribute("studentId");
			try {
				exam = ViewExams.fetchExam(examId);
				Integer timeToStart = (int) (Long.parseLong((String) exam.get("startTime"))/1000 - System.currentTimeMillis()/1000);
				Long endTime = Long.parseLong((String) exam.get("endTime"))/1000;
				Long currentTime = System.currentTimeMillis()/1000;
				System.out.println(endTime);
				System.out.println("BC");
				System.out.println(currentTime);
				if(Long.compare(endTime, currentTime) < 0) {
					exam.put("endExam", true);
				}
				else {
					exam.put("endExam", false);
				}
				json.put("exam", exam);
				sections = ViewSections.fetchAllSections(examId);
				for(int i=0; i<sections.size(); i++) {
					JSONObject section = sections.get(i);
					Integer sectionId = Integer.parseInt((String) section.get("sectionId"));
					Integer sectionTimeDuration = Integer.parseInt((String) section.get("timeDuration"));
					Integer totalTimeOnQuestionsInASection = Exam.totalQuestionTimeInSectionOnQuestion(sectionId);
					section.put("timeDuration", totalTimeOnQuestionsInASection + sectionTimeDuration);
					sections.set(i, section);
				}
				exam.put("timeToStart", timeToStart);
				exam.put("sections", sections);
				Integer totalSectionTimeDuration = Exam.totalSectionTimerDuration(examId);
				Integer totalQuestionTimeDuration = Exam.totalQuestionTimeDuration(examId);
				Integer examTimeDuration = Integer.parseInt((String)exam.get("timeDuration"));
				exam.put("timeDuration", examTimeDuration + totalQuestionTimeDuration + totalSectionTimeDuration);
				Integer attemptId = Attempt.getAttemptId(examId, studentId);
				if(attemptId > 0) {
					JSONObject attempt = Attempt.details(attemptId);
					Integer examSubmitted = Integer.parseInt((String) attempt.get("examSubmitted"));
					exam.put("examSubmitted", examSubmitted == 1 ? true : false);
				}
				

				Integer numberOfAttempts = Integer.parseInt((String) exam.get("numberOfAttempts")); // number of attempts available
				Integer givenAttempts = Attempt.count(examId, studentId); // number of attempts given // submitted exam
				Integer yourAttempt = 0;
				if(givenAttempts < numberOfAttempts) {
					Integer lastAttemptId = Attempt.getAttemptId(examId, studentId); // exam going on
					if(lastAttemptId == 0) { // all submitted attempts
						if(givenAttempts + 1 <= numberOfAttempts) {
							exam.put("giveAttempt", true);
							exam.put("yourAttempt", givenAttempts + 1);
						}
						else {
							exam.put("giveAttempt", false);
							exam.put("yourAttempt", "No more attempts available");
						}
					}
					else {
						exam.put("giveAttempt", true);
						exam.put("yourAttempt", givenAttempts + 1);
					}
				}
				else {
					exam.put("giveAttempt", false);
					exam.put("yourAttempt", "No more attempts available");
				}
				success = "Exam Details fetched successfully";
			} catch(Exception e) {
				e.printStackTrace();
				error = "Something went wrong while fetching exam details";
			}
		}
		else {
			error = "Student not logged in";
		}

		PrintWriter out = response.getWriter();
		json.put("success", success);
		json.put("error", error);
		out.println(json.toString());
	}

}
