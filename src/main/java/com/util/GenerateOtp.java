package com.util;

public class GenerateOtp {
	public static String create(Integer size) {
		String otpSequence = "";
		while(otpSequence.length() != size) {
			Integer otp = (int)(Math.pow(10, size)*Math.random());
			otpSequence = otp.toString();
		}
		
		return otpSequence;
	}
}