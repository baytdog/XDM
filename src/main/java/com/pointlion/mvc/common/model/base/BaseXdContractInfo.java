package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXdContractInfo<M extends BaseXdContractInfo<M>> extends Model<M> implements IBean {

	public void setId(String id) {
		set("id", id);
	}
	
	public String getId() {
		return getStr("id");
	}

	public void setEid(String eid) {
		set("eid", eid);
	}
	
	public String getEid() {
		return getStr("eid");
	}


	public void setEmpName(java.lang.String empName) {
		set("emp_name", empName);
	}

	public java.lang.String getEmpName() {
		return getStr("emp_name");
	}

	public void setContractstartdate(String contractstartdate) {
		set("contractstartdate", contractstartdate);
	}
	
	public String getContractstartdate() {
		return getStr("contractstartdate");
	}

	public void setContractenddate(String contractenddate) {
		set("contractenddate", contractenddate);
	}
	
	public String getContractenddate() {
		return getStr("contractenddate");
	}

	public void setContractclauses(Integer contractclauses) {
		set("contractclauses", contractclauses);
	}
	
	public Integer getContractclauses() {
		return getInt("contractclauses");
	}

	public void setContractnature(String contractnature) {
		set("contractnature", contractnature);
	}
	
	public String getContractnature() {
		return getStr("contractnature");
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
