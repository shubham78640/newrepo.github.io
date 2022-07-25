/**
 * 
 */
package com.pinch.user.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinch.user.Projections.UserView;
import com.pinch.user.entity.UserEntity;
import com.pinch.core.base.enums.Department;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pinch.core.sqljpa.repository.AbstractJpaRepository;
import com.pinch.core.user.enums.UserType;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
@Repository
public interface UserRepository extends AbstractJpaRepository<UserEntity, Long> {

	UserEntity findByMobileAndIsoCode(String mobile, String isoCode);

	/**
	 * @author piyush srivastava
	 * @param nameStartsWith String
	 * @return List
	 */
	@Query("SELECT u FROM com.pinch.user.entity.UserEntity u"
			+ " WHERE CONCAT_WS(' ', u.userProfile.firstName, u.userProfile.middleName, u.userProfile.lastName)"
			+ " LIKE :name%")
	List<UserEntity> searchByName(@Param("name") String nameStartsWith);

	List<UserEntity> findByEmail(String email);

	List<UserEntity> findByUserType(UserType userType);

	UserEntity findByMobileAndUserType(String mobileNo, UserType userType);

	UserEntity findByUuidAndEmail(String userUuid, String email);

	List<UserEntity> findByMobileIn(Set<String> mobileNos);

	UserEntity findTop1ByEmailOrderByCreatedAtDesc(String email);

//	@Query("SELECT new com.pinch.core.user.acl.dto.UuidByEmailDto(user.email, user.uuid) " +
//			"FROM UserEntity user " +
//			"WHERE user.department = :dept AND user.email IN (:emails)")
//	List<UserView> getUuidByEmailAndDept(@Param("emails") List<String> emails, @Param("dept") Department department);


	List<UserView> findByEmailInAndStatus(List<String> emails, Boolean status);

}