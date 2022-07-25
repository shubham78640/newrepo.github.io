/**
 * 
 */
package com.pinch.user.acl.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pinch.user.acl.db.service.ApiDbService;
import com.pinch.user.acl.entity.ApiEntity;
import com.pinch.user.acl.repository.ApiRepository;
import com.pinch.core.sqljpa.service.impl.AbstractJpaServiceImpl;

/**
 * @author tech
 *
 * @date 21-Oct-2019
 */
@Service
public class ApiDbServiceImpl extends AbstractJpaServiceImpl<ApiEntity, Long, ApiRepository> implements ApiDbService {

	@Autowired
	private ApiRepository apiRepository;

	@Override
	protected ApiRepository getJpaRepository() {
		return apiRepository;
	}

	@Override
	public boolean existsByActionUrl(String actionUrl) {
		return getJpaRepository().existsByActionUrl(actionUrl);
	}

	@Override
	public boolean existsByApiName(String apiName) {
		return getJpaRepository().existsByApiName(apiName);
	}

}