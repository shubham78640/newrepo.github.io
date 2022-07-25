package com.pinch.user.interceptor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.pinch.user.entity.UserSessionEntity;
import com.pinch.user.service.SessionService;
import com.pinch.core.base.PinchConstants;
import com.pinch.core.base.constants.SecurityConstants;
import com.pinch.core.base.exception.AuthException;
import com.pinch.core.base.utils.DateUtil;
import com.pinch.core.base.utils.SecureCookieUtil;
import com.pinch.core.user.constants.UserErrorCodes;

import lombok.extern.log4j.Log4j2;

/**
 * 
 * @author tech
 *
 * @date 11-Oct-2019
 */
@Log4j2
@Component
public class UserAuthInterceptor extends HandlerInterceptorAdapter {

	@Value("${inactive.minutes.student:0}")
	private int studentUserInactiveMinutes;

	@Value("${inactive.minutes.parent:0}")
	private int parentUserInactiveMinutes;

	@Value("${inactive.minutes.legal:180}")
	private int legalUserInactiveMinutes;

	@Value("${inactive.minutes.hr:180}")
	private int hrUserInactiveMinutes;

	@Value("${inactive.minutes.tech:180}")
	private int techUserInactiveMinutes;

	@Value("${inactive.minutes.finance:180}")
	private int financeUserInactiveMinutes;

	@Value("${inactive.minutes.procurement:1440}")
	private int procurementUserInactiveMinutes;

	@Autowired
	private SessionService sessionService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String token = extractTokenFromRequest(request);

		try {
			if (StringUtils.isNotBlank(token)) {
				UserSessionEntity userSession = sessionService.getUserSessionByToken(token);

				if (userSession != null && userSession.getUserType() != null) {

					boolean isSessionExpired = userSessionExpiredCheck(userSession);

					markInactiveIfSessionExpired(isSessionExpired, userSession);

					setUpdatedAtAndSave(userSession);

					sessionExpirationCheck(isSessionExpired, request, response);

					request.setAttribute(SecurityConstants.USER_ID, userSession.getUserId());

					return true;
				} else {
					log.error("No User Session found with Token: " + token + ". Cann't authorize user.");
				}
			} else {
				log.error("User Token is null/empty. Can't authorize user. Send to login for url :" + request.getRequestURI());
			}
		} catch (Exception e) {
			log.error("Error validating user token: ", e);
		}

		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		return false;
	}

	private String extractTokenFromRequest(HttpServletRequest request) {
		String token = null;

		if (request.getCookies() != null) {

			for (Cookie cookie : request.getCookies()) {

				if (SecurityConstants.TOKEN_HEADER_NAME.equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}

		if (token == null) {
			token = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
			if (token != null && token.startsWith(SecurityConstants.VENTA_TOKEN_PREFIX)) {		//only if it follows bearer schema, then we would consider valid token
				token = token.replace(SecurityConstants.VENTA_TOKEN_PREFIX, "");
			} else {
				token = null;
			}
		}

		return token;
	}

	private boolean userSessionExpiredCheck(UserSessionEntity userSession) {
		int sessionExpiredTime = 0;

		switch (userSession.getUserType()) {

			case STUDENT:
				sessionExpiredTime = studentUserInactiveMinutes;
				break;

			case PARENT:
				sessionExpiredTime = parentUserInactiveMinutes;
				break;

			case LEGAL:
				sessionExpiredTime = legalUserInactiveMinutes;
				break;

			case HR:
				sessionExpiredTime = hrUserInactiveMinutes;
				break;

			case TECH:
				sessionExpiredTime = techUserInactiveMinutes;
				break;

			case FINANCE:
				sessionExpiredTime = financeUserInactiveMinutes;
				break;

			case PROCUREMENT:
				sessionExpiredTime = procurementUserInactiveMinutes;
				break;

			default:
				sessionExpiredTime = 0;
				break;
		}

		return isSessionExpired(userSession, sessionExpiredTime);

	}

	private boolean isSessionExpired(UserSessionEntity userSession, int inActivityMinutes) {
		boolean isSessionExpired = false;

		if (inActivityMinutes > 0) {
			Date expiryTime = getUserExpiryTime(inActivityMinutes);

			if (userSession.getUpdatedAt() == null || userSession.getUpdatedAt().before(expiryTime)) {
				isSessionExpired = true;
			}
		}

		log.debug("User [UserId: " + userSession.getUserId() + ", UserType: " + userSession.getUserType() + "] Session Expiration Status: " + isSessionExpired);

		return isSessionExpired;
	}

	private Date getUserExpiryTime(int inactivityMinutes) {
		ZoneId zone = PinchConstants.IST_TIMEZONEID;

		return new Date(LocalDateTime.now(zone).atZone(zone).toInstant().toEpochMilli() - (inactivityMinutes * 60000));
	}

	private void markInactiveIfSessionExpired(boolean isSessionExpired, UserSessionEntity userSession) {
		if (isSessionExpired) {
			userSession.setStatus(false);
		}
	}

	private void setUpdatedAtAndSave(UserSessionEntity userSession) {
		userSession.setUpdatedAt(DateUtil.convertToDate(LocalDateTime.now(PinchConstants.IST_TIMEZONEID)));
		sessionService.updateUserSession(userSession);
	}

	private void sessionExpirationCheck(boolean isSessionExpired, HttpServletRequest request, HttpServletResponse response) {
		if (isSessionExpired) {

			SecureCookieUtil.handleLogOutResponse(request, response);

			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			throw new AuthException("User Session has expired", UserErrorCodes.SESSION_EXPIRED);
		}
	}
}