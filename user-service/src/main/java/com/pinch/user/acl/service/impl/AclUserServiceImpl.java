package com.pinch.user.acl.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.pinch.core.user.acl.request.dto.AddUserDeptLevelRoleByEmailRequestDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pinch.user.acl.adapters.RoleAdapter;
import com.pinch.user.acl.adapters.UserDepartmentLevelRoleAdapter;
import com.pinch.user.acl.db.service.RoleDbService;
import com.pinch.user.acl.db.service.UserDepartmentLevelDbService;
import com.pinch.user.acl.db.service.UserDepartmentLevelRoleDbService;
import com.pinch.user.acl.entity.RoleEntity;
import com.pinch.user.acl.entity.UserDepartmentLevelEntity;
import com.pinch.user.acl.entity.UserDepartmentLevelRoleEntity;
import com.pinch.user.acl.service.AclService;
import com.pinch.user.acl.service.AclUserService;
import com.pinch.user.acl.service.RoleService;
import com.pinch.user.acl.service.UserDepartmentLevelRoleService;
import com.pinch.user.acl.service.UserDepartmentLevelService;
import com.pinch.user.adapters.UserAdapter;
import com.pinch.user.db.service.UserDbService;
import com.pinch.user.entity.UserEntity;
import com.pinch.user.service.UserService;
import com.pinch.core.base.enums.AccessLevel;
import com.pinch.core.base.enums.Department;
import com.pinch.core.base.exception.ApiValidationException;
import com.pinch.core.kafka.producer.NotificationProducer;
import com.pinch.core.user.acl.dto.RoleDto;
import com.pinch.core.user.acl.dto.UserDeptLevelRoleDto;
import com.pinch.core.user.acl.dto.UserDeptLevelRoleListDto;
import com.pinch.core.user.acl.dto.UserDeptLevelRoleNameUrlExpandedDto;
import com.pinch.core.user.acl.dto.UserRoleSnapshot;
import com.pinch.core.user.acl.request.dto.AddUserDeptLevelRequestDto;
import com.pinch.core.user.acl.request.dto.AddUserDeptLevelRoleRequestDto;
import com.pinch.core.user.dto.response.UserContactDetailsResponseDto;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AclUserServiceImpl implements AclUserService {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private RoleDbService roleDbService;

	@Autowired
	private UserDbService userDbService;

	@Autowired
	private UserDepartmentLevelService userDepartmentLevelService;

	@Autowired
	private UserDepartmentLevelDbService userDepartmentLevelDbService;

	@Autowired
	private UserDepartmentLevelRoleService userDepartmentLevelRoleService;

	@Autowired
	private UserDepartmentLevelRoleDbService userDepartmentLevelRoleDbService;

	@Autowired
	private AclService aclService;

	@Autowired
	private NotificationProducer notificationProducer;

	@Value("${kafka.topic.role}")
	private String roleTopic;

	@Override
	public void addRole(AddUserDeptLevelRoleRequestDto addUserDeptLevelRoleDto) {

		userService.assertActiveUserByUserUuid(addUserDeptLevelRoleDto.getUserUuid());

		AddUserDeptLevelRequestDto addUserDeptLevelRequestDto = new AddUserDeptLevelRequestDto(addUserDeptLevelRoleDto);

		UserDepartmentLevelEntity userDepartmentLevelEntity = userDepartmentLevelService.add(addUserDeptLevelRequestDto);

		userDepartmentLevelRoleService.addRoles(userDepartmentLevelEntity.getUuid(), addUserDeptLevelRoleDto.getRolesUuid());
		publishCurrentRoleSnapshot(addUserDeptLevelRoleDto.getUserUuid());
	}

	@Override
	public void revokeAllRolesOfDepartment(String userUuid, Department department) {

		userService.assertActiveUserByUserUuid(userUuid);

		List<UserDepartmentLevelEntity> userDepartmentLevelEntityList = userDepartmentLevelDbService.findByUserUuidAndDepartment(userUuid, department);
		if (CollectionUtils.isEmpty(userDepartmentLevelEntityList)) {
			throw new ApiValidationException("User doesn't have any access in this department");
		}

		for (UserDepartmentLevelEntity userDepartmentLevelEntity : userDepartmentLevelEntityList) {
			userDepartmentLevelService.delete(userDepartmentLevelEntity);
		}
		publishCurrentRoleSnapshot(userUuid);
		return;

	}

	@Override
	public List<UserDeptLevelRoleDto> getActiveUserDeptLevelRole(String userUuid) {
		userService.assertActiveUserByUserUuid(userUuid);
		return getUserDeptLevelRole(userUuid);
	}

	@Override
	public List<UserDeptLevelRoleDto> getUserDeptLevelRole(String userUuid) {

		List<UserDeptLevelRoleDto> userDeptLevelRoleDtoList = new ArrayList<>();
		List<UserDepartmentLevelRoleEntity> userDepartmentLevelRoleEntityList;

		List<UserDepartmentLevelEntity> userDepartmentLevelEntityList = userDepartmentLevelDbService.findByUserUuidAndStatus(userUuid, true);

		for (UserDepartmentLevelEntity userDepartmentLevelEntity : userDepartmentLevelEntityList) {
			userDepartmentLevelRoleEntityList = userDepartmentLevelRoleDbService.findByUserDepartmentLevelUuidAndStatus(userDepartmentLevelEntity.getUuid(), true);
			userDeptLevelRoleDtoList.add(UserDepartmentLevelRoleAdapter.getUserDeptLevelRoleDto(userDepartmentLevelEntity, userDepartmentLevelRoleEntityList));
		}
		return userDeptLevelRoleDtoList;
	}

	@Override
	public List<RoleDto> getUserRoles(String userUuid) {
		List<RoleDto> roleDtoList = new ArrayList<>();
		List<UserDepartmentLevelRoleEntity> userDepartmentLevelRoleEntityList;
		List<UserDepartmentLevelEntity> userDepartmentLevelEntityList = userDepartmentLevelDbService.findByUserUuidAndStatus(userUuid, true);
		List<String> roleUuids;

		for (UserDepartmentLevelEntity userDepartmentLevelEntity : userDepartmentLevelEntityList) {
			userDepartmentLevelRoleEntityList = userDepartmentLevelRoleDbService.findByUserDepartmentLevelUuidAndStatus(userDepartmentLevelEntity.getUuid(), true);
			roleUuids = userDepartmentLevelRoleEntityList.parallelStream().map(UserDepartmentLevelRoleEntity::getRoleUuid).collect(Collectors.toList());
			List<RoleEntity> roleEntities = roleDbService.findByUuidInAndStatus(roleUuids, true);
			roleDtoList.addAll(RoleAdapter.getDtoList(roleEntities));
		}
		return roleDtoList;
	}

	@Override
	public void revokeAllRolesOfDepartmentOfLevel(String userUuid, Department department, AccessLevel accessLevel) {
		userService.assertActiveUserByUserUuid(userUuid);

		List<UserDepartmentLevelEntity> userDepartmentLevelEntityList = userDepartmentLevelDbService.findByUserUuidAndDepartmentAndAccessLevel(userUuid, department, accessLevel);
		if (CollectionUtils.isEmpty(userDepartmentLevelEntityList)) {
			throw new ApiValidationException("User doesn't have any access in this department");
		}

		for (UserDepartmentLevelEntity userDepartmentLevelEntity : userDepartmentLevelEntityList) {
			userDepartmentLevelService.delete(userDepartmentLevelEntity);
		}
		publishCurrentRoleSnapshot(userUuid);
		return;
	}

	@Override
	public void revokeAccessLevelEntityForDepartmentOfLevel(AddUserDeptLevelRequestDto addUserDeptLevelRequestDto) {

		userService.assertActiveUserByUserUuid(addUserDeptLevelRequestDto.getUserUuid());

		userDepartmentLevelService.revokeAccessLevelEntityForDepartmentOfLevel(addUserDeptLevelRequestDto);
		publishCurrentRoleSnapshot(addUserDeptLevelRequestDto.getUserUuid());
	}

	@Override
	public void revokeRolesForDepartmentOfLevel(UserDeptLevelRoleListDto userDeptLevelRoleListDto) {
		userService.assertActiveUserByUserUuid(userDeptLevelRoleListDto.getUserUuid());

		UserDepartmentLevelEntity userDepartmentLevelEntity = userDepartmentLevelDbService.findByUserUuidAndDepartmentAndAccessLevelAndStatus(userDeptLevelRoleListDto.getUserUuid(),
				userDeptLevelRoleListDto.getDepartment(), userDeptLevelRoleListDto.getAccessLevel(), true);

		if (null == userDepartmentLevelEntity) {
			throw new ApiValidationException("Unable to revoke roles, User doesn't exist at this level in the department");
		}

		userDepartmentLevelRoleService.revokeRoles(userDepartmentLevelEntity.getUuid(), userDeptLevelRoleListDto.getRolesUuid());
		publishCurrentRoleSnapshot(userDeptLevelRoleListDto.getUserUuid());
	}

	@Override
	public Map<String, List<String>> getUsersForRoles(Department department, String roleName, List<String> accessLevelEntityList) {

		log.info("Got request to get list of userid by rolename {} and department {}", roleName, department);

		RoleDto roleDto = roleService.findByRoleNameAndDepartment(roleName, department);

		Map<String, List<String>> userIdAccessLevelIdListMap = new HashMap<>();

		if (Objects.nonNull(roleDto) && roleDto.getDepartment().equals(department)) {

			List<UserDepartmentLevelRoleEntity> departmentLevelRoleEntities = userDepartmentLevelRoleDbService.findByRoleUuid(roleDto.getUuid());

			if (CollectionUtils.isNotEmpty(departmentLevelRoleEntities)) {

				List<String> uuids = departmentLevelRoleEntities.stream().map(UserDepartmentLevelRoleEntity::getUserDepartmentLevelUuid).collect(Collectors.toList());

				List<UserDepartmentLevelEntity> departmentLevelEntities = userDepartmentLevelDbService.findByUuidInAndAccessLevel(uuids, roleDto.getAccessLevel());

				if (CollectionUtils.isNotEmpty(departmentLevelEntities)) {

					departmentLevelEntities.forEach(entity -> {

						Set<String> accessLevelUuids = new HashSet<>(Arrays.asList((entity.getCsvAccessLevelEntityUuid().split(","))));

						for (String accessLevelEntity : accessLevelEntityList) {
							if (accessLevelUuids.contains(accessLevelEntity)) {
								List<String> accessLevelIds = userIdAccessLevelIdListMap.getOrDefault(entity.getUserUuid(), new ArrayList<>());
								accessLevelIds.add(accessLevelEntity);
								userIdAccessLevelIdListMap.put(entity.getUserUuid(), accessLevelIds);
							}
						}
						// if (!Collections.disjoint(accessLevelEntityList, accessLevelUuids)) {
						// userIds.add(entity.getUserUuid());
						// }
					});
				}
			}

		}

		return userIdAccessLevelIdListMap;
	}

	@Override
	public List<UserContactDetailsResponseDto> getUserContactDetails(Department department, String roleName, List<String> accessLevelEntity) {
		List<String> userUuids = new ArrayList<>(getUsersForRoles(department, roleName, accessLevelEntity).keySet());

		if (CollectionUtils.isEmpty(userUuids)) {
			return Collections.emptyList();
		}

		List<UserEntity> userEntities = userDbService.findByUuidInAndStatus(userUuids, true);

		if (CollectionUtils.isEmpty(userEntities)) {
			return Collections.emptyList();
		}

		return userEntities.parallelStream().map(UserAdapter::convertToContactResponseDto).collect(Collectors.toList());
	}

	@Override
	public void bulkAddRole(AddUserDeptLevelRoleByEmailRequestDto addUserDeptLevelRoleByEmailRequestDto) {
		Map<String,String> userUuids = userDbService.getUuidByEmail(addUserDeptLevelRoleByEmailRequestDto.getEmails());

		addUserDeptLevelRoleByEmailRequestDto.getEmails()
						.forEach(email->{
							if (!userUuids.keySet().contains(email)){
								throw new ApiValidationException("No user exists with email id: "+email);
							}
						});

		userUuids
				.forEach((email,uuid) -> {
							addRole(
									AddUserDeptLevelRoleRequestDto
											.builder()
											.rolesUuid(addUserDeptLevelRoleByEmailRequestDto.getRolesUuid())
											.userUuid(uuid)
											.department(addUserDeptLevelRoleByEmailRequestDto.getDepartment())
											.accessLevel(addUserDeptLevelRoleByEmailRequestDto.getAccessLevel())
											.accessLevelEntityListUuid(addUserDeptLevelRoleByEmailRequestDto.getAccessLevelEntityListUuid())
											.build()
							);
						}
				);
	}

	private void publishCurrentRoleSnapshot(String userUuid) {
		List<UserDeptLevelRoleNameUrlExpandedDto> data = aclService.getUserDeptLevelRoleNameUrlExpandedDtoBe(userUuid);
		UserRoleSnapshot userRoleSnapshot = UserRoleSnapshot.builder().userUuid(userUuid).userDeptLevelRoles(data).build();
		notificationProducer.publish(roleTopic, UserRoleSnapshot.class.getName(), userRoleSnapshot);
	}
}
