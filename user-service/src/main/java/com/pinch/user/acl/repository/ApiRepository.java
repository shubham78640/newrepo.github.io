/**
 * 
 */
package com.pinch.user.acl.repository;

import org.springframework.stereotype.Repository;

import com.pinch.user.acl.entity.ApiEntity;
import com.pinch.core.sqljpa.repository.AbstractJpaRepository;

/**
 * @author tech
 *
 * @date 21-Oct-2019
 */
@Repository
public interface ApiRepository extends AbstractJpaRepository<ApiEntity, Long> {

	boolean existsByActionUrl(String actionUrl);

    boolean existsByApiName(String apiName);
}