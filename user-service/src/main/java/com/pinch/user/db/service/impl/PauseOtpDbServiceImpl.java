package com.pinch.user.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pinch.user.db.service.PauseOtpDbService;
import com.pinch.user.entity.PauseOtpEntity;
import com.pinch.user.repository.PauseOtpRepository;
import com.pinch.core.sqljpa.service.impl.AbstractJpaServiceImpl;

@Service
public class PauseOtpDbServiceImpl extends AbstractJpaServiceImpl<PauseOtpEntity, Long, PauseOtpRepository> implements PauseOtpDbService {

	@Autowired
	private PauseOtpRepository pauseOtpRepository;

	@Override
	public boolean checkIfMobileExist(String mobile) {
		return getJpaRepository().existsByMobileAndStatus(mobile, true);
	}

	@Override
	public PauseOtpEntity findByMobileNumber(String mobile) {
		return getJpaRepository().findByMobile(mobile);
	}

	@Override
	protected PauseOtpRepository getJpaRepository() {
		return pauseOtpRepository;
	}

}