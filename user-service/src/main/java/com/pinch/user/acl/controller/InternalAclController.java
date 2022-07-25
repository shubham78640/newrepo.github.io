package com.pinch.user.acl.controller;

import com.pinch.user.acl.service.AclService;
import com.pinch.core.base.common.dto.ResponseDto;
import com.pinch.core.user.acl.dto.UserDeptLevelRoleNameUrlExpandedDto;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/internal/acl/")
public class InternalAclController {

    @Autowired
    private AclService aclService;

    @GetMapping("user/fe/{email}")
    public ResponseDto<List<UserDeptLevelRoleNameUrlExpandedDto>> getUserRolesFe(@PathVariable @NotBlank(message = "User email must not be blank") String email) {

        log.info("Request received to getUserRolesFe for user : " + email);
        return ResponseDto.success(aclService.getUserDeptLevelRoleNameUrlExpandedDtoFeFromEmail(email));

    }
}
