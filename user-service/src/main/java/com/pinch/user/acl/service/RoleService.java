package com.pinch.user.acl.service;

import com.pinch.core.base.enums.AccessLevel;
import com.pinch.core.base.enums.Department;
import com.pinch.core.user.acl.dto.RoleDto;
import com.pinch.core.user.acl.request.dto.AddRoleRequestDto;

import java.util.List;

public interface RoleService {
    RoleDto addRole(AddRoleRequestDto addRoleRequestDto);

    RoleDto getRoleByUuid(String roleUuid);
    
    RoleDto findByRoleName(String roleName);
    
    RoleDto findByRoleNameAndDepartment(String roleName,Department department);

    List<RoleDto> findByDepartmentAndAccessLevel(Department department, AccessLevel accessLevel);

    List<RoleDto> filter(String roleName, Department department, AccessLevel accessLevel);

    List<RoleDto> filter(RoleDto roleDto);

    List<RoleDto> findByRoleNameInAndDepartment(List<String> roleNames, Department department);
}
