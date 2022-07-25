/**
 * 
 */
package com.pinch.user.service.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.pinch.user.db.service.UserSessionDbService;
import com.pinch.user.entity.UserEntity;
import com.pinch.user.entity.UserSessionEntity;
import com.pinch.user.service.SessionService;
import com.pinch.user.service.UserService;
import com.pinch.core.base.constants.SecurityConstants;
import com.pinch.core.base.exception.AuthException;
import com.pinch.core.user.constants.UserErrorCodes;
import com.pinch.core.user.dto.SessionRequestDto;
import com.pinch.core.user.dto.UserDto;

import lombok.extern.log4j.Log4j2;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
@Log4j2
@Service
public class SessionServiceImpl implements SessionService {

	@Value("${bcrypt.salt}")
	private String bcryptSalt;

	@Autowired
	private UserSessionDbService userSessionDbService;

	@Autowired
	private UserService userService;

	@Override
	public UserSessionEntity createUserSession(UserDto userDto, String token) {

		log.info("Creating Session for User: " + userDto.getUuid());

		UserSessionEntity userSessionEntity =
				UserSessionEntity.builder()
						.userId(userDto.getUuid())
						.token(getBcryptPassword(token))
						.userType(userDto.getUserType())
						.build();

		userSessionEntity = userSessionDbService.saveAndFlush(userSessionEntity);

		log.info("Created Session: " + userSessionEntity.getUuid() + " for User: " + userDto.getUuid());

		return userSessionEntity;
	}

	@Override
	public UserSessionEntity getUserSessionByToken(String token) {
		return userSessionDbService.getUserSessionForToken(getBcryptPassword(token));
	}

	@Override
	public void removeUserSession(String token) {

		UserSessionEntity userSessionEntity = getUserSessionByToken(token);

		if (Objects.isNull(userSessionEntity)) {
			throw new AuthException("No User Session Found!! Please Login!!", UserErrorCodes.SESSION_NOT_FOUND);
		}

		log.info("Invalidating User Session: " + userSessionEntity.getUuid() + " for User: " + userSessionEntity.getUserId());

		userSessionEntity.setStatus(false);
		userSessionDbService.updateAndFlush(userSessionEntity);
	}

	@Override
	public UserSessionEntity updateUserSession(UserSessionEntity userSessionEntity) {
		return userSessionDbService.updateAndFlush(userSessionEntity);
	}

    @Override
    public void createSession(SessionRequestDto sessionRequestDto) {
		List<UserEntity> userEntityList = userService.getUserByEmail(sessionRequestDto.getEmail().trim());
		if (CollectionUtils.isEmpty(userEntityList)) {
			log.info("User not found for email: " + sessionRequestDto.getEmail());			//if all users of venta will be created in user service, then throw exception
			return;
		}

		String token = sessionRequestDto.getToken().replace(SecurityConstants.VENTA_TOKEN_PREFIX, "");
		UserSessionEntity userSessionEntity = getUserSessionByToken(token);
		if (null != userSessionEntity) {
			log.info("Session already exist with token {}", token);
			return;
		}

		UserEntity userEntity = userEntityList.get(0);
		userSessionEntity = UserSessionEntity.builder()
						.userId(userEntity.getUuid())
						.token(getBcryptPassword(token))
						.userType(userEntity.getUserType())
						.build();
		userSessionEntity = userSessionDbService.saveAndFlush(userSessionEntity);

		log.info("Created Manual Session: " + userSessionEntity.getUuid() + " for User: " + userEntity.getUuid());
    }

    private String getBcryptPassword(String password) {
		return BCrypt.hashpw(password, bcryptSalt);
	}

}