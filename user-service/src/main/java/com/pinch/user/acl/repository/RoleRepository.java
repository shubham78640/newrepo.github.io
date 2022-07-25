/**
 * 
 */
package com.pinch.user.acl.repository;

import com.pinch.user.acl.entity.RoleEntity;
import com.pinch.core.base.enums.AccessLevel;
import com.pinch.core.base.enums.Department;
import com.pinch.core.sqljpa.repository.AbstractJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tech
 *
 * @date 21-Oct-2019
 */
@Repository
public interface RoleRepository extends AbstractJpaRepository<RoleEntity, Long> {

	boolean existsByRoleName(String roleName);

	boolean existsByRoleNameAndDepartment(String roleName,Department department);

	RoleEntity findByRoleName(String roleName);
	
	RoleEntity findByRoleNameAndDepartment(String roleName, Department department);

	List<RoleEntity> findByRoleNameInAndDepartment(List<String> roleNames, Department department);

	List<RoleEntity> findByDepartmentAndAccessLevel(Department department, AccessLevel accessLevel);
}