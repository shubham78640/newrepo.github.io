/**
 * 
 */
package com.pinch.user.acl.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.pinch.core.transformation.client.cache.TransformationCache;
import com.pinch.user.acl.adapters.UserDepartmentLevelRoleAdapter;
import com.pinch.user.acl.db.service.ApiDbService;
import com.pinch.user.acl.db.service.RoleAccessDbService;
import com.pinch.user.acl.db.service.RoleDbService;
import com.pinch.user.acl.db.service.UserDepartmentLevelDbService;
import com.pinch.user.acl.db.service.UserDepartmentLevelRoleDbService;
import com.pinch.user.acl.entity.ApiEntity;
import com.pinch.user.acl.entity.RoleAccessEntity;
import com.pinch.user.acl.entity.RoleEntity;
import com.pinch.user.acl.entity.UserDepartmentLevelEntity;
import com.pinch.user.acl.entity.UserDepartmentLevelRoleEntity;
import com.pinch.user.acl.service.AclService;
import com.pinch.user.entity.UserEntity;
import com.pinch.user.service.UserService;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.pinch.core.user.acl.dto.UserDeptLevelRoleNameUrlExpandedDto;
import com.pinch.core.user.acl.enums.RoleAccessType;

import lombok.extern.log4j.Log4j2;

/**
 * @author tech
 *
 * @date 23-Oct-2019
 *
 **/
@Log4j2
@Service
public class AclServiceImpl implements AclService {

	@Autowired
	private UserService userService;

	@Autowired
	private ApiDbService apiDbService;

	@Autowired
	private RoleDbService roleDbService;

	@Autowired
	private RoleAccessDbService roleAccessDbService;

	@Autowired
	private UserDepartmentLevelDbService userDepartmentLevelDbService;

	@Autowired
	private UserDepartmentLevelRoleDbService userDepartmentLevelRoleDbService;

	@Autowired
	private TransformationCache transformationCache;

	@Override
	public boolean isAccessible(String userId, String url) {

		Set<String> accessibleUrlList = getAccessibleUrlList(userId);
		return accessibleUrlList.contains(url);
	}

	@Override
	public List<UserDeptLevelRoleNameUrlExpandedDto> getUserDeptLevelRoleNameUrlExpandedDtoFe(String userUuid) {

		List<UserDeptLevelRoleNameUrlExpandedDto> userDeptLevelRoleNameUrlExpandedDtoList = getUserDeptLevelRoleNameUrlExpandedDto(userUuid);

		for (UserDeptLevelRoleNameUrlExpandedDto userDeptLevelRoleNameUrlExpandedDto : userDeptLevelRoleNameUrlExpandedDtoList) {
			userDeptLevelRoleNameUrlExpandedDto.setUrlList(new ArrayList<>());
		}

		return userDeptLevelRoleNameUrlExpandedDtoList;
	}

	@Override
	public List<UserDeptLevelRoleNameUrlExpandedDto> getUserDeptLevelRoleNameUrlExpandedDtoBe(String userUuid) {

		log.info("Fetching User Department role name for user: {}", userUuid);

		return getUserDeptLevelRoleNameUrlExpandedDto(userUuid);
	}

	@Override
	public Set<String> getAccessibleUrlList(String userUuid) {
		List<UserDeptLevelRoleNameUrlExpandedDto> userDeptLevelRoleNameUrlExpandedDtoList = getUserDeptLevelRoleNameUrlExpandedDto(userUuid);
		Set<String> accessibleUrlList = new HashSet<>();
		for (UserDeptLevelRoleNameUrlExpandedDto userDeptLevelRoleNameUrlExpandedDto : userDeptLevelRoleNameUrlExpandedDtoList) {
			accessibleUrlList.addAll(userDeptLevelRoleNameUrlExpandedDto.getUrlList());
		}
		return accessibleUrlList;
	}

	@Override
	public List<UserDeptLevelRoleNameUrlExpandedDto> getUserDeptLevelRoleNameUrlExpandedDtoFeFromEmail(String email) {
		List<UserEntity> userEntityList = userService.getUserByEmail(email.trim());
		if (CollectionUtils.isEmpty(userEntityList)) {
			return Collections.emptyList();
		}
		List<UserDeptLevelRoleNameUrlExpandedDto> userDeptLevelRoleNameUrlExpandedDtoList = new ArrayList<>();
		for (UserEntity userEntity : userEntityList) {
			userDeptLevelRoleNameUrlExpandedDtoList.addAll(getUserDeptLevelRoleNameUrlExpandedDtoFe(userEntity.getUuid()));
		}
		return userDeptLevelRoleNameUrlExpandedDtoList;
	}

	private List<UserDeptLevelRoleNameUrlExpandedDto> getUserDeptLevelRoleNameUrlExpandedDto(String userUuid) {

		userService.assertActiveUserByUserUuid(userUuid);

		List<UserDeptLevelRoleNameUrlExpandedDto> userDeptLevelRoleNameUrlExpandedDtoList = new ArrayList<>();

		List<UserDepartmentLevelEntity> userDepartmentLevelEntityList = userDepartmentLevelDbService.findByUserUuidAndStatus(userUuid, true);

		for (UserDepartmentLevelEntity userDepartmentLevelEntity : userDepartmentLevelEntityList) {
			
			Pair<List<String>, List<String>> roleUuidApiUuidList = getRoleUuidApiUuidListOfUser(userDepartmentLevelEntity);
			
			List<RoleEntity> roleEntityList = roleDbService.findByUuidInAndStatus(roleUuidApiUuidList.getFirst(), true);
			List<ApiEntity> apiEntityList = apiDbService.findByUuidInAndStatus(roleUuidApiUuidList.getSecond(), true);

			if (CollectionUtils.isNotEmpty(apiEntityList) || CollectionUtils.isNotEmpty(roleEntityList)) {

				userDeptLevelRoleNameUrlExpandedDtoList.add(
						UserDepartmentLevelRoleAdapter.getUserDeptLevelRoleNameUrlExpandedDto(userDepartmentLevelEntity, roleEntityList, apiEntityList, transformationCache));
			}
		}

		return userDeptLevelRoleNameUrlExpandedDtoList;
	}

	private Pair<List<String>, List<String>> getRoleUuidApiUuidListOfUser(UserDepartmentLevelEntity userDepartmentLevelEntity) {

		List<UserDepartmentLevelRoleEntity> userDepartmentLevelRoleEntityList = userDepartmentLevelRoleDbService.findByUserDepartmentLevelUuidAndStatus(userDepartmentLevelEntity.getUuid(), true);
		Set<String> finalRoleUuidSet = new HashSet<>();
		Set<String> finalApiUuidSet = new HashSet<>();

		List<String> roleUuidListParent = new ArrayList<>(userDepartmentLevelRoleEntityList.stream().map(entity -> entity.getRoleUuid()).collect(Collectors.toSet()));
		finalRoleUuidSet.addAll(roleUuidListParent);

		List<RoleAccessEntity> roleAccessEntityListChild;
		Set<String> roleUuidSetChild;
		Set<String> apiUuidSetChild;

		while (CollectionUtils.isNotEmpty(roleUuidListParent)) {
			roleAccessEntityListChild = roleAccessDbService.findByRoleUuidInAndStatus(roleUuidListParent, true);
			roleUuidSetChild =
					roleAccessEntityListChild.stream().filter(entity -> RoleAccessType.ROLE.equals(entity.getRoleAccessType())).map(entity -> entity.getAccessUuid()).collect(Collectors.toSet());
			apiUuidSetChild =
					roleAccessEntityListChild.stream().filter(entity -> RoleAccessType.API.equals(entity.getRoleAccessType())).map(entity -> entity.getAccessUuid()).collect(Collectors.toSet());
			roleUuidListParent = roleUuidSetChild.stream().filter(child -> !finalRoleUuidSet.contains(child)).collect(Collectors.toList());
			finalRoleUuidSet.addAll(roleUuidListParent);
			finalApiUuidSet.addAll(apiUuidSetChild);
		}

		return Pair.of(new ArrayList<>(finalRoleUuidSet), new ArrayList<>(finalApiUuidSet));

	}
}