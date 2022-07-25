package com.pinch.user.acl.service;

import java.util.List;

import com.pinch.user.acl.entity.UserDepartmentLevelRoleEntity;

public interface UserDepartmentLevelRoleService {

    List<UserDepartmentLevelRoleEntity> addRoles(String userDepartmentLevelUuid, List<String> rolesUuid);

    void revokeRoles(String uuid, List<String> rolesUuid);

	List<UserDepartmentLevelRoleEntity> findByRoleUuid(String roleUuid);
    
}
