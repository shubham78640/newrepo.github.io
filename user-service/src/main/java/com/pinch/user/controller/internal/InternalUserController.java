	/**
 * 
 */
package com.pinch.user.controller.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import com.pinch.core.generic.dto.UIKeyValue;
import com.pinch.core.user.dto.UserRoleCacheDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pinch.user.service.UserService;
import com.pinch.core.base.common.dto.ResponseDto;
import com.pinch.core.user.dto.AccessLevelRoleRequestDto;
import com.pinch.core.user.dto.UserDto;
import com.pinch.core.user.dto.UserProfileDto;
import com.pinch.core.user.enums.UserType;
import com.pinch.core.user.request.dto.AddUserRequestDto;
import com.pinch.core.user.request.dto.UpdateUserRequestDto;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
@Log4j2
@RestController
@RequestMapping("internal")
public class InternalUserController {

	@Autowired
	private UserService userService;

	@PostMapping("update")
	public ResponseDto<UserDto> updateUser(@RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {

		UserDto userDto = userService.updateUser(updateUserRequestDto);

		log.info("Update user with id: " + userDto.getUuid());

		return ResponseDto.success("User Updated", userDto);
	}

	@PostMapping("update/mobile")
	public ResponseDto<UserDto> updateUserMobile(@RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {

		UserDto userDto = userService.updateUserMobile(updateUserRequestDto);

		log.info("Update user with id: " + userDto.getUuid());

		return ResponseDto.success("User Updated", userDto);
	}

	@PostMapping("add")
	public ResponseDto<UserDto> addUser(@RequestBody @Valid AddUserRequestDto addUserRequestDto) {

		UserDto userDto = userService.addUser(addUserRequestDto);

		log.info("Added new user with id: " + userDto.getUuid());

		return ResponseDto.success("New User Created", userDto);
	}

	@PostMapping("deactivate/{userId}")
	public ResponseDto<Boolean> addUser(@PathVariable String userId) {

		boolean status = userService.updateUserStatus(userId, false);

		log.info("Deactivated user with id: " + userId);

		return (status) ? ResponseDto.success(status) : ResponseDto.failure("Unable to deactivate user");
	}

	@PostMapping("update/status/{mobileNo}/{userType}/{enabled}")
	public ResponseDto<Boolean> updateUserStatus(@PathVariable(value="mobileNo",required=true) String mobileNo,
			@PathVariable(value="userType",required=true) UserType userType,
			@PathVariable(name = "enabled", required = true) Boolean enabled
			) {

		boolean status = userService.updateUserStatus(mobileNo, userType,enabled);

		log.info("Status Update {} for Mobile No {} ",enabled, mobileNo);

		return (status) ? ResponseDto.success("Status Update Successfully", status)
				: ResponseDto.failure("Unable to Update user Status");
	}

	@PostMapping("update/userType/{mobileNo}/{isoCode}/{userType}")
	public ResponseDto<UserDto> updateUserType(@PathVariable(value="mobileNo",required=true) String mobileNo,
			@PathVariable(value="isoCode",required=true) String isoCode,
			@PathVariable(value="userType",required=true) UserType userType
			
			) {

		UserDto userDto = userService.updateUserType(mobileNo,isoCode, userType);

		log.info("Update userType: " + userDto.getUuid());

		return ResponseDto.success("User Updated", userDto);
	}

	
	@PostMapping("user/role/accesslevel")
	@ApiOperation(value = "Get user for particular access level and role")
	public UserDto getUserForAccessLevelAndRole(@RequestBody @Valid AccessLevelRoleRequestDto cityRolesRequestDto) {

		log.info("Request received for getting users for Role: {}  and AccessLevel: {}  with AccessUuid:{}", cityRolesRequestDto.getRoleName(), cityRolesRequestDto.getAccessLevel(), cityRolesRequestDto.getAccessLevelUuid());

		return userService.getUserForAccessLevelAndRole(cityRolesRequestDto);
	}
	

	@PostMapping("/search/get/userUuid")
	public ResponseDto<Map<String,UserProfileDto>> getUserProfileDto(@RequestBody Set<String> mobileNos
			) {

		log.info("Request received for getting user details");
		
		Map<String,UserProfileDto> userlist = userService.getUserProfileDto(mobileNos);

		log.info("Successfully fetched user details.");
		
		return ResponseDto.success("Found user Details with manager and role details.", userlist);
	}

	@PostMapping("/accesslevel/cachemaps")
	public ResponseDto<List<UserRoleCacheDto>> getCacheableRoles(@RequestBody List<String> roles ) {

		log.info("Fetching user by {} {}", roles);

		return ResponseDto.success("Found User", userService.getCacheableForRoles(roles));
	}
}