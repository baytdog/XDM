package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXdAttendanceRcp<M extends BaseXdAttendanceRcp<M>> extends Model<M> implements IBean {

	public void setId(Long id) {
		set("id", id);
	}
	
	public Long getId() {
		return getLong("id");
	}

	public void setAttendidId(String attendidId) {
		set("attendid_id", attendidId);
	}
	
	public String getAttendidId() {
		return getStr("attendid_id");
	}

	public void setMidnightDays(String midnightDays) {
		set("midnight_days", midnightDays);
	}
	
	public String getMidnightDays() {
		return getStr("midnight_days");
	}

	public void setMonworkDays(String monworkDays) {
		set("monwork_days", monworkDays);
	}
	
	public String getMonworkDays() {
		return getStr("monwork_days");
	}

	public void setAllowanceAmount(String allowanceAmount) {
		set("allowance_amount", allowanceAmount);
	}
	
	public String getAllowanceAmount() {
		return getStr("allowance_amount");
	}

	public void setShiftName(String shiftName) {
		set("shift_name", shiftName);
	}
	
	public String getShiftName() {
		return getStr("shift_name");
	}

	public void setRemarks(String remarks) {
		set("remarks", remarks);
	}
	
	public String getRemarks() {
		return getStr("remarks");
	}

	public void setCreateDate(String createDate) {
		set("create_date", createDate);
	}
	
	public String getCreateDate() {
		return getStr("create_date");
	}

	public void setCreateUser(String createUser) {
		set("create_user", createUser);
	}
	
	public String getCreateUser() {
		return getStr("create_user");
	}

}
