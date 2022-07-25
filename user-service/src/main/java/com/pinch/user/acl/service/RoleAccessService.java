package com.pinch.user.acl.service;

import com.pinch.user.acl.entity.RoleEntity;
import com.pinch.core.user.acl.dto.RoleAccessDto;
import com.pinch.core.user.acl.request.dto.AddRoleAccessDto;
import com.pinch.core.user.acl.request.dto.UpdateRoleAccessDto;

public interface RoleAccessService {

	RoleAccessDto addRoleAccess(AddRoleAccessDto addRoleAccessDto);

	void revokeRoleAccess(AddRoleAccessDto addRoleAccessDto);

	RoleAccessDto updateRoleAccess(UpdateRoleAccessDto updateRoleAccessDto);

	void assertSameDepartmentAssignment(RoleEntity roleEntity1, RoleEntity roleEntity2);

	void assertParentChildAssignment(RoleEntity parentRoleEntity, RoleEntity childRoleEntity);

}