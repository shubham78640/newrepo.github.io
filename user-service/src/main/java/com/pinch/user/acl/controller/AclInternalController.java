/**
 * 
 */
package com.pinch.user.acl.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pinch.core.user.acl.dto.UserAccessLevelIdsByRoleNameDto;
import com.pinch.core.user.dto.response.UserContactDetailsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pinch.user.acl.service.AclUserService;
import com.pinch.core.base.common.dto.ResponseDto;
import com.pinch.core.base.enums.Department;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/internal/acl/")
public class AclInternalController {

	@Autowired
	private AclUserService aclUserService;

	@GetMapping("/useridByRoleName/{department}/{roleName}/{accessLevelId}")
	public ResponseDto<List<String>> getUserIds(@PathVariable Department department,@PathVariable String roleName,@PathVariable List<String> accessLevelId ) {

		log.info("Fetching user by {},{},{}", department,roleName,accessLevelId);

		return ResponseDto.success("Found User", new ArrayList<>(aclUserService.getUsersForRoles(department, roleName, accessLevelId).keySet()));
	}

	@GetMapping("/useridAccessLevelIdByRoleName/{department}/{roleName}/{accessLevelId}")
	public ResponseDto<Map<String, List<String>>> useridAccessLevelIdByRoleName(@PathVariable Department department, @PathVariable String roleName, @PathVariable List<String> accessLevelId ) {

		log.info("Fetching user by {},{},{}", department,roleName,accessLevelId);

		return ResponseDto.success("Found User", aclUserService.getUsersForRoles(department, roleName, accessLevelId));
	}
	
	@PostMapping("/useridAccessLevelIdByRoleName")
	public ResponseDto<Map<String, List<String>>> getUseridAccessLevelIdByRoleName(@RequestBody UserAccessLevelIdsByRoleNameDto userAccessLevelIdsByRoleNameDto) {

		log.info("Fetching user by {} ", userAccessLevelIdsByRoleNameDto);

		return ResponseDto.success("Found User", aclUserService.getUsersForRoles(userAccessLevelIdsByRoleNameDto.getDepartment(), userAccessLevelIdsByRoleNameDto.getRoleName(), userAccessLevelIdsByRoleNameDto.getAccessLevelId()));
	}

	@GetMapping("/usercontactdetails/{department}/{roleName}/{accessLevelId}")
	public ResponseDto<List<UserContactDetailsResponseDto>> getUserContactDetails(
			@PathVariable Department department,
			@PathVariable String roleName,
			@PathVariable List<String> accessLevelId
	) {
		log.info("Received user contact details request by department {}, roleName {} and accessLevelId {}", department, roleName, accessLevelId);
		return ResponseDto.success("Found contact details of users", aclUserService.getUserContactDetails(department, roleName, accessLevelId));
	}
}