package com.pinch.user.acl.repository;

import com.pinch.user.acl.entity.UserDepartmentLevelRoleEntity;
import com.pinch.core.sqljpa.repository.AbstractJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDepartmentLevelRoleRepository extends AbstractJpaRepository<UserDepartmentLevelRoleEntity, Long>  {

    List<UserDepartmentLevelRoleEntity> findByUserDepartmentLevelUuid(String userDepartmentLevelUuid);

    List<UserDepartmentLevelRoleEntity> findByUserDepartmentLevelUuidAndStatus(String userDepartmentLevelUuid, boolean status);
    
    List<UserDepartmentLevelRoleEntity> findByRoleUuid(String roleUuid);

    List<UserDepartmentLevelRoleEntity> findByUserDepartmentLevelUuidAndRoleUuidInAndStatus(String userDepartmentLevelUuid, List<String> rolesUuid, boolean status);
}
