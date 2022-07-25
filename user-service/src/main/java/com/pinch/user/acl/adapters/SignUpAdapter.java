package com.pinch.user.acl.adapters;

import com.pinch.user.entity.SignupEntity;
import com.pinch.core.user.request.dto.AddUserRequestDto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SignUpAdapter {

	public static SignupEntity getSignupEntity(AddUserRequestDto addUserRequestDto, int otp) {

		SignupEntity signup = SignupEntity.builder().otp(otp).mobile(addUserRequestDto.getMobile())
				.validated(Boolean.FALSE).signupObject(addUserRequestDto).build();

		return signup;
	}

}
