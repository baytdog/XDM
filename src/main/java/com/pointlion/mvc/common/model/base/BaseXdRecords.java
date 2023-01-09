package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXdRecords<M extends BaseXdRecords<M>> extends Model<M> implements IBean {

	public void setId(Integer id) {
		set("id", id);
	}
	
	public Integer getId() {
		return getInt("id");
	}

	public void setEid(String eid) {
		set("eid", eid);
	}
	
	public String getEid() {
		return getStr("eid");
	}

	public void setFieldType(Integer fieldType) {
		set("field_type", fieldType);
	}
	
	public Integer getFieldType() {
		return getInt("field_type");
	}

	public void setEffictDate(String effictDate) {
		set("effict_date", effictDate);
	}
	
	public String getEffictDate() {
		return getStr("effict_date");
	}

	public void setOldValue(String oldValue) {
		set("old_value", oldValue);
	}
	
	public String getOldValue() {
		return getStr("old_value");
	}

	public void setNewValue(String newValue) {
		set("new_value", newValue);
	}
	
	public String getNewValue() {
		return getStr("new_value");
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

	public void setCuser(String cuser) {
		set("cuser", cuser);
	}
	
	public String getCuser() {
		return getStr("cuser");
	}

	public void setCtime(String ctime) {
		set("ctime", ctime);
	}
	
	public String getCtime() {
		return getStr("ctime");
	}

	public void setBackup1(String backup1) {
		set("backup1", backup1);
	}
	
	public String getBackup1() {
		return getStr("backup1");
	}

	public void setBackup2(String backup2) {
		set("backup2", backup2);
	}
	
	public String getBackup2() {
		return getStr("backup2");
	}

	public void setBackup3(String backup3) {
		set("backup3", backup3);
	}
	
	public String getBackup3() {
		return getStr("backup3");
	}

}