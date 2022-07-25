package com.pinch.user.acl.db.service.impl;

import com.pinch.user.acl.db.service.UserDepartmentLevelRoleDbService;
import com.pinch.user.acl.entity.UserDepartmentLevelRoleEntity;
import com.pinch.user.acl.repository.UserDepartmentLevelRoleRepository;
import com.pinch.core.sqljpa.service.impl.AbstractJpaServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDepartmentLevelRoleDbServiceImpl extends AbstractJpaServiceImpl<UserDepartmentLevelRoleEntity, Long, UserDepartmentLevelRoleRepository> implements UserDepartmentLevelRoleDbService {

    @Autowired
    UserDepartmentLevelRoleRepository userDepartmentLevelRoleRepository;

    @Override
    protected UserDepartmentLevelRoleRepository getJpaRepository() {
        return userDepartmentLevelRoleRepository;
    }

    @Override
    public List<UserDepartmentLevelRoleEntity> findByUserDepartmentLevelUuid(String userDepartmentLevelUuid) {
        return getJpaRepository().findByUserDepartmentLevelUuid(userDepartmentLevelUuid);
    }

    @Override
    public List<UserDepartmentLevelRoleEntity> findByUserDepartmentLevelUuidAndStatus(String userDepartmentLevelUuid, boolean status) {
        return getJpaRepository().findByUserDepartmentLevelUuidAndStatus(userDepartmentLevelUuid, status);
    }

    @Override
    public List<UserDepartmentLevelRoleEntity> findByUserDepartmentLevelUuidAndRoleUuidInAndStatus(String userDepartmentLevelUuid, List<String> rolesUuid, boolean status) {
        return getJpaRepository().findByUserDepartmentLevelUuidAndRoleUuidInAndStatus(userDepartmentLevelUuid, rolesUuid, true);
    }

	@Override
	public List<UserDepartmentLevelRoleEntity> findByRoleUuid(String roleUuid) {
		return getJpaRepository().findByRoleUuid(roleUuid);
	}
}
