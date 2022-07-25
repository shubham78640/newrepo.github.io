/**
 * 
 */
package com.pinch.user.db.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.pinch.user.db.service.OtpDbService;
import com.pinch.user.entity.OtpEntity;
import com.pinch.user.repository.OtpRepository;
import com.pinch.core.base.utils.PhoneNumberUtils;
import com.pinch.core.sqljpa.service.impl.AbstractJpaServiceImpl;
import com.pinch.core.user.enums.OtpType;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
@Service
public class OtpDbServiceImpl extends AbstractJpaServiceImpl<OtpEntity, Long, OtpRepository> implements OtpDbService {

	@Autowired
	private OtpRepository otpRepository;

	@Override
	protected OtpRepository getJpaRepository() {
		return otpRepository;
	}

	@Override
	public OtpEntity getOtpForMobile(String mobile, OtpType otpType, String isoCode) {
		Pageable pageable = PageRequest.of(0, 1, Direction.DESC, "updatedAt");

		List<OtpEntity> userOtps = getJpaRepository()
				.findByMobileAndOtpTypeAndIsoCode(PhoneNumberUtils.normalizeNumber(mobile), otpType, isoCode, pageable);

		return CollectionUtils.isNotEmpty(userOtps) ? userOtps.get(0) : null;
	}

	@Override
	public OtpEntity getActiveOtpForMobile(String mobile, OtpType otpType, String isoCode) {
		Pageable pageable = PageRequest.of(0, 1, Direction.DESC, "updatedAt");

		List<OtpEntity> userOtps =
				getJpaRepository()
						.findByMobileAndOtpTypeAndIsoCodeAndStatus(PhoneNumberUtils.normalizeNumber(mobile), otpType, isoCode, true, pageable);

		return CollectionUtils.isNotEmpty(userOtps) ? userOtps.get(0) : null;
	}

	@Override
	public OtpEntity getUserOtpByUserId(String userId, OtpType otpType) {

		Pageable pageable = PageRequest.of(0, 1, Direction.DESC, "updatedAt");
		List<OtpEntity> userOtps = getJpaRepository().findByUserIdAndOtpType(userId, otpType, pageable);

		return CollectionUtils.isNotEmpty(userOtps) ? userOtps.get(0) : null;
	}

	@Override
	public OtpEntity getActiveOtpByEmailAndUserUuidAndOtpType(String email, String userUuid, OtpType otpType) {
		
		Pageable pageable = PageRequest.of(0, 1, Direction.DESC, "updatedAt");

		List<OtpEntity> userOtps = getJpaRepository().findByEmailAndOtpTypeAndUserIdAndStatus(email, otpType, userUuid, true, pageable);

		return CollectionUtils.isNotEmpty(userOtps) ? userOtps.get(0) : null;
	}
}