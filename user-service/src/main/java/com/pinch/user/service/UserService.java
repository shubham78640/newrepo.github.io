/**
 * 
 */
package com.pinch.user.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinch.user.entity.UserEntity;
import com.pinch.core.base.common.dto.PageResponse;
import com.pinch.core.user.dto.AccessLevelRoleRequestDto;
import com.pinch.core.user.dto.UserDto;
import com.pinch.core.user.dto.UserFilterDto;
import com.pinch.core.user.dto.UserManagerAndRoleDto;
import com.pinch.core.user.dto.UserProfileDto;
import com.pinch.core.user.dto.UserRoleCacheDto;
import com.pinch.core.user.enums.UserType;
import com.pinch.core.user.request.dto.ActiveUserRequestDto;
import com.pinch.core.user.request.dto.AddUserRequestDto;
import com.pinch.core.user.request.dto.UpdateDepartmentUserTypeDto;
import com.pinch.core.user.request.dto.UpdateUserRequestDto;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
public interface UserService {

	UserProfileDto getActiveUserByUserId(String userId);

	void assertActiveUserByUserUuid(String userId);

	UserDto addUser(AddUserRequestDto addUserRequestDto);

	UserProfileDto getUserProfile(String userId);

	PageResponse<UserProfileDto> searchUser(UserFilterDto userFilterDto);

	Map<String, UserProfileDto> getUserProfileIn(Map<String, String> userManagerUuidMap);

	boolean updateUserStatus(String userId, Boolean status);

	UserManagerAndRoleDto getUserWithManagerAndRole(String userUuid);

	List<UserProfileDto> getAllUsers();
	
	List<UserProfileDto> getAllActiveUsersByUuidIn(ActiveUserRequestDto activeUserRequestDto);

	List<UserEntity> getUserByEmail(String email);

	boolean updateUserTypeAndDepartment(UpdateDepartmentUserTypeDto updateDepartmentUserTypeDto);

	UserDto updateUser(UpdateUserRequestDto updateUserRequestDto);

	UserDto updateUserMobile(UpdateUserRequestDto updateUserRequestDto);

	boolean updateUserStatus(String mobileNo, UserType userType, Boolean enabled);

	UserDto updateUserType(String mobileNo, String isoCode, UserType userType);

	UserDto getUserForAccessLevelAndRole(AccessLevelRoleRequestDto cityRolesRequestDto);

	boolean createRoleBaseUser(UserType userType);

	Map<String, UserProfileDto> getUserProfileDto(Set<String> mobileNos);

	boolean createRoleBaseUser(List<String> mobiles);

	UserProfileDto getUserDetails(String mobileNo);
    
	Map<String, UserProfileDto> getUserProfileForUserIn(List<String> userUuids);
    
	List<UserRoleCacheDto> getCacheableForRoles(List<String> roleNames);

	UserProfileDto getUserProfileDtoByEmail(String email);
}