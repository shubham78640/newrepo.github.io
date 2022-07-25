package com.pinch.user.acl.db.service;

import com.pinch.user.acl.entity.RoleAccessEntity;
import com.pinch.core.sqljpa.service.AbstractJpaService;
import com.pinch.core.user.acl.enums.RoleAccessType;

import java.util.List;

public interface RoleAccessDbService extends AbstractJpaService<RoleAccessEntity, Long> {
    RoleAccessEntity findByRoleUuidAndAccessUuidAndRoleAccessType(String roleUuid, String accessUuid, RoleAccessType roleAccessType);

    List<RoleAccessEntity> findByRoleUuidInAndRoleAccessTypeAndStatus(List<String> roleUuidListParent, RoleAccessType role, boolean status);

    List<RoleAccessEntity> findByRoleUuidInAndStatus(List<String> roleUuidListParent, boolean status);
}
