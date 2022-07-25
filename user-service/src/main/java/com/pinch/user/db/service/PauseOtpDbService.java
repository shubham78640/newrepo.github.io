/**
 * 
 */
package com.pinch.user.db.service;

import com.pinch.user.entity.PauseOtpEntity;
import com.pinch.core.sqljpa.service.AbstractJpaService;

/**
 * @author tech
 *
 * @date 10-Oct-2019
 */
public interface PauseOtpDbService extends AbstractJpaService<PauseOtpEntity, Long> {

	boolean checkIfMobileExist(String mobile);
	
	PauseOtpEntity findByMobileNumber(String mobile);

}