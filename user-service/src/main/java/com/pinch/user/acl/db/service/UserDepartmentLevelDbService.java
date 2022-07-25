package com.pinch.user.acl.db.service;

import com.pinch.user.acl.entity.UserDepartmentLevelEntity;
import com.pinch.core.base.enums.AccessLevel;
import com.pinch.core.base.enums.Department;
import com.pinch.core.sqljpa.service.AbstractJpaService;

import java.util.List;

public interface UserDepartmentLevelDbService extends AbstractJpaService<UserDepartmentLevelEntity, Long> {
    List<UserDepartmentLevelEntity> findByUserUuidAndDepartmentAndStatus(String userUuid, Department department, boolean status);

    List<UserDepartmentLevelEntity> findByUserUuidAndDepartment(String userUuid, Department department);

    List<UserDepartmentLevelEntity> findByUserUuidAndStatus(String userUuid, boolean status);

    UserDepartmentLevelEntity findByUserUuidAndDepartmentAndAccessLevelAndStatus(String userUuid, Department department, AccessLevel accessLevel, boolean status);

    List<UserDepartmentLevelEntity> findByUserUuidAndDepartmentAndAccessLevel(String userUuid, Department department, AccessLevel accessLevel);

    List<UserDepartmentLevelEntity> findByUuidInAndAccessLevel(List<String> uuids, AccessLevel accessLevel);
}
