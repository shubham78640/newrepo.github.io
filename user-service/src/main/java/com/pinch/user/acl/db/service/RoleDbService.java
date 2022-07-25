/**
 * 
 */
package com.pinch.user.acl.db.service;

import com.pinch.user.acl.entity.RoleEntity;
import com.pinch.core.base.enums.AccessLevel;
import com.pinch.core.base.enums.Department;
import com.pinch.core.sqljpa.service.AbstractJpaService;
import com.pinch.core.user.acl.dto.RoleDto;

import java.util.List;

/**
 * @author tech
 *
 * @date 21-Oct-2019
 */
public interface RoleDbService extends AbstractJpaService<RoleEntity, Long> {

	boolean isRoleExists(String roleName);
	
	boolean isRoleExists(String roleName,Department department);

	RoleEntity findByRoleName(String roleName);

	RoleEntity findByRoleNameAndDepartment(String roleName,Department department);
	
	List<RoleEntity> findByDepartmentAndAccessLevel(Department department, AccessLevel accessLevel);

	List<RoleEntity> filter(RoleDto roleDto);

	List<RoleEntity> findByRoleNameAndDepartment(List<String> roleName, Department department);
}