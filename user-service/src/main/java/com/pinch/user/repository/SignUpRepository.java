package com.pinch.user.repository;

import org.springframework.stereotype.Repository;

import com.pinch.user.entity.SignupEntity;
import com.pinch.core.sqljpa.repository.AbstractJpaRepository;

@Repository
public interface SignUpRepository extends AbstractJpaRepository<SignupEntity, Long> {

}
