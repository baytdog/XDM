package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOaDepartmentsFiles<M extends BaseOaDepartmentsFiles<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public void setKeywords(java.lang.String keywords) {
		set("keywords", keywords);
	}
	
	public java.lang.String getKeywords() {
		return getStr("keywords");
	}

	public void setFiledecription(java.lang.String filedecription) {
		set("filedecription", filedecription);
	}
	
	public java.lang.String getFiledecription() {
		return getStr("filedecription");
	}

	public void setDepartmentid(java.lang.String departmentid) {
		set("departmentid", departmentid);
	}
	
	public java.lang.String getDepartmentid() {
		return getStr("departmentid");
	}

	public void setOrgid(java.lang.String orgid) {
		set("orgid", orgid);
	}
	
	public java.lang.String getOrgid() {
		return getStr("orgid");
	}

	public void setCuserid(java.lang.String cuserid) {
		set("cuserid", cuserid);
	}
	
	public java.lang.String getCuserid() {
		return getStr("cuserid");
	}

	public void setCusername(java.lang.String cusername) {
		set("cusername", cusername);
	}
	
	public java.lang.String getCusername() {
		return getStr("cusername");
	}

	public void setCtime(java.lang.String ctime) {
		set("ctime", ctime);
	}
	
	public java.lang.String getCtime() {
		return getStr("ctime");
	}

	public void setBackup1(java.lang.String backup1) {
		set("backup1", backup1);
	}
	
	public java.lang.String getBackup1() {
		return getStr("backup1");
	}

	public void setBakcup2(java.lang.String bakcup2) {
		set("bakcup2", bakcup2);
	}
	
	public java.lang.String getBakcup2() {
		return getStr("bakcup2");
	}

	public void setBakcup3(java.lang.String bakcup3) {
		set("bakcup3", bakcup3);
	}
	
	public java.lang.String getBakcup3() {
		return getStr("bakcup3");
	}

}
