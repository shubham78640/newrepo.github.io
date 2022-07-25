/**
 * 
 */
package com.pinch.user.acl.controller;

import com.pinch.user.acl.adapters.RoleAccessAdapter;
import com.pinch.user.acl.service.RoleAccessService;
import com.pinch.user.acl.service.RoleService;
import com.pinch.core.base.common.dto.ResponseDto;
import com.pinch.core.base.enums.AccessLevel;
import com.pinch.core.base.enums.Department;
import com.pinch.core.user.acl.dto.RoleAccessDto;
import com.pinch.core.user.acl.dto.RoleDto;
import com.pinch.core.user.acl.request.dto.AddRoleAccessDto;
import com.pinch.core.user.acl.request.dto.AddRoleRequestDto;
import com.pinch.core.user.acl.request.dto.CheckRoleNamesDto;
import com.pinch.core.user.acl.request.dto.UpdateRoleAccessDto;
import com.pinch.core.user.enums.EnumListing;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author tech
 *
 * @date 22-Oct-2019
 * 
 **/
@Log4j2
@RestController
@RequestMapping("acl/role")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@Autowired
	private RoleAccessService roleAccessService;

	@PostMapping("add")
	public ResponseDto<RoleDto> addRole(@RequestBody @Valid AddRoleRequestDto addRoleRequestDto) {

		log.info("Received request to add new role: " + addRoleRequestDto);

		return ResponseDto.success("Added New Role: " + addRoleRequestDto.getRoleName(), roleService.addRole(addRoleRequestDto));
	}

	@GetMapping("{roleUuid}")
	public ResponseDto<RoleDto> getRole(@PathVariable @NotBlank(message = "Role Id must not be blank") String roleUuid) {

		log.info("Fetching role with id: " + roleUuid);

		return ResponseDto.success("Found Role with Id: " + roleUuid, roleService.getRoleByUuid(roleUuid));
	}

	@GetMapping("getRoles")
	public ResponseDto<List<RoleDto>> getRoleByDepartmentAndLevel(
			@RequestParam(name = "department") Department department,
			@RequestParam(name = "accessLevel") AccessLevel accessLevel) {
		log.info("Fetching roles by Department {} And Level {} ", department, accessLevel);

		return ResponseDto.success("Found Role with Department: " + department + " level: " + accessLevel, roleService.findByDepartmentAndAccessLevel(department, accessLevel));
	}

	@PostMapping("getRolesByDepartmentAndNames")
	public ResponseDto<List<RoleDto>> getRoleByDepartmentAndRoleName(@RequestBody CheckRoleNamesDto checkRoleNamesDto) {
		log.info("Checked roles are present in Department {} ",checkRoleNamesDto.getDepartment());
		return ResponseDto.success("Roles exist in Department: " + checkRoleNamesDto.getDepartment(),roleService.findByRoleNameInAndDepartment(checkRoleNamesDto.getRoleNames(),checkRoleNamesDto.getDepartment()));
	}

	@GetMapping("list")
	public ResponseDto<List<RoleDto>> filterRoles(
			@RequestParam(name = "department", required = false) Department department,
			@RequestParam(name = "accessLevel", required = false) AccessLevel accessLevel,
			@RequestParam(name = "roleName", required = false) String roleName) {
		log.info("Fetching roles by Department {} And AccessLevel {} And roleName {}", department, accessLevel, roleName);
		List<RoleDto> roleDtoList = roleService.filter(roleName, department, accessLevel);
		return ResponseDto.success("Found " + roleDtoList.size() + " Roles with Department: " + department + " and AccessLevel: " + accessLevel, roleDtoList);
	}

	@PostMapping("addRoleAccess")
	public ResponseDto<RoleAccessDto> addRoleAccess(@RequestBody @Valid AddRoleAccessDto addRoleAccessDto) {

		log.info("Received request for role assignment : " + addRoleAccessDto);

		return ResponseDto.success("Added role access successfully", roleAccessService.addRoleAccess(addRoleAccessDto));

	}

	@PostMapping("revokeRoleAccess")
	public ResponseDto<Void> revokeRole(@RequestBody @Valid AddRoleAccessDto addRoleAccessDto) {
		log.info("Received request to revoke role assignment : " + addRoleAccessDto);
		roleAccessService.revokeRoleAccess(addRoleAccessDto);
		return ResponseDto.success("Role access revocation successful");
	}

	@PostMapping("updateRoleAccess")
	public ResponseDto<RoleAccessDto> updateRoleAccess(@RequestBody @Valid UpdateRoleAccessDto updateRoleAccessDto) {
		log.info("Received request to update role: " + updateRoleAccessDto);
		return ResponseDto.success("Updated Role: " + updateRoleAccessDto.getRoleAccessUuid(), roleAccessService.updateRoleAccess(updateRoleAccessDto));
	}

	@GetMapping("accesslevel/list")
	public ResponseDto<List<EnumListing<AccessLevel>>> getAccessLevelList() {
		log.info("Received request for retrieving access level list api.");
		return ResponseDto.success("Found Access Levels", RoleAccessAdapter.getAccessLevelEnumAsEnumListing());
	}

}