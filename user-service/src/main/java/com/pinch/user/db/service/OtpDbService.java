/**
 * 
 */
package com.pinch.user.db.service;

import com.pinch.user.entity.OtpEntity;
import com.pinch.core.sqljpa.service.AbstractJpaService;
import com.pinch.core.user.enums.OtpType;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
public interface OtpDbService extends AbstractJpaService<OtpEntity, Long> {

	OtpEntity getOtpForMobile(String mobile, OtpType otpType, String isoCode);

	OtpEntity getActiveOtpForMobile(String mobile, OtpType otpType, String isoCode);

	OtpEntity getUserOtpByUserId(String userId, OtpType otpType);

	OtpEntity getActiveOtpByEmailAndUserUuidAndOtpType(String email, String userUuid, OtpType otpType);

}