package com.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.util.FisherYatesShuffle;


@WebServlet("/ShuffleQuestion")
public class ShuffleQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		out.println();
		Integer arr[] = {
	            new Integer(1),
	            new Integer(2),
	            new Integer(3),
	            new Integer(4),
	            new Integer(5),
	            new Integer(6),
	            new Integer(7),
	            new Integer(8),
	             new Integer(9),
	            new Integer(10)
	        };
	        ArrayList<Integer> questions = new ArrayList<Integer>(Arrays.asList(arr));
	        questions = FisherYatesShuffle.shuffle(questions);
	        for(Integer questionId : questions) {
	        	out.print(questionId + ", ");
	        }
	}


}
