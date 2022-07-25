package com.pinch.user.acl.service;

import com.pinch.user.acl.entity.UserDepartmentLevelEntity;
import com.pinch.core.user.acl.request.dto.AddUserDeptLevelRequestDto;

public interface UserDepartmentLevelService {
    UserDepartmentLevelEntity add(AddUserDeptLevelRequestDto addUserDeptLevelRequestDto);

    void delete(UserDepartmentLevelEntity userDepartmentLevelEntity);

    void revokeAccessLevelEntityForDepartmentOfLevel(AddUserDeptLevelRequestDto addUserDeptLevelRequestDto);

	UserDepartmentLevelEntity findByUuid(String userDepartmentLevelUuid);
}
