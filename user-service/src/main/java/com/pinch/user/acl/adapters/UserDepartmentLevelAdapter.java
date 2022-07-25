package com.pinch.user.acl.adapters;


import com.pinch.user.acl.entity.UserDepartmentLevelEntity;
import com.pinch.core.user.acl.request.dto.AddUserDeptLevelRequestDto;

import lombok.experimental.UtilityClass;

import java.util.TreeSet;
import java.util.stream.Collectors;

@UtilityClass
public class UserDepartmentLevelAdapter {

    public static UserDepartmentLevelEntity getEntityFromRequest(AddUserDeptLevelRequestDto addUserDeptLevelRequestDto) {
        return UserDepartmentLevelEntity.builder()
                .userUuid(addUserDeptLevelRequestDto.getUserUuid())
                .department(addUserDeptLevelRequestDto.getDepartment())
                .accessLevel(addUserDeptLevelRequestDto.getAccessLevel())
                .csvAccessLevelEntityUuid(String.join(",",
                        addUserDeptLevelRequestDto.getAccessLevelEntityListUuid().stream().collect(Collectors.toCollection(TreeSet::new)).stream().collect(Collectors.toList())))
                .build();
    }

}
