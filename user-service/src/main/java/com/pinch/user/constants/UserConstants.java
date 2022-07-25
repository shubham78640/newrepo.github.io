/**
 * 
 */
package com.pinch.user.constants;

import lombok.experimental.UtilityClass;

/**
 * @author tech
 *
 * @date 12-Oct-2019
 */
@UtilityClass
public class UserConstants {

	public static final String DEFAULT_OTP_TEXT = "<otp> is OTP to verify your phone number for accessing pinch Living.";

	public static final String MOBILE_VERIFICATION_OTP_TEXT = "<otp> is OTP to verify your phone number with pinch Living";

	public static final String EMAIL_VERIFICATION_OTP_TEXT = "Hi <residentName>" + System.lineSeparator() + "The OTP to verify your email with pinch Living is <otp>." + System.lineSeparator() + "Please do not share this anyone including pinch employees.";

	public static final String USER_VERIFICATION_OTP_TEXT = "<otp> is your OTP to authenticate with pinch Living";

	public static final String DELIMITER_KEY = ",";
	
	public static final int ZERO = 0;
}