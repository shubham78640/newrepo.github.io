/**
 * 
 */
package com.pinch.user.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pinch.user.db.service.UserSessionDbService;
import com.pinch.user.entity.UserSessionEntity;
import com.pinch.user.repository.UserSessionRepository;
import com.pinch.core.sqljpa.service.impl.AbstractJpaServiceImpl;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
@Service
public class UserSessionDbServiceImpl extends AbstractJpaServiceImpl<UserSessionEntity, Long, UserSessionRepository> implements UserSessionDbService {

	@Autowired
	private UserSessionRepository userSessionRepository;

	@Override
	protected UserSessionRepository getJpaRepository() {
		return userSessionRepository;
	}

	@Override
	public UserSessionEntity getUserSessionForToken(String token) {
		return userSessionRepository.findByTokenAndStatus(token, true);
	}

}