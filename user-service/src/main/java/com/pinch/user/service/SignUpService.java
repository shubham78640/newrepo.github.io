package com.pinch.user.service;

import com.pinch.core.user.dto.UserProfileDto;
import com.pinch.core.user.request.dto.AddUserRequestDto;

public interface SignUpService {

	String signUpUser(AddUserRequestDto addUserRequestDto);

	UserProfileDto validateSignUpOtp(String uuid, String otp);

}
