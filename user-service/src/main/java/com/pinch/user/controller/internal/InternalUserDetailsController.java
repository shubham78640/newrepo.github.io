package com.pinch.user.controller.internal;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pinch.user.service.UserService;
import com.pinch.core.base.common.dto.ResponseDto;
import com.pinch.core.user.dto.UserManagerProfileRequestDto;
import com.pinch.core.user.dto.UserProfileDto;
import com.pinch.core.user.request.dto.ActiveUserRequestDto;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/internal/details")
public class InternalUserDetailsController {

	@Autowired
	private UserService userService;

	@GetMapping("{userUuid}")
	public ResponseDto<UserProfileDto> getUser(@PathVariable String userUuid) {

		log.info("Fetching User with userUuid: {}", userUuid);

		return ResponseDto.success("Found User for userUuid", userService.getActiveUserByUserId(userUuid));
	}
	
	@GetMapping("email")
	public ResponseDto<UserProfileDto> getUserProfileDtoByEmail(@RequestParam(name = "email", required = true) String email) {

		log.info("Fetching User with userUuid: {}", email);

		return ResponseDto.success("Found User for email", userService.getUserProfileDtoByEmail(email));
	}

	@GetMapping("all")
	public ResponseDto<List<UserProfileDto>> getAllUsers() {

		log.info("Fetching All User");

		return ResponseDto.success("Found Users", userService.getAllUsers());
		
	}
	
	@PostMapping("user/all")
	public ResponseDto<List<UserProfileDto>> getAllActiveUsersByUuidIn(@RequestBody ActiveUserRequestDto activeUserRequestDto) {

		log.info("Fetching All User");

		return ResponseDto.success("Found Users", userService.getAllActiveUsersByUuidIn(activeUserRequestDto));
	}
	
	@GetMapping("/mobile")
	public ResponseDto<UserProfileDto> getUserDetails(@RequestParam(name = "mobileNo", required = true) String mobileNo) {

		log.info("Fetching User with mobileNo: {}", mobileNo);

		return ResponseDto.success("Found User for userUuid", userService.getUserDetails(mobileNo));
	}
	
	@PostMapping("/userProfiles")
	public ResponseDto<Map<String, UserProfileDto>> getUserProfileByUserID(@RequestBody UserManagerProfileRequestDto profileRequestDto) {
		log.info("get user profiles by " + profileRequestDto.getUserUuids());

		Map<String, UserProfileDto> userProfileMap = userService.getUserProfileForUserIn(profileRequestDto.getUserUuids());
		
		return Objects.nonNull(userProfileMap)? ResponseDto.success("User Profile Found!", userProfileMap):
			ResponseDto.failure("User Profiles Not Found");
	}

}
