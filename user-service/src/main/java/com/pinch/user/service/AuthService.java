/**
 * 
 */
package com.pinch.user.service;

import com.pinch.user.entity.UserEntity;
import com.pinch.core.user.dto.UserProfileDto;
import com.pinch.core.user.request.dto.EmailOtpValidateRequestDto;
import com.pinch.core.user.request.dto.EmailVerificationRequestDto;
import com.pinch.core.user.request.dto.LoginRequestDto;
import com.pinch.core.user.request.dto.OtpValidateRequestDto;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
public interface AuthService {

	void login(LoginRequestDto loginRequestDto);

	UserProfileDto validateOtp(OtpValidateRequestDto otpValidateRequestDto);

	void resendOtp(LoginRequestDto loginRequestDto);

	void sendEmailOtp(EmailVerificationRequestDto emailVerificationRequestDto);

	UserEntity validateEmailVerificationOtpAndUpdateUserDetails(EmailOtpValidateRequestDto emailOtpValidateRequestDto);

	void resendEmailOtp(EmailVerificationRequestDto emailVerificationRequestDto);

}