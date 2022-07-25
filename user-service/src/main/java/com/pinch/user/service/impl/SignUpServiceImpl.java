package com.pinch.user.service.impl;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import com.pinch.user.acl.adapters.SignUpAdapter;
import com.pinch.user.adapters.UserAdapter;
import com.pinch.user.db.service.SignUpDbService;
import com.pinch.user.db.service.UserDbService;
import com.pinch.user.entity.OtpEntity;
import com.pinch.user.entity.SignupEntity;
import com.pinch.user.entity.UserEntity;
import com.pinch.user.kafka.service.KafkaUserService;
import com.pinch.user.service.SignUpService;
import com.pinch.user.service.UserService;
import com.pinch.core.base.PinchConstants;
import com.pinch.core.base.exception.AuthException;
import com.pinch.core.base.utils.PinchUtils;
import com.pinch.core.user.constants.UserErrorCodes.Otp;
import com.pinch.core.user.dto.UserDto;
import com.pinch.core.user.dto.UserProfileDto;
import com.pinch.core.user.enums.OtpType;
import com.pinch.core.user.request.dto.AddUserRequestDto;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SignUpServiceImpl implements SignUpService {

	@Value("${otp.length:4}")
	private int otpLength;

	@Value("${kafka.resident.detail.topic}")
	private String kafkaResidentDetailTopic;
	
	@Value("${environment.type}")
	private String environment;

	@Value("${test.mobile}")
	private String testMobile;
	
	

	@Autowired
	private KafkaUserService kafkaUserService;

	@Autowired
	private SignUpDbService signUpDbService;
	@Autowired
	private UserService userService;

	@Autowired
	private UserDbService userDbService;

	@Override
	public String signUpUser(AddUserRequestDto addUserRequestDto) {

		int otp = generateOtp(addUserRequestDto);

		addUserRequestDto.setSignupFlow(true);
		SignupEntity signUp = SignUpAdapter.getSignupEntity(addUserRequestDto, otp);

		signUp = signUpDbService.save(signUp);

		sendOtp(addUserRequestDto, signUp);

		return signUp.getUuid();
	}

	@Override
	public UserProfileDto validateSignUpOtp(String uuid, String otp) {

		SignupEntity signup = signUpDbService.findByUuidAndStatus(uuid, Boolean.TRUE);

		compareOTP(otp, signup);
		UserDto userDto = userService.addUser(signup.getSignupObject());
		if (Objects.isNull(userDto))
			throw new AuthException("user Not created.");

		UserEntity userEntity = userDbService.getUserForMobile(signup.getMobile(), PinchConstants.INDIA_ISO_CODE);

		return UserAdapter.getUserProfileDto(userEntity);
	}

	private boolean compareOTP(String otp, SignupEntity signup) {

		if (signup == null)
			throw new AuthException("No OTP exists for mobile", Otp.OTP_NOT_FOUND);

		if (!signup.isValidated() && signup.getOtp().toString().equals(otp)) {
			signup.setValidated(true);
			signUpDbService.save(signup);
		} else {
			throw new AuthException("Invalid OTP For User With Mobile " + signup.getMobile(), Otp.INVALID_OTP);
		}

		return Boolean.TRUE;
	}

	private void sendOtp(AddUserRequestDto addUserRequestDto, SignupEntity signUpOtp) {

		OtpEntity userOtp = OtpEntity.builder().otp(signUpOtp.getOtp()).mobile(signUpOtp.getMobile())
				.otpType(OtpType.MOBILE_VERIFICATION)
				.userType(addUserRequestDto.getUserType()).status(Boolean.TRUE).build();

		log.info("Sending Otp for Signup, {}", userOtp);

		kafkaUserService.sendOtpToKafka(userOtp, null);

	}

	private Integer generateOtp(AddUserRequestDto addUserRequestDto) {
		return isTestEnvironment() || isTestMobile(addUserRequestDto) ? PinchUtils.generateDefaultOtpOfLength(otpLength) : PinchUtils.generateOTPOfLength(otpLength);
	}

	private boolean isTestEnvironment() {
		return StringUtils.isNotBlank(environment)
				&& ("dev".equalsIgnoreCase(environment)
						|| "staging".equalsIgnoreCase(environment)
						|| "test".equalsIgnoreCase(environment));
	}

	private boolean isTestMobile(AddUserRequestDto addUserRequestDto) {

		if (StringUtils.isNotBlank(testMobile)
				&& StringUtils.isNotBlank(addUserRequestDto.getMobile())
				&& StringUtils.isNotBlank(addUserRequestDto.getIsoCode())) {

			String[] mobileNos = testMobile.split(",");

			Set<String> mobiles = Arrays.stream(mobileNos).collect(Collectors.toSet());

			return addUserRequestDto.getMobile().startsWith("2") || mobiles.contains(addUserRequestDto.getMobile());
		}

		return false;
	}
	
}
