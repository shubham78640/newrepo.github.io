package com.pinch.user.acl.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pinch.user.acl.adapters.RoleAdapter;
import com.pinch.user.acl.db.service.RoleDbService;
import com.pinch.user.acl.entity.RoleEntity;
import com.pinch.user.acl.service.RoleAccessService;
import com.pinch.user.acl.service.RoleService;
import com.pinch.user.kafka.service.KafkaUserService;
import com.pinch.core.base.enums.AccessLevel;
import com.pinch.core.base.enums.Department;
import com.pinch.core.base.exception.ApiValidationException;
import com.pinch.core.user.acl.dto.RoleDto;
import com.pinch.core.user.acl.enums.RoleAccessType;
import com.pinch.core.user.acl.request.dto.AddRoleAccessDto;
import com.pinch.core.user.acl.request.dto.AddRoleRequestDto;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDbService roleDbService;

	@Autowired
	private RoleAccessService roleAccessService;

	@Autowired
	private KafkaUserService kafkaUserService;

	private static String PARENT_UUID_TO_SKIP_PARENT_ROLE = "SELF";

	@Override
	public RoleDto addRole(AddRoleRequestDto addRoleRequestDto) {
		if (roleDbService.isRoleExists(addRoleRequestDto.getRoleName(), addRoleRequestDto.getDepartment())) {
			throw new ApiValidationException("Role already exists with given name " + addRoleRequestDto.getRoleName());
		}

		RoleEntity parentRoleEntity = roleDbService.findByUuid(addRoleRequestDto.getParentRoleUuid());
		if (!PARENT_UUID_TO_SKIP_PARENT_ROLE.equalsIgnoreCase(addRoleRequestDto.getParentRoleUuid()) && null == parentRoleEntity) {
			throw new ApiValidationException("Parent role doesn't exist for parentUuid " + addRoleRequestDto.getParentRoleUuid());
		}

		RoleEntity roleEntity = RoleAdapter.getEntityFromRequest(addRoleRequestDto);

		if (!PARENT_UUID_TO_SKIP_PARENT_ROLE.equalsIgnoreCase(addRoleRequestDto.getParentRoleUuid())) {
			roleAccessService.assertSameDepartmentAssignment(parentRoleEntity, roleEntity);
			roleAccessService.assertParentChildAssignment(parentRoleEntity, roleEntity);
		}

		roleDbService.save(roleEntity);

		if (!PARENT_UUID_TO_SKIP_PARENT_ROLE.equalsIgnoreCase(addRoleRequestDto.getParentRoleUuid())) {
			roleAccessService.addRoleAccess(
					AddRoleAccessDto.builder()
							.roleUuid(addRoleRequestDto.getParentRoleUuid())
							.accessUuid(roleEntity.getUuid())
							.roleAccessType(RoleAccessType.ROLE)
							.build());
		}
		RoleDto roleDto = RoleAdapter.getDto(roleEntity);
		kafkaUserService.sendNewRoleToKafka(roleDto);
		return roleDto;
	}

	@Override
	public RoleDto getRoleByUuid(String roleUuid) {
		RoleEntity roleEntity = roleDbService.findByUuid(roleUuid);

		if (null == roleEntity) {
			throw new ApiValidationException("Unable to find rule by uuid " + roleUuid);
		}

		return RoleAdapter.getDto(roleEntity);
	}

	@Override
	public List<RoleDto> findByDepartmentAndAccessLevel(Department department, AccessLevel accessLevel) {
		List<RoleEntity> roleEntityList = roleDbService.findByDepartmentAndAccessLevel(department, accessLevel);
		return RoleAdapter.getDtoList(roleEntityList);
	}

	@Override
	public List<RoleDto> filter(String roleName, Department department, AccessLevel accessLevel) {
		return filter(
				RoleDto.builder()
						.roleName(roleName)
						.accessLevel(accessLevel)
						.department(department)
						.build());
	}

	@Override
	public List<RoleDto> filter(RoleDto roleDto) {
		List<RoleEntity> roleEntities = roleDbService.filter(roleDto);
		return RoleAdapter.getDtoList(roleEntities);
	}

	@Override
	public RoleDto findByRoleName(String roleName) {

		log.info("Searching role by name: {}", roleName);

		RoleEntity roleEntity = roleDbService.findByRoleName(roleName);

		if (null == roleEntity) {
			throw new ApiValidationException("Unable to find rule by roleName " + roleName);
		}

		return RoleAdapter.getDto(roleEntity);
	}

	@Override
	public RoleDto findByRoleNameAndDepartment(String roleName, Department department) {

		log.info("Searching role by name: {}", roleName);

		RoleEntity roleEntity = roleDbService.findByRoleNameAndDepartment(roleName, department);

		if (null == roleEntity) {
			throw new ApiValidationException("Unable to find rule by roleName " + roleName);
		}

		return RoleAdapter.getDto(roleEntity);
	}

	@Override
	public List<RoleDto> findByRoleNameInAndDepartment(List<String> roleNames, Department department) {

		log.info("Searching roles by name: {}", roleNames);

		List<RoleEntity> roleEntity = roleDbService.findByRoleNameAndDepartment(roleNames, department);

		if (null == roleEntity) {
			throw new ApiValidationException("Unable to find rule by roleNames ");
		}

		return roleEntity.stream().map(f->RoleAdapter.getDto(f)).collect(Collectors.toList());
	}

}