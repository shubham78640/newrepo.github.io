/**
 * 
 */
package com.pinch.user.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pinch.user.acl.service.AclService;
import com.pinch.user.adapters.UserAdapter;
import com.pinch.user.service.UserService;
import com.pinch.core.base.common.dto.PageResponse;
import com.pinch.core.base.common.dto.PaginationRequest;
import com.pinch.core.base.common.dto.ResponseDto;
import com.pinch.core.base.constants.SecurityConstants;
import com.pinch.core.base.enums.Department;
import com.pinch.core.base.utils.CSVConverter;
import com.pinch.core.user.acl.dto.AclUserProfileDTO;
import com.pinch.core.user.dto.UserDto;
import com.pinch.core.user.dto.UserFilterDto;
import com.pinch.core.user.dto.UserManagerAndRoleDto;
import com.pinch.core.user.dto.UserProfileDto;
import com.pinch.core.user.enums.EnumListing;
import com.pinch.core.user.enums.UserType;
import com.pinch.core.user.request.dto.AddUserRequestDto;
import com.pinch.core.user.request.dto.UpdateDepartmentUserTypeDto;
import com.pinch.core.user.request.dto.UpdateUserRequestDto;
import com.pinch.core.user.request.dto.UserRequestDto;
import com.pinch.core.user.request.dto.UserStatusRequestDto;

import lombok.extern.log4j.Log4j2;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
@Log4j2
@RestController
@RequestMapping("")
public class UserController {

	@Autowired
	private AclService aclService;

	@Autowired
	private UserService userService;

	@GetMapping("details")
	public ResponseDto<UserProfileDto> getUser(
			@RequestAttribute(name = SecurityConstants.USER_ID) @NotBlank(message = "User Id is mandatory to get user") String userId) {

		log.info("Fetching User with UserId: " + userId);

		return ResponseDto.success("Found User for User Id", userService.getActiveUserByUserId(userId));
	}

	@GetMapping("profile")
	public ResponseDto<AclUserProfileDTO> getUserProfile(
			@RequestAttribute(name = SecurityConstants.USER_ID) @NotBlank(message = "User Id is mandatory to get user profile") String userId) {

		log.info("Fetching User Profile with UserId: " + userId);

		return ResponseDto.success("Found User Profile for User Id", UserAdapter.getAclUserProfileDTO(userService.getUserProfile(userId), aclService.getUserDeptLevelRoleNameUrlExpandedDtoFe(userId)));
	}

	@GetMapping("profile/current")
	public ResponseDto<UserProfileDto> getCurrentUserProfile(
			@RequestAttribute(name = SecurityConstants.USER_ID) @NotBlank(message = "User Id is mandatory to get user profile") String userId) {

		log.info("Fetching current User Profile with UserId: " + userId);

		return ResponseDto.success("Found Current User Profile for User Id", userService.getUserProfile(userId));
	}
	
