package com.pinch.user.controller.internal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pinch.user.service.OtpService;
import com.pinch.core.base.common.dto.ResponseDto;
import com.pinch.core.base.exception.AuthException;
import com.pinch.core.user.request.dto.MobileEmailOtpRequestDto;
import com.pinch.core.user.request.dto.MobileOtpRequestDto;
import com.pinch.core.user.request.dto.MobileOtpValidateRequestDto;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/internal/otp")
public class InternalOtpController {

	@Autowired
	private OtpService otpService;

	@PostMapping("mobile/request")
	public ResponseDto<Void> sendMobileOtp(@RequestBody @Valid MobileOtpRequestDto mobileOtpRequestDto) {

		log.info("Received request to send OTP: {}", mobileOtpRequestDto);

		otpService.sendMobileOtp(mobileOtpRequestDto);

		return ResponseDto.success("OTP sent to mobile");
	}

	@PostMapping("mobile/validate")
	public ResponseDto<Void> validateMobileOtp(@RequestBody @Valid MobileOtpValidateRequestDto mobileOtpValidateRequestDto) {

		log.info("Received request to validate OTP: {}", mobileOtpValidateRequestDto);

		try {

			otpService.validateMobileOtp(
					mobileOtpValidateRequestDto.getMobile(),
					mobileOtpValidateRequestDto.getIsoCode(),
					mobileOtpValidateRequestDto.getOtp(),
					mobileOtpValidateRequestDto.getOtpType());

			return ResponseDto.success("OTP Succefully Validated");

		} catch (AuthException e) {
			log.error(e.getMessage());
			return ResponseDto.failure(e.getMessage());
		}
	}

	@PostMapping("mobile/resend")
	public ResponseDto<Void> resendMobileOtp(@RequestBody @Valid MobileOtpRequestDto mobileOtpRequestDto) {

		log.info("Received request to resend OTP: {}", mobileOtpRequestDto);

		try {

			otpService.resendMobileOtp(mobileOtpRequestDto.getMobile(), mobileOtpRequestDto.getIsoCode(), mobileOtpRequestDto.getOtpType());

			return ResponseDto.success("OTP resent to mobile");

		} catch (AuthException e) {
			log.error(e.getMessage());
			return ResponseDto.failure(e.getMessage());
		}
	}

	@PostMapping("request")
	public ResponseDto<Void> sendMobileAndEmailOtp(@RequestBody @Valid MobileEmailOtpRequestDto mobileEmailOtpRequestDto) {

		log.info("Received request to send OTP: {}", mobileEmailOtpRequestDto);

		otpService.sendOtp(mobileEmailOtpRequestDto);

		return ResponseDto.success("OTP sent to mobile & email");
	}

}