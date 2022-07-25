/**
 * 
 */
package com.pinch.user.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pinch.user.entity.UserManagerMappingEntity;
import com.pinch.core.sqljpa.repository.AbstractJpaRepository;

/**
 * @author raj.kumar
 *
 */
@Repository
public interface UserManagerMappingRepository extends AbstractJpaRepository<UserManagerMappingEntity, Long> {

	List<UserManagerMappingEntity> findByManagerIdAndStatus(String managerId, Boolean status);
	
	UserManagerMappingEntity findByUserId(String userId);
	
	List<UserManagerMappingEntity> findByUserIdIn(List<String> userIds);
	
	List<UserManagerMappingEntity> findByManagerId(String managerId);

    UserManagerMappingEntity findFirstByUserId(String userUuid);
}
