/**
 * 
 */
package com.pinch.user.kafka.service.impl;

import com.pinch.core.user.acl.dto.RoleDto;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.pinch.user.constants.NotificationKeys;
import com.pinch.user.constants.UserConstants;
import com.pinch.user.entity.OtpEntity;
import com.pinch.user.entity.UserEntity;
import com.pinch.user.entity.UserProfileEntity;
import com.pinch.user.kafka.service.KafkaUserService;
import com.pinch.core.base.PinchConstants;
import com.pinch.core.base.exception.PinchException;
import com.pinch.core.enums.SmsType;
import com.pinch.core.kafka.producer.NotificationProducer;
import com.pinch.core.pojo.EmailDto;
import com.pinch.core.pojo.SmsDto;
import com.pinch.core.property.manager.PropertyManager;
import com.pinch.core.user.constants.UserErrorCodes;
import com.pinch.core.user.constants.UserErrorCodes.Otp;
import com.pinch.core.user.enums.OtpType;

import lombok.extern.log4j.Log4j2;

/**
 * @author tech
 *
 * @date 12-Oct-2019
 */
@Log4j2
@Service
public class KafkaUserServiceImpl implements KafkaUserService {

	@Autowired
	private PropertyManager propertyManager;

	@Autowired
	private ThreadPoolTaskExecutor userExecutor;

	@Autowired
	private NotificationProducer notificationProducer;

	@Override
	public void sendOtpToKafka(OtpEntity otpEntity, UserEntity userEntity) {
		try {
			userExecutor.execute(() -> {
				if(Objects.isNull(otpEntity.getOtpType())) {
					otpEntity.setOtpType(OtpType.MOBILE_VERIFICATION);
				}
				switch (otpEntity.getOtpType()) {
				
				case EMAIL_VERIFICATION:
					sendOtpOnMail(otpEntity, userEntity);
					break;

				default:
					log.error("sending default via mail and email");
					sendOtpOnMobile(otpEntity);
					sendOtpOnMail(otpEntity, null);
					break;
				}
			});

		} catch (Exception e) {
			log.error("OTP Queue Overflow : ", e);
			throw new PinchException(Otp.ERROR_SENDING_OTP, e);
		}
	}

	private void sendOtpOnMobile(OtpEntity otpEntity) {
		SmsDto otpDto = getSms(otpEntity);

		sendMessage(otpDto);
	}

	private SmsDto getSms(OtpEntity otpEntity) {
		return SmsDto.builder()
				.smsType(SmsType.OTP)
				.isoCode(otpEntity.getIsoCode())
				.mobile(otpEntity.getMobile())
				.text(getOtpMessageForUserType(otpEntity, null))
				.templateId(getNotificationTemplateId(otpEntity))
				.build();
	}

	private void sendMessage(SmsDto smsDto) {

		String smsTopic = propertyManager.getProperty("kafka.topic.sms", "sms");

		if (SmsType.OTP == smsDto.getSmsType()) {
			smsTopic = propertyManager.getProperty("kafka.topic.sms.otp", "sms_otp");
		}
		log.debug("Sending OTP for user: " + smsDto);
		notificationProducer.publish(smsTopic, SmsDto.class.getName(), smsDto);
	}

	@Override
	public void sendSmsToKafka(SmsDto smsDto) {
		try {
			userExecutor.execute(() -> sendMessage(smsDto));
		} catch (Exception e) {
			log.error("SMS Queue Overflow : ", e);
			throw new PinchException(UserErrorCodes.ERROR_SENDING_SMS, e);
		}
	}

	private void sendOtpOnMail(OtpEntity otpEntity, UserEntity userEntity) {
		if (propertyManager.getPropertyAsBoolean("otp.email.enabled")
				&& StringUtils.isNotBlank(otpEntity.getEmail())) {

			try {
				EmailDto emailDto = getEmail(otpEntity, userEntity);
				log.debug("Sending OTP on Email for user: " + otpEntity.getUserId());
				notificationProducer.publish(propertyManager.getProperty("kafka.topic.email.otp", "email_otp"), EmailDto.class.getName(), emailDto);
			} catch (Exception e) {
				log.error("Error sending OTP on Email for user: " + otpEntity.getUserId(), e);
			}
		}
	}

	private EmailDto getEmail(OtpEntity otpEntity, UserEntity userEntity) {
		EmailDto emailDto = new EmailDto();

		emailDto.setFrom(propertyManager.getProperty("email.from"));
		emailDto.setFromName(PinchConstants.ORGANIZATION_NAME);

		emailDto.setTo(new String[] { otpEntity.getEmail() });

		emailDto.setSubject("OTP to access pinch Living");

		emailDto.setContent(getOtpMessageForUserType(otpEntity, userEntity));

		return emailDto;
	}

	private String getOtpMessageForUserType(OtpEntity otpEntity, UserEntity userEntity) {
		String message;

		if (OtpType.MOBILE_VERIFICATION == otpEntity.getOtpType()) {

			message = propertyManager.getProperty("mobile.verification.otp.msg", UserConstants.MOBILE_VERIFICATION_OTP_TEXT);

		} else if (OtpType.EMAIL_VERIFICATION == otpEntity.getOtpType()) {

			message = propertyManager.getProperty("email.verification.otp.msg", UserConstants.EMAIL_VERIFICATION_OTP_TEXT);
			
			StringBuffer name = new StringBuffer("");
			
			if(Objects.nonNull(userEntity)) {
				
				UserProfileEntity userProfile = userEntity.getUserProfile();
				
				if(Objects.nonNull(userProfile)) {
					
					if(Objects.nonNull(userProfile.getFirstName()))
						name = name.append(userProfile.getFirstName());
					
					if(Objects.nonNull(userProfile.getLastName()))
						name = name.append(" ").append(userProfile.getLastName());
				}
			}
			
			message = message.replaceAll("<residentName>", name.toString());

		} else if (OtpType.USER_VERFICATION == otpEntity.getOtpType()) {

			message = propertyManager.getProperty("user.verification.otp.msg", UserConstants.USER_VERIFICATION_OTP_TEXT);

		} else {
			switch (otpEntity.getUserType()) {
				case STUDENT:
					message = propertyManager.getProperty("student.otp.msg", UserConstants.DEFAULT_OTP_TEXT);
					break;
				case PARENT:
					message = propertyManager.getProperty("parent.otp.msg", UserConstants.DEFAULT_OTP_TEXT);
					break;
				case LEGAL:
					message = propertyManager.getProperty("legal.otp.msg", UserConstants.DEFAULT_OTP_TEXT);
					break;
				case HR:
					message = propertyManager.getProperty("hr.otp.msg", UserConstants.DEFAULT_OTP_TEXT);
					break;
				case TECH:
					message = propertyManager.getProperty("tech.otp.msg", UserConstants.DEFAULT_OTP_TEXT);
					break;
				case FINANCE:
					message = propertyManager.getProperty("finance.otp.msg", UserConstants.DEFAULT_OTP_TEXT);
					break;
				case PROCUREMENT:
					message = propertyManager.getProperty("procurement.otp.msg", UserConstants.DEFAULT_OTP_TEXT);
					break;
				default:
					message = propertyManager.getProperty("default.otp.msg", UserConstants.DEFAULT_OTP_TEXT);
			}
		}

		message = message.replaceAll("<otp>", String.valueOf(otpEntity.getOtp()));
		
		return message;
	}

	private String getNotificationTemplateId(OtpEntity otpEntity) {
		if (OtpType.MOBILE_VERIFICATION == otpEntity.getOtpType()) {
			return NotificationKeys.MOBILE_VERIFICATION_OTP_MSG;
		} else if (OtpType.EMAIL_VERIFICATION == otpEntity.getOtpType()) {
			return NotificationKeys.EMAIL_VERIFICATION_OTP_MSG;
		} else if (OtpType.USER_VERFICATION == otpEntity.getOtpType()) {
			return NotificationKeys.USER_VERIFICATION_OTP_MSG;
		} else {
			switch (otpEntity.getUserType()) {
				case STUDENT:
					return NotificationKeys.STUDENT_OTP_MSG;
				case PARENT:
					return NotificationKeys.PARENT_OTP_MSG;
				case LEGAL:
					return NotificationKeys.LEGAL_OTP_MSG;
				case HR:
					return NotificationKeys.HR_OTP_MSG;
				case TECH:
					return NotificationKeys.TECH_OTP_MSG;
				case FINANCE:
					return NotificationKeys.FINANCE_OTP_MSG;
				case PROCUREMENT:
					return NotificationKeys.PROCUREMENT_OTP_MSG;
				default:
					return NotificationKeys.DEFAULT_OTP_MSG;
			}
		}
	}

	public void sendNewRoleToKafka(RoleDto roleDto) {

		try {
			userExecutor.execute(() -> sendMessage(roleDto));
		} catch (Exception e) {
			log.error("Role Queue Overflow for : " + roleDto, e);
		}

	}

	private void sendMessage(RoleDto roleDto) {
		String topic = propertyManager.getProperty("kafka.topic.acl", "acl");
		notificationProducer.publish(topic, RoleDto.class.getName(), roleDto);
	}
}
