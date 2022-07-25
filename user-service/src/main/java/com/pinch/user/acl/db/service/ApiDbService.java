/**
 * 
 */
package com.pinch.user.acl.db.service;

import com.pinch.user.acl.entity.ApiEntity;
import com.pinch.core.sqljpa.service.AbstractJpaService;

/**
 * @author tech
 *
 * @date 21-Oct-2019
 */
public interface ApiDbService extends AbstractJpaService<ApiEntity, Long> {

	boolean existsByActionUrl(String actionUrl);

	boolean existsByApiName(String apiName);
}