	@PostMapping("update")
	public ResponseDto<UserDto> updateUser(@RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {

		UserDto userDto = userService.updateUser(updateUserRequestDto);

		log.info("Update user with id: " + userDto.getUuid());

		return ResponseDto.success("User Updated", userDto);
	}

	@PostMapping("add")
	public ResponseDto<UserDto> addUser(@RequestBody @Valid AddUserRequestDto addUserRequestDto) {

		UserDto userDto = userService.addUser(addUserRequestDto);

		log.info("Added new user with id: " + userDto.getUuid());

		return ResponseDto.success("New User Created", userDto);
	}

	@GetMapping("search/{pageNo}/{limit}")
	public ResponseDto<PageResponse<UserProfileDto>> searchUsers(
			@PathVariable(name = "pageNo") @Min(value = 1, message = "Page No must be greater than 0") int pageNo,
			@PathVariable(name = "limit") @Min(value = 1, message = "Limit must be greater than 0") int limit,
			@RequestParam(name = "userIds", required = false) List<String> userIds,
			@RequestParam(name = "mobile", required = false) String mobile,
			@RequestParam(name = "isoCode", required = false) String isoCode,
			@RequestParam(name = "email", required = false) String email,
			@RequestParam(name = "userType", required = false) UserType userType,
			@RequestParam(name = "status", required = false) Boolean status,
			@RequestParam(name = "department", required = false) Department department,
			@RequestParam(name = "name", required = false) String name) {

		log.info("Received User Search Request With Parameters [Page: " + pageNo + ", Limit: " + limit + ", Mobile: " + mobile + ", ISO: " + isoCode + ", Email: " + email + ", UserType: " + userType
				+ ", Status: " + status + ", UserIds: {" + CSVConverter.getCSVString(userIds) + "} ]");

		PaginationRequest paginationRequest = PaginationRequest.builder().pageNo(pageNo).limit(limit).build();
		UserFilterDto userFilterDto = UserFilterDto.builder()
				.userIds(userIds)
				.mobile(mobile)
				.isoCode(isoCode)
				.email(email)
				.userType(userType)
				.status(status)
				.department(department)
				.name(name)
				.pageRequest(paginationRequest)
				.build();
		PageResponse<UserProfileDto> userDtos = userService.searchUser(userFilterDto);

		return ResponseDto.success("Found " + userDtos.getRecords() + " Users for Search Criteria", userDtos);
	}

	@GetMapping("type/list")
	public ResponseDto<List<EnumListing<UserType>>> getUserType() {

		log.info("Received UserType listing request");
		return ResponseDto.success("Found UserType List", UserAdapter.getUserTypeEnumAsListing());
	}

	@PostMapping("update/userStatus")
	public ResponseDto<Boolean> updateUserStatus(@RequestBody UserStatusRequestDto requestDto) {

		log.info("Received request to deactivate user");
		String updatedStatus = requestDto.getStatus() ? "activated" : "deactivated";

		return ResponseDto.success("Successfully " + updatedStatus + " user.", userService.updateUserStatus(requestDto.getUserId(), requestDto.getStatus()));
	}

	@GetMapping("details/manager/role")
	public ResponseDto<UserManagerAndRoleDto> getUserWithManagerAndRole(@RequestParam("userId") String userUuid) {

		log.info("Request received for getting user details along with manager and role details");
		UserManagerAndRoleDto userManagerAndRoleDto = userService.getUserWithManagerAndRole(userUuid);

		log.info("Successfully fetched user details along with manager and role details.");
		return ResponseDto.success("Found user Details with manager and role details.", userManagerAndRoleDto);
	}
	
	@PostMapping("update/usertype/department")
	public ResponseDto<UserDto> updateUserTypeAndDepartment(@RequestBody @Valid UpdateDepartmentUserTypeDto updateDepartmentUserTypeDto) {

		boolean response = userService.updateUserTypeAndDepartment(updateDepartmentUserTypeDto);

		return response ? ResponseDto.success("User type and department modified successfully") : ResponseDto.failure("User type and department not modified");
	}

	@PostMapping("search/user")
	public ResponseDto<PageResponse<UserProfileDto>> searchUsersDetail(@RequestBody UserRequestDto userRequestDto) {

		log.info("Post Request recived search user detail UserRequestDto {} ", userRequestDto);

		PaginationRequest paginationRequest = PaginationRequest.builder().pageNo(userRequestDto.getPageNo()).limit(userRequestDto.getLimit()).build();
		UserFilterDto userFilterDto = UserFilterDto.builder()
				.userIds(userRequestDto.getUserIds())
				.mobile(userRequestDto.getMobile())
				.isoCode(userRequestDto.getIsoCode())
				.email(userRequestDto.getEmail())
				.userType(userRequestDto.getUserType())
				.status(userRequestDto.getStatus())
				.department(userRequestDto.getDepartment())
				.name(userRequestDto.getName())
				.pageRequest(paginationRequest)
				.build();
		PageResponse<UserProfileDto> userDtos = userService.searchUser(userFilterDto);

		return ResponseDto.success("Found " + userDtos.getRecords() + " Users for Search Criteria", userDtos);
	}
}