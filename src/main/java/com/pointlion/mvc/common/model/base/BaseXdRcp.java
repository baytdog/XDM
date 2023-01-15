package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXdRcp<M extends BaseXdRcp<M>> extends Model<M> implements IBean {

	public void setId(String id) {
		set("id", id);
	}
	
	public String getId() {
		return getStr("id");
	}

	public void setDeptValue(String deptValue) {
		set("dept_value", deptValue);
	}
	
	public String getDeptValue() {
		return getStr("dept_value");
	}

	public void setDeptName(String deptName) {
		set("dept_name", deptName);
	}
	
	public String getDeptName() {
		return getStr("dept_name");
	}

	public void setOvertimeYear(String overtimeYear) {
		set("overtime_year", overtimeYear);
	}
	
	public String getOvertimeYear() {
		return getStr("overtime_year");
	}

	public void setOvertimeMonth(String overtimeMonth) {
		set("overtime_month", overtimeMonth);
	}
	
	public String getOvertimeMonth() {
		return getStr("overtime_month");
	}

	public void setOvertimeYearMonth(String overtimeYearMonth) {
		set("overtime_year_month", overtimeYearMonth);
	}
	
	public String getOvertimeYearMonth() {
		return getStr("overtime_year_month");
	}

	public void setApproverId(String approverId) {
		set("approver_id", approverId);
	}
	
	public String getApproverId() {
		return getStr("approver_id");
	}

	public void setApproverName(String approverName) {
		set("approver_name", approverName);
	}
	
	public String getApproverName() {
		return getStr("approver_name");
	}

	public void setRemarks(String remarks) {
		set("remarks", remarks);
	}
	
	public String getRemarks() {
		return getStr("remarks");
	}

	public void setStatus(String status) {
		set("status", status);
	}
	
	public String getStatus() {
		return getStr("status");
	}

	public void setCreateUser(String createUser) {
		set("create_user", createUser);
	}
	
	public String getCreateUser() {
		return getStr("create_user");
	}

	public void setCreateDate(String createDate) {
		set("create_date", createDate);
	}
	
	public String getCreateDate() {
		return getStr("create_date");
	}

}
