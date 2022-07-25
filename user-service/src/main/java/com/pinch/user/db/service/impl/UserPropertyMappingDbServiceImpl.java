/**
 * 
 */
package com.pinch.user.db.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.pinch.user.db.service.UserPropertyMappingDbService;
import com.pinch.user.entity.UserPropertyMappingEntity;
import com.pinch.user.repository.UserPropertyMappingRepository;
import com.pinch.core.sqljpa.service.impl.AbstractJpaServiceImpl;

/**
 * @author tech
 *
 * @date 13-Oct-2019
 */
@Service
public class UserPropertyMappingDbServiceImpl
		extends AbstractJpaServiceImpl<UserPropertyMappingEntity, Long, UserPropertyMappingRepository>
		implements UserPropertyMappingDbService {

	@Autowired
	private UserPropertyMappingRepository userPropertyMappingRepository;

	@Override
	protected UserPropertyMappingRepository getJpaRepository() {
		return userPropertyMappingRepository;
	}

	@Override
	public List<UserPropertyMappingEntity> getAllUserPropertyMappings(String userId) {
		return getJpaRepository().findByUserId(userId, PageRequest.of(0, 100));
	}

	@Override
	public List<UserPropertyMappingEntity> getActiveUserPropertyMappings(String userId) {
		return getJpaRepository().findByUserIdAndStatus(userId, true, PageRequest.of(0, 100));
	}

	@Override
	public boolean isUserPropertyMappingExists(String userId, String propertyId) {
		return getJpaRepository().existsByUserIdAndPropertyId(userId, propertyId);
	}

}