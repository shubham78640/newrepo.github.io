/**
 * 
 */
package com.pinch.user.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.pinch.user.entity.UserPropertyMappingEntity;
import com.pinch.core.user.dto.UserPropertyMappingDto;
import com.pinch.core.user.request.dto.UserPropertyMappingRequestDto;

import lombok.experimental.UtilityClass;

/**
 * @author tech
 *
 * @date 13-Oct-2019
 */
@UtilityClass
public class UserPropertyMappingAdapter {

	public static UserPropertyMappingEntity getMappingEntityFromRequest(UserPropertyMappingRequestDto requestDto) {

		return UserPropertyMappingEntity.builder()
				.userId(requestDto.getUserId())
				.propertyId(requestDto.getPropertyId())
				.status(true)
				.build();
	}

	public static List<UserPropertyMappingDto> getMappingDtos(List<UserPropertyMappingEntity> mappingEntities) {

		if (CollectionUtils.isEmpty(mappingEntities)) {
			return new ArrayList<>();
		}

		return mappingEntities.stream().map(UserPropertyMappingAdapter::getMappingDto).collect(Collectors.toList());
	}

	public static UserPropertyMappingDto getMappingDto(UserPropertyMappingEntity mappingEntity) {

		return UserPropertyMappingDto.builder()
				.uuid(mappingEntity.getUuid())
				.createdAt(mappingEntity.getCreatedAt())
				.createdBy(mappingEntity.getCreatedBy())
				.updatedAt(mappingEntity.getUpdatedAt())
				.updatedBy(mappingEntity.getUpdatedBy())
				.status(mappingEntity.isStatus())
				.userId(mappingEntity.getUserId())
				.propertyId(mappingEntity.getPropertyId())
				.build();
	}
}