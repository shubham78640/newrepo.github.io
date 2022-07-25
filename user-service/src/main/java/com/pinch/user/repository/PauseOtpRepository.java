/**
 * 
 */
package com.pinch.user.repository;

import org.springframework.stereotype.Repository;

import com.pinch.user.entity.PauseOtpEntity;
import com.pinch.core.sqljpa.repository.AbstractJpaRepository;

@Repository
public interface PauseOtpRepository extends AbstractJpaRepository<PauseOtpEntity, Long> {

	boolean existsByMobileAndStatus(String mobile,boolean status);
	
	PauseOtpEntity findByMobile(String mobile);	
	
}