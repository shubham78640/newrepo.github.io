package com.pinch.user.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pinch.user.db.service.SignUpDbService;
import com.pinch.user.entity.SignupEntity;
import com.pinch.user.repository.SignUpRepository;
import com.pinch.core.sqljpa.service.impl.AbstractJpaServiceImpl;
@Service
public class SignUpDbServiceImpl extends AbstractJpaServiceImpl<SignupEntity, Long, SignUpRepository>
		implements SignUpDbService {

	@Autowired
	private SignUpRepository signUpRepository;

	@Override
	protected SignUpRepository getJpaRepository() {
		return signUpRepository;
	}

}
