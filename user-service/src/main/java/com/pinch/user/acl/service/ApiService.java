/**
 * 
 */
package com.pinch.user.acl.service;

import com.pinch.core.base.common.dto.PageResponse;
import com.pinch.core.user.acl.dto.ApiDto;
import com.pinch.core.user.acl.request.dto.AddApiRequestDto;
import com.pinch.core.user.acl.request.dto.UpdateApiRequestDto;

/**
 * @author tech
 *
 * @date 21-Oct-2019
 */
public interface ApiService {

	ApiDto addApi(AddApiRequestDto addApiRequestDto);

	ApiDto updateApi(UpdateApiRequestDto updateApiRequestDto);

	void deleteApi(String apiUuid);

	PageResponse<ApiDto> searchApi(String apiName, String apiUrl, String service, Boolean status, int pageNo, int limit);

}