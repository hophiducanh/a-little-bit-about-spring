package com.tellyouiam.alittlebitaboutspring.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "customers", uniqueConstraints = {
	@UniqueConstraint(columnNames = "phone")
})
public class Customers {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //auto increment
	@Column(name = "customerNumber")
	private Integer customerNumber;
	
	@Column(name = "customerName")
	private String customerName;
	
	@Column(name = "contactLastName")
	private String contactLastName;
	
	@Column(name = "contactFirstName")
	private String contactFirstName;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "addressLine1")
	private String addressLine1;
	
	@Column(name = "addressLine2")
	private String addressLine2;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "postalCode")
	private String postalCode;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "salesRepEmployeeNumber")
	private Integer salesRepEmployeeNumber;
	
	@Column(name = "creditLimit")
	private BigDecimal creditLimit;
	
	public Integer getCustomerNumber() {
		return this.customerNumber;
	}
	
	public void setCustomerNumber(Integer customerNumber) {
		this.customerNumber = customerNumber;
	}
	
	public String getCustomerName() {
		return this.customerName;
	}
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getContactLastName() {
		return this.contactLastName;
	}
	
	public void setContactLastName(String contactLastName) {
		this.contactLastName = contactLastName;
	}
	
	public String getContactFirstName() {
		return this.contactFirstName;
	}
	
	public void setContactFirstName(String contactFirstName) {
		this.contactFirstName = contactFirstName;
	}
	
	public String getPhone() {
		return this.phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getAddressLine1() {
		return this.addressLine1;
	}
	
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	
	public String getAddressLine2() {
		return this.addressLine2;
	}
	
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getPostalCode() {
		return this.postalCode;
	}
	
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public String getCountry() {
		return this.country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public Integer getSalesRepEmployeeNumber() {
		return this.salesRepEmployeeNumber;
	}
	
	public void setSalesRepEmployeeNumber(Integer salesRepEmployeeNumber) {
		this.salesRepEmployeeNumber = salesRepEmployeeNumber;
	}
	
	public BigDecimal getCreditLimit() {
		return this.creditLimit;
	}
	
	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}
}
