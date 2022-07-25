/**
 * 
 */
package com.pinch.user.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pinch.user.adapters.UserAdapter;
import com.pinch.user.db.service.UserDbService;
import com.pinch.user.entity.UserEntity;
import com.pinch.user.service.AuthService;
import com.pinch.user.service.OtpService;
import com.pinch.core.base.exception.ApiValidationException;
import com.pinch.core.base.exception.AuthException;
import com.pinch.core.kafka.dto.KafkaDTO;
import com.pinch.core.kafka.producer.NotificationProducer;
import com.pinch.core.user.constants.UserErrorCodes;
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
@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private OtpService otpService;

	@Autowired
	private UserDbService userDbService;
	
	@Value("${kafka.resident.detail.topic}")
	private String kafkaResidentDetailTopic;
	
	@Autowired
	private NotificationProducer notificationProducer;

	@Override
	public void login(LoginRequestDto loginRequestDto) {

		UserEntity userEntity = getActiveUser(loginRequestDto);

		otpService.sendLoginOtp(userEntity);

		log.info("OTP sent for User: " + userEntity.getUuid() + " for Login");

	}

	private UserEntity getActiveUser(LoginRequestDto loginRequestDto) {

		UserEntity userEntity = userDbService.getUserForMobile(loginRequestDto.getMobile(), loginRequestDto.getIsoCode());

		// userEntity = createUserIfUserIsConsumer(loginRequestDto, userEntity);

		if (Objects.isNull(userEntity)) {
			throw new AuthException("No user exists with this number", UserErrorCodes.USER_NOT_EXISTS);
		}

		if (!userEntity.isStatus()) {
			throw new AuthException("The booking is disabled for this number", UserErrorCodes.USER_ACCOUNT_INACTIVE);
		}

		log.info("Found User: " + userEntity.getUuid() + " for Mobile: " + loginRequestDto.getMobile() + " of Type: " + userEntity.getUserType());

		return userEntity;
	}

	@Override
	public UserProfileDto validateOtp(OtpValidateRequestDto otpValidateRequestDto) {

		UserEntity userEntity = getActiveUser(otpValidateRequestDto);

		otpService.validateLoginOtp(otpValidateRequestDto);

		log.info("OTP verification completed for User: " + userEntity.getUuid());

		userEntity.setMobileVerified(true);

		userDbService.update(userEntity);

		return UserAdapter.getUserProfileDto(userEntity);
	}

	@Override
	public void resendOtp(LoginRequestDto loginRequestDto) {

		otpService.resendLoginOtp(loginRequestDto);
	}

	@Override
	public void sendEmailOtp(EmailVerificationRequestDto emailVerificationRequestDto) {

		UserEntity userEntity = getActiveUserByUuid(emailVerificationRequestDto.getUserUuid());

		otpService.sendEmailOtp(userEntity, emailVerificationRequestDto.getEmail());

		log.info("OTP sent for User: " + userEntity.getUuid() + " for Email Verification To Email id: " + emailVerificationRequestDto.getEmail());
	}

	private UserEntity getActiveUserByUuid(String userUuid) {
		
		UserEntity userEntity = userDbService.findByUuid(userUuid);

		if (Objects.isNull(userEntity)) {
			
			throw new ApiValidationException("User Not Found with Uuid: " + userUuid);
		}

		if (!userEntity.isStatus()) {
			
			throw new ApiValidationException("User Account is Disabled for Uuid " + userUuid);
		}
		
		log.info("Found User: " + userEntity.getUuid() + " of Type: " + userEntity.getUserType());
		
		return userEntity;
	}

	@Override
	public UserEntity validateEmailVerificationOtpAndUpdateUserDetails(EmailOtpValidateRequestDto emailOtpValidateRequestDto) {

		UserEntity userEntity = getActiveUserByUuid(emailOtpValidateRequestDto.getUserUuid());

		otpService.validateEmailVerificationOtp(emailOtpValidateRequestDto);

		log.info("OTP verification completed for User: " + userEntity.getUuid());

		userEntity.setEmail(emailOtpValidateRequestDto.getEmail());
		
		userEntity.setEmailVerified(true);
		
		if (Objects.nonNull(emailOtpValidateRequestDto.getFirstName()))
			userEntity.getUserProfile().setFirstName(emailOtpValidateRequestDto.getFirstName());

		if (Objects.nonNull(emailOtpValidateRequestDto.getLastName()))
			userEntity.getUserProfile().setLastName(emailOtpValidateRequestDto.getLastName());
		
		userEntity = userDbService.update(userEntity);
		
		UserProfileDto userProfileDto = UserAdapter.getUserProfileDto(userEntity);
		
		KafkaDTO kafkaDTO = new KafkaDTO();
		kafkaDTO.setData(userProfileDto);

		notificationProducer.publish(kafkaResidentDetailTopic, KafkaDTO.class.getName(), kafkaDTO);


		return userEntity;
	}

	@Override
	public void resendEmailOtp(EmailVerificationRequestDto emailVerificationRequestDto) {
		
		UserEntity userEntity = getActiveUserByUuid(emailVerificationRequestDto.getUserUuid());
		
		otpService.resendEmailVerificationOtp(emailVerificationRequestDto, userEntity);		
	}
}