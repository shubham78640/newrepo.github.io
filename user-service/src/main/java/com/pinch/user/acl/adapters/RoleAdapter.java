/**
 * 
 */
package com.pinch.user.acl.adapters;

import com.pinch.user.acl.entity.RoleEntity;
import com.pinch.core.user.acl.dto.RoleDto;
import com.pinch.core.user.acl.request.dto.AddRoleRequestDto;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tech
 *
 * @date 22-Oct-2019
 *
 **/
@UtilityClass
public class RoleAdapter {

	public static RoleEntity getEntityFromRequest(AddRoleRequestDto addRoleRequestDto) {

		return RoleEntity.builder()
				.roleName(addRoleRequestDto.getRoleName())
				.department(addRoleRequestDto.getDepartment())
				.accessLevel(addRoleRequestDto.getAccessLevel())
				.build();

	}

	public static List<RoleDto> getDtoList(List<RoleEntity> roleEntities) {

		if (CollectionUtils.isEmpty(roleEntities)) {
			return new ArrayList<>();
		}

		return roleEntities.stream().map(RoleAdapter::getDto).collect(Collectors.toList());

	}

	public static RoleDto getDto(RoleEntity roleEntity) {

		return RoleDto.builder()
				.uuid(roleEntity.getUuid())
				.createdAt(roleEntity.getCreatedAt())
				.createdBy(roleEntity.getCreatedBy())
				.updatedAt(roleEntity.getUpdatedAt())
				.updatedBy(roleEntity.getUpdatedBy())
				.status(roleEntity.isStatus())
				.roleName(roleEntity.getRoleName())
				.department(roleEntity.getDepartment())
				.accessLevel(roleEntity.getAccessLevel())
				.build();

	}
}