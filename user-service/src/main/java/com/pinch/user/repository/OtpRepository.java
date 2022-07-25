/**
 * 
 */
package com.pinch.user.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.pinch.user.entity.OtpEntity;
import com.pinch.core.sqljpa.repository.AbstractJpaRepository;
import com.pinch.core.user.enums.OtpType;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
@Repository
public interface OtpRepository extends AbstractJpaRepository<OtpEntity, Long> {

	List<OtpEntity> findByMobileAndOtpTypeAndIsoCode(String mobile, OtpType otpType, String isoCode, Pageable pageable);
	
	List<OtpEntity> findByMobileAndOtpTypeAndIsoCodeAndStatus(String mobile, OtpType otpType, String isoCode, boolean status, Pageable pageable);

	List<OtpEntity> findByUserIdAndOtpType(String userId, OtpType otpType, Pageable pageable);

	List<OtpEntity> findByEmailAndOtpTypeAndUserIdAndStatus(String email, OtpType otpType, String userUuid, boolean status, Pageable pageable);
}