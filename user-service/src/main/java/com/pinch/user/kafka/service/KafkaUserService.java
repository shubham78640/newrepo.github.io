/**
 * 
 */
package com.pinch.user.kafka.service;

import com.pinch.user.entity.OtpEntity;
import com.pinch.user.entity.UserEntity;
import com.pinch.core.pojo.SmsDto;
import com.pinch.core.user.acl.dto.RoleDto;

/**
 * @author tech
 *
 * @date 12-Oct-2019
 */
public interface KafkaUserService {

	void sendOtpToKafka(OtpEntity otpEntity, UserEntity userEntity);

	void sendSmsToKafka(SmsDto smsDto);

	void sendNewRoleToKafka(RoleDto roleDto);
}