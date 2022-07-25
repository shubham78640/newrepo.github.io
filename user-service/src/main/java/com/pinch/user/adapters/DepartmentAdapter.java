package com.pinch.user.adapters;

import java.util.ArrayList;
import java.util.List;

import com.pinch.core.base.enums.Department;
import com.pinch.core.user.enums.EnumListing;

import lombok.experimental.UtilityClass;

/**
 * @author piyush srivastava "piyush@pinch.com"
 *
 * @date 16-Apr-2020
 *
 */
@UtilityClass
public class DepartmentAdapter {

	public List<EnumListing<Department>> getDepartmentEnumAsEnumListing() {

		List<EnumListing<Department>> data = new ArrayList<>();

		for (Department department : Department.values()) {
			data.add(EnumListing.of(department, department.getDepartmentName()));
		}
		
		return data;
	}
}