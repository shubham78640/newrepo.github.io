/**
 * 
 */
package com.pinch.user.service;

import java.util.List;

import com.pinch.core.base.common.dto.PageResponse;
import com.pinch.core.user.dto.UserPropertyMappingDto;
import com.pinch.core.user.request.dto.UserPropertyMappingRequestDto;

/**
 * @author tech
 *
 * @date 13-Oct-2019
 */
public interface UserPropertyMappingService {

	void createUserPropertyMapping(List<UserPropertyMappingRequestDto> mappingRequestDtos);

	List<UserPropertyMappingDto> getUserPropertyMappings(String userId);

	PageResponse<UserPropertyMappingDto> searchUserPropertyMappings(String userId, String propertyId, int pageNo, int limit);

	void deleteMapping(String mappingId);

}