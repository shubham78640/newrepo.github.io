/**
 * 
 */
package com.pinch.user.db.service;

import com.pinch.user.entity.UserEntity;
import com.pinch.core.base.enums.Department;
import org.springframework.data.jpa.domain.Specification;

import com.pinch.core.sqljpa.service.AbstractJpaService;
import com.pinch.core.user.dto.UserFilterDto;
import com.pinch.core.user.enums.UserType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
public interface UserDbService extends AbstractJpaService<UserEntity, Long> {
	
	UserEntity getUserForMobile(String mobile, String isoCode);

	Specification<UserEntity> getSearchQuery(UserFilterDto userFilterDto);

	List<UserEntity> findByEmail(String email);

	UserEntity findByMobileAndUserType(String userMobile,UserType userType);

	List<UserEntity> findByUserType(UserType userType);
	
	UserEntity findByMobile(String mobile);

	UserEntity findByUuidAndEmail(String userUuid, String email);

	List<UserEntity> findByMobileIn(Set<String> mobileNos);

	UserEntity findTop1ByEmailOrderByCreatedAtDesc(String email);

	Map<String, String> getUuidByEmail(List<String> emails);
}