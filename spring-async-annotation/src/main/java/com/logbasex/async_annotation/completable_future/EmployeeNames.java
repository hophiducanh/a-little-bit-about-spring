package com.logbasex.async_annotation.completable_future;

import java.io.Serializable;
import java.util.List;

public class EmployeeNames implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1773599508061743940L;
	private List<EmployeeName> employeeNameList;

	public List<EmployeeName> getEmployeeNameList() {
		return employeeNameList;
	}

	public void setEmployeeNameList(List<EmployeeName> employeeNameList) {
		this.employeeNameList = employeeNameList;
	}

	@Override
	public String toString() {
		return "EmployeeNames [employeeNameList=" + employeeNameList + "]";
	}

}
