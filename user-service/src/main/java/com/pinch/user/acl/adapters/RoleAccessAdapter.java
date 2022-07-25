package com.pinch.user.acl.adapters;

import java.util.ArrayList;
import java.util.List;

import com.pinch.user.acl.entity.RoleAccessEntity;
import com.pinch.core.base.enums.AccessLevel;
import com.pinch.core.user.acl.dto.RoleAccessDto;
import com.pinch.core.user.enums.EnumListing;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RoleAccessAdapter {

	public static RoleAccessDto getDto(RoleAccessEntity roleAccessEntity) {

		return RoleAccessDto.builder()
				.uuid(roleAccessEntity.getUuid())
				.createdAt(roleAccessEntity.getCreatedAt())
				.createdBy(roleAccessEntity.getCreatedBy())
				.updatedAt(roleAccessEntity.getUpdatedAt())
				.updatedBy(roleAccessEntity.getUpdatedBy())
				.status(roleAccessEntity.isStatus())
				.roleUuid(roleAccessEntity.getRoleUuid())
				.accessUuid(roleAccessEntity.getAccessUuid())
				.roleAccessType(roleAccessEntity.getRoleAccessType())
				.build();

	}

	public List<EnumListing<AccessLevel>> getAccessLevelEnumAsEnumListing() {
		List<EnumListing<AccessLevel>> data = new ArrayList<>();

		for (AccessLevel accessLevel : AccessLevel.values()) {
			data.add(EnumListing.of(accessLevel, String.valueOf(accessLevel.getLevelNum())));
		}
		
		return data;
	}

}