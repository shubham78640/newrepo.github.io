package com.pinch.user.acl.db.service;

import com.pinch.user.acl.entity.UserDepartmentLevelRoleEntity;
import com.pinch.core.sqljpa.service.AbstractJpaService;

import java.util.List;

public interface UserDepartmentLevelRoleDbService extends AbstractJpaService<UserDepartmentLevelRoleEntity, Long> {
    List<UserDepartmentLevelRoleEntity> findByUserDepartmentLevelUuid(String userDepartmentLevelUuid);

    List<UserDepartmentLevelRoleEntity> findByUserDepartmentLevelUuidAndStatus(String userDepartmentLevelUuid, boolean status);
    
    List<UserDepartmentLevelRoleEntity> findByRoleUuid(String roleUuid);

    List<UserDepartmentLevelRoleEntity> findByUserDepartmentLevelUuidAndRoleUuidInAndStatus(String userDepartmentLevelUuid, List<String> rolesUuid, boolean status);
}
