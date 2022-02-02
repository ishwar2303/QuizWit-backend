package com.mail;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.admin.Admin;
import com.config.Headers;
import com.config.Origin;
import com.student.Student;
import com.util.GenerateOtp;
import com.util.Validation;

@WebServlet("/EmailVerification")
public class EmailVerification extends HttpServlet {
	
	private static final long serialVersionUID = 4652855784469428732L;

	private static final String sendFrom = "";
	private static final String password = "";
	private static final String host = "smtp.gmail.com";
	
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String email = req.getParameter("email");
		String user = req.getParameter("user");
		String error = "";
		String success = "";
		String otpResponse = "";
		HttpSession session = req.getSession();
		Admin admin = new Admin();
		Student student = new Student();
		if(email == "")
			error = "E-mail required";
		else if(!Validation.email(email))
			error = "Invalid E-mail";
		else if(user == null) {
			error = "Select your role";
		}
		else if(!user.matches("[12]")) {
			error = "Invalid role";
		}
		else {
			try {
				Boolean result = false;
				if(user.equals("1")) {
					result = admin.exists(email);
				}
				else {
					result = student.exists(email);
				}
				if(!result) {
					otpResponse = EmailVerification.sendOtp(email);
					session.setAttribute("verifiedEmail", false);
					session.setAttribute("verifiedEmailDesc", email);
				}
				else {
					error = "Account with this E-mail already exists";
				}
				if(otpResponse != "") {
					success = "OTP sent successfully";
					session.setAttribute("emailOtp", otpResponse);
					System.out.println("otp set in session " + session.getAttribute("emailOtp"));
				}

				
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		JSONObject json = new JSONObject();
		
		json.put("success", success);
		json.put("error", error);
		Headers.setRequiredHeaders(res, Origin.getQuizWit());
		PrintWriter printWriter = res.getWriter();
		printWriter.println(json.toString());
	}
	
	public static String sendOtp(String to) {
		String otpString = "";
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sendFrom, password);			
			}
		});
		
		try {
			MimeMessage message = new MimeMessage(session); 
			message.setFrom(new InternetAddress(sendFrom));
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		    message.setSubject("QuizWit E-mail Verfication");
		    otpString = GenerateOtp.create(6);
		    String msgString = "Your OTP for email verification is " + otpString; 
		    message.setText(msgString);
		    // send the message
		    Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return otpString;
	}
}