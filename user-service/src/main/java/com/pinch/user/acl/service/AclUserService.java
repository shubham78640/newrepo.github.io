package com.pinch.user.acl.service;

import com.pinch.core.base.enums.AccessLevel;
import com.pinch.core.base.enums.Department;
import com.pinch.core.user.acl.dto.RoleDto;
import com.pinch.core.user.acl.dto.UserDeptLevelRoleDto;
import com.pinch.core.user.acl.dto.UserDeptLevelRoleListDto;
import com.pinch.core.user.acl.request.dto.AddUserDeptLevelRequestDto;
import com.pinch.core.user.acl.request.dto.AddUserDeptLevelRoleByEmailRequestDto;
import com.pinch.core.user.acl.request.dto.AddUserDeptLevelRoleRequestDto;
import com.pinch.core.user.dto.response.UserContactDetailsResponseDto;

import java.util.List;
import java.util.Map;

public interface AclUserService {
	void addRole(AddUserDeptLevelRoleRequestDto addUserDeptLevelRoleDto);

	void revokeAllRolesOfDepartment(String userUuid, Department department);

	List<UserDeptLevelRoleDto> getActiveUserDeptLevelRole(String userUuid);

	List<UserDeptLevelRoleDto> getUserDeptLevelRole(String userUuid);

	List<RoleDto> getUserRoles(String userUuid);

	void revokeAllRolesOfDepartmentOfLevel(String userUuid, Department department, AccessLevel accessLevel);

	void revokeAccessLevelEntityForDepartmentOfLevel(AddUserDeptLevelRequestDto addUserDeptLevelRequestDto);

	void revokeRolesForDepartmentOfLevel(UserDeptLevelRoleListDto userDeptLevelRoleListDto);

    Map<String, List<String>> getUsersForRoles(Department department,String roleName,List<String> accessLevelEntity);

	List<UserContactDetailsResponseDto> getUserContactDetails(Department department, String roleName, List<String> accessLevelEntity);

	void bulkAddRole(AddUserDeptLevelRoleByEmailRequestDto addUserDeptLevelRoleByEmailRequestDto);
}
