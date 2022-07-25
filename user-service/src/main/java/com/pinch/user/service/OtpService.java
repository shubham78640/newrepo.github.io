/**
 * 
 */
package com.pinch.user.service;

import com.pinch.user.entity.UserEntity;
import com.pinch.core.user.enums.OtpType;
import com.pinch.core.user.request.dto.EmailOtpValidateRequestDto;
import com.pinch.core.user.request.dto.EmailVerificationRequestDto;
import com.pinch.core.user.request.dto.LoginRequestDto;
import com.pinch.core.user.request.dto.MobileEmailOtpRequestDto;
import com.pinch.core.user.request.dto.MobileOtpRequestDto;
import com.pinch.core.user.request.dto.OtpValidateRequestDto;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
public interface OtpService {

	void sendLoginOtp(UserEntity userEntity);

	void validateLoginOtp(OtpValidateRequestDto otpValidateRequestDto);

	void validateMobileOtp(String mobile, String isoCode, String otp, OtpType otpType);

	void resendLoginOtp(LoginRequestDto loginRequestDto);

	void resendMobileOtp(String mobile, String isoCode, OtpType otpType);

	void sendMobileOtp(MobileOtpRequestDto mobileOtpRequestDto);

	void sendOtp(MobileEmailOtpRequestDto mobileEmailOtpRequestDto);

	void sendEmailOtp(UserEntity userEntity, String email);

	void validateEmailVerificationOtp(EmailOtpValidateRequestDto emailOtpValidateRequestDto);

	void resendEmailVerificationOtp(EmailVerificationRequestDto emailVerificationRequestDto, UserEntity userEntity);
}