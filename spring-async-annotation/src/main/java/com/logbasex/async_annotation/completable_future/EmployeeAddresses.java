package com.logbasex.async_annotation.completable_future;

import java.io.Serializable;
import java.util.List;

public class EmployeeAddresses implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6822909773594610374L;
	/**
	 * 
	 */

	private List<EmployeeAddress> employeeAddressList;

	public List<EmployeeAddress> getEmployeeAddressList() {
		return employeeAddressList;
	}

	public void setEmployeeAddressList(List<EmployeeAddress> employeeAddressList) {
		this.employeeAddressList = employeeAddressList;
	}

	@Override
	public String toString() {
		return "EmployeeAddresses [employeeAddressList=" + employeeAddressList + "]";
	}

}
