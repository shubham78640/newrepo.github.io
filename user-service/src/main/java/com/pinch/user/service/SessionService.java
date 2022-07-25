/**
 * 
 */
package com.pinch.user.service;

import com.pinch.user.entity.UserSessionEntity;
import com.pinch.core.user.dto.SessionRequestDto;
import com.pinch.core.user.dto.UserDto;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
public interface SessionService {

	UserSessionEntity createUserSession(UserDto userDto, String token);

	UserSessionEntity getUserSessionByToken(String token);

	void removeUserSession(String token);

	UserSessionEntity updateUserSession(UserSessionEntity userSessionEntity);

	void createSession(SessionRequestDto sessionRequestDto);
}