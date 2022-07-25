/**
 * 
 */
package com.pinch.user.repository;

import org.springframework.stereotype.Repository;

import com.pinch.user.entity.UserSessionEntity;
import com.pinch.core.sqljpa.repository.AbstractJpaRepository;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
@Repository
public interface UserSessionRepository extends AbstractJpaRepository<UserSessionEntity, Long> {

	UserSessionEntity findByTokenAndStatus(String token, boolean status);
}