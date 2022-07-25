package com.pinch.user.acl.db.service.impl;

import com.pinch.user.acl.db.service.RoleAccessDbService;
import com.pinch.user.acl.entity.RoleAccessEntity;
import com.pinch.user.acl.repository.RoleAccessRepository;
import com.pinch.core.sqljpa.service.impl.AbstractJpaServiceImpl;
import com.pinch.core.user.acl.enums.RoleAccessType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleAccessDbServiceImpl extends AbstractJpaServiceImpl<RoleAccessEntity, Long, RoleAccessRepository> implements RoleAccessDbService {

    @Autowired
    RoleAccessRepository roleAccessRepository;

    @Override
    protected RoleAccessRepository getJpaRepository() {
        return roleAccessRepository;
    }


    @Override
    public RoleAccessEntity findByRoleUuidAndAccessUuidAndRoleAccessType(String roleUuid, String accessUuid, RoleAccessType roleAccessType) {
        return getJpaRepository().findByRoleUuidAndAccessUuidAndRoleAccessType(roleUuid, accessUuid, roleAccessType);
    }

    @Override
    public List<RoleAccessEntity> findByRoleUuidInAndRoleAccessTypeAndStatus(List<String> roleUuidListParent, RoleAccessType role, boolean status) {
        return getJpaRepository().findByRoleUuidInAndRoleAccessTypeAndStatus(roleUuidListParent, role, status);
    }

    @Override
    public List<RoleAccessEntity> findByRoleUuidInAndStatus(List<String> roleUuidListParent, boolean status) {
        return getJpaRepository().findByRoleUuidInAndStatus(roleUuidListParent, status);
    }
}
