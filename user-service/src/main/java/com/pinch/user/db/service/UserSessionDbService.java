/**
 * 
 */
package com.pinch.user.db.service;

import com.pinch.user.entity.UserSessionEntity;
import com.pinch.core.sqljpa.service.AbstractJpaService;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
public interface UserSessionDbService extends AbstractJpaService<UserSessionEntity, Long> {

	UserSessionEntity getUserSessionForToken(String token);

}