/**
 * 
 */
package com.pinch.user.controller;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pinch.user.acl.service.AclService;
import com.pinch.user.adapters.UserAdapter;
import com.pinch.user.entity.UserEntity;
import com.pinch.user.entity.UserSessionEntity;
import com.pinch.user.service.AuthService;
import com.pinch.user.service.SessionService;
import com.pinch.core.base.common.dto.ResponseDto;
import com.pinch.core.base.constants.SecurityConstants;
import com.pinch.core.base.utils.ObjectMapperUtil;
import com.pinch.core.base.utils.SecureCookieUtil;
import com.pinch.core.base.utils.PinchUtils;
import com.pinch.core.user.acl.dto.AclUserDto;
import com.pinch.core.user.dto.UserProfileDto;
import com.pinch.core.user.request.dto.EmailOtpValidateRequestDto;
import com.pinch.core.user.request.dto.EmailVerificationRequestDto;
import com.pinch.core.user.request.dto.LoginRequestDto;
import com.pinch.core.user.request.dto.OtpValidateRequestDto;

import lombok.extern.log4j.Log4j2;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
@Log4j2
@RestController
@RequestMapping("auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private AclService aclService;

	@PostMapping("login")
	public ResponseDto<Void> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {

		authService.login(loginRequestDto);

		return ResponseDto.success("OTP Sent for Login");
	}
	
	@PostMapping("validateOtp")
	public ResponseDto<AclUserDto> validateOtp(
			@RequestBody @Valid OtpValidateRequestDto otpValidateRequestDto, HttpServletRequest request, HttpServletResponse response) {

		UserProfileDto userProfileDto = authService.validateOtp(otpValidateRequestDto);

		log.info("OTP Successfully Validated for User: " + userProfileDto.getUuid() + ". Creating User Session now");

		String token = PinchUtils.generateUniqueId();

		UserSessionEntity userSessionEntity = sessionService.createUserSession(userProfileDto, token);

		if (Objects.nonNull(userSessionEntity)) {
			addTokenToResponse(request, response, token);
			return ResponseDto.success("User Login Successfull", UserAdapter.getAclUserDto(userProfileDto, aclService.getUserDeptLevelRoleNameUrlExpandedDtoFe(userProfileDto.getUuid())));
		}

		return ResponseDto.failure("Failed to create user session");
	}
	
	@PostMapping("sendEmailVerificationOtp")
	public ResponseDto<Void> sendEmailVerificationOtp(@RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto) {

		log.info("Request received to send otp for email verification: {}", emailVerificationRequestDto);
		
		authService.sendEmailOtp(emailVerificationRequestDto);

		return ResponseDto.success("Email OTP Sent");
	}
	
	@PostMapping("resendEmailVerificationOtp")
	public ResponseDto<Void> resendEmailVerificationOtp(@RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto) {

		log.info("Request received to re-send otp for email verification: {}", emailVerificationRequestDto);
		
		authService.resendEmailOtp(emailVerificationRequestDto);

		return ResponseDto.success("OTP Successfully Resent");
	}
	
	@PostMapping("validateEmailVerificationOtp")
	public ResponseDto<String> validateEmailVerificationOtp(@RequestBody EmailOtpValidateRequestDto emailOtpValidateRequestDto) {

		log.info("Request received to validate otp for email verification and update Deatils: {}", ObjectMapperUtil.getString(emailOtpValidateRequestDto));
		
		UserEntity userEntity = authService.validateEmailVerificationOtpAndUpdateUserDetails(emailOtpValidateRequestDto);
		
		log.info("Email OTP Successfully verified for User: " + userEntity.getUuid());

		return ResponseDto.success("Email OTP Successfully verified for User: " + userEntity.getUuid() + " with Email: " + userEntity.getEmail() + " and User Details Updated Successfully.");
	}

	@PostMapping("resendOtp")
	public ResponseDto<Void> resendOtp(@RequestBody @Valid LoginRequestDto loginRequestDto) {

		authService.resendOtp(loginRequestDto);

		return ResponseDto.success("OTP Successfully Resent");
	}

	@GetMapping("logout")
	public ResponseDto<Void> logout(
			@CookieValue(name = SecurityConstants.TOKEN_HEADER_NAME) String token,
			HttpServletRequest request, HttpServletResponse response) {

		String userId = request.getParameter(SecurityConstants.USER_ID);

		log.info("Logout requested for user: " + userId);

		sessionService.removeUserSession(token);

		SecureCookieUtil.handleLogOutResponse(request, response);

		return ResponseDto.success("Successfully Logged Out");
	}

	private void addTokenToResponse(HttpServletRequest request, HttpServletResponse response, String token) {

		if (StringUtils.isNotBlank(token)) {

			String frontEnv = request.getHeader(SecurityConstants.FRONT_ENVIRONMENT);
			boolean isLocalFrontEnd = StringUtils.isNotBlank(frontEnv) && SecurityConstants.FRONT_ENVIRONMENT_LOCAL.equals(frontEnv);

			String appEnv = request.getHeader(SecurityConstants.APP_ENVIRONMENT);
			boolean isApp = StringUtils.isNotBlank(appEnv) && SecurityConstants.APP_ENVIRONMENT_TRUE.equals(appEnv);

			response.addCookie(SecureCookieUtil.create(SecurityConstants.TOKEN_HEADER_NAME, token, Optional.of(isLocalFrontEnd), Optional.of(isApp)));
		}
	}
}