package com.pinch.user.controller.internal;

import com.pinch.user.acl.service.RoleService;
import com.pinch.core.base.common.dto.ResponseDto;
import com.pinch.core.user.acl.dto.RoleDto;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Log4j2
@RestController
@RequestMapping("internal")
public class InternalRoleController {

    @Autowired
    RoleService roleService;

    @GetMapping("/acl/role/{roleUuid}")
    public ResponseDto<RoleDto> getRole(@PathVariable @NotBlank(message = "Role Id must not be blank") String roleUuid) {

        log.info("Fetching role with id: " + roleUuid);

        return ResponseDto.success("Found Role with Id: " + roleUuid, roleService.getRoleByUuid(roleUuid));
    }
}
