package com.config;

public class Origin {
	private static String QuizWit = "http://localhost:3000";
	private static String Admin = "http://localhost:3001";
	private static String Exam = "http://localhost:3002";
	private static String Student = "http://localhost:3003";
	
	public static String getQuizWit() {
		return QuizWit;
	}
	public static String getAdmin() {
		return Admin;
	}
	public static String getExam() {
		return Exam;
	}
	public static String getStudent() {
		return Student;
	}
}
