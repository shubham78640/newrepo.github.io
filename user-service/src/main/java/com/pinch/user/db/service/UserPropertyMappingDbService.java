/**
 * 
 */
package com.pinch.user.db.service;

import java.util.List;

import com.pinch.user.entity.UserPropertyMappingEntity;
import com.pinch.core.sqljpa.service.AbstractJpaService;

/**
 * @author tech
 *
 * @date 13-Oct-2019
 */
public interface UserPropertyMappingDbService extends AbstractJpaService<UserPropertyMappingEntity, Long> {

	List<UserPropertyMappingEntity> getAllUserPropertyMappings(String userId);

	List<UserPropertyMappingEntity> getActiveUserPropertyMappings(String userId);

	boolean isUserPropertyMappingExists(String userId, String propertyId);
}