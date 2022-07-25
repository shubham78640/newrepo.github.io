package com.pinch.user.config;

import javax.annotation.PostConstruct;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.pinch.core.base.http.PinchRestClient;
import com.pinch.core.base.notification.SlackNotification;

@Aspect
@Configuration
public class AopConfig {

	@Qualifier("slackClient")
	@Autowired
	private PinchRestClient pinchRestClient;

	private SlackNotification slackNotification;

	@Value("${enable.slack.exception}")
	private Boolean enableSlackException;

	@Value("${service.slack.exception.endUrl}")
	private String slackExceptionEndUrl;

	@Value("${spring.application.name}")
	private String springApplicationName;

	@PostConstruct
	public void init() {
		slackNotification = new SlackNotification(pinchRestClient);
	}

	@Before(value = "execution(* com.pinch.core.base.exception.ExceptionInterceptor.*(..)) && @annotation(com.pinch.core.base.annotation.SendExceptionToSlack))")
	public void sendToSlack(JoinPoint joinPoint) {
		if (enableSlackException)
			slackNotification.sendExceptionNotificationRequest((Exception) joinPoint.getArgs()[0], slackExceptionEndUrl);
	}
}