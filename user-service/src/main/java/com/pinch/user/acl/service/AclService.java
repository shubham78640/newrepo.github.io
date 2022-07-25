package com.pinch.user.acl.service;

import com.pinch.core.user.acl.dto.UserDeptLevelRoleNameUrlExpandedDto;

import java.util.List;
import java.util.Set;

public interface AclService {
    boolean isAccessible(String userId, String url);

    List<UserDeptLevelRoleNameUrlExpandedDto> getUserDeptLevelRoleNameUrlExpandedDtoFe(String userUuid);

    List<UserDeptLevelRoleNameUrlExpandedDto> getUserDeptLevelRoleNameUrlExpandedDtoBe(String userUuid);

    Set<String> getAccessibleUrlList(String userUuid);

    List<UserDeptLevelRoleNameUrlExpandedDto> getUserDeptLevelRoleNameUrlExpandedDtoFeFromEmail(String email);
}
