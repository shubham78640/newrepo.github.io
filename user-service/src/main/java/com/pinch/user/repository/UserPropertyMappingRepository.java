/**
 * 
 */
package com.pinch.user.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.pinch.user.entity.UserPropertyMappingEntity;
import com.pinch.core.sqljpa.repository.AbstractJpaRepository;

/**
 * @author tech
 *
 * @date 13-Oct-2019
 */
@Repository
public interface UserPropertyMappingRepository extends AbstractJpaRepository<UserPropertyMappingEntity, Long> {

	List<UserPropertyMappingEntity> findByUserId(String userId, Pageable pageable);

	List<UserPropertyMappingEntity> findByUserIdAndStatus(String userId, boolean status, Pageable pageable);

	boolean existsByUserIdAndPropertyId(String userId, String propertyId);
}