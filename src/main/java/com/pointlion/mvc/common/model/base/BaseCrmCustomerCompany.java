package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCrmCustomerCompany<M extends BaseCrmCustomerCompany<M>> extends Model<M> implements IBean {

	public void setId(String id) {
		set("id", id);
	}
	
	public String getId() {
		return getStr("id");
	}

	public void setUserid(java.lang.String userid) {
		set("userid", userid);
	}

	public java.lang.String getUserid() {
		return getStr("userid");
	}

	public void setCreateUserName(String createUserName) {
		set("create_user_name", createUserName);
	}
	
	public String getCreateUserName() {
		return getStr("create_user_name");
	}

	public void setOrgId(java.lang.String orgId) {
		set("org_id", orgId);
	}

	public java.lang.String getOrgId() {
		return getStr("org_id");
	}

	public void setCreateOrgName(java.lang.String orgName) {
		set("create_org_name", orgName);
	}

	public java.lang.String getCreateOrgName() {
		return getStr("create_org_name");
	}

	public void setCreateTime(String createTime) {
		set("create_time", createTime);
	}
	
	public String getCreateTime() {
		return getStr("create_time");
	}

	public void setCode(String code) {
		set("code", code);
	}
	
	public String getCode() {
		return getStr("code");
	}

	public void setName(String name) {
		set("name", name);
	}
	
	public String getName() {
		return getStr("name");
	}

	public void setAddress(String address) {
		set("address", address);
	}
	
	public String getAddress() {
		return getStr("address");
	}

	public void setContacts(String contacts) {
		set("contacts", contacts);
	}
	
	public String getContacts() {
		return getStr("contacts");
	}

	public void setContactsOrg(String contactsOrg) {
		set("contacts_org", contactsOrg);
	}
	
	public String getContactsOrg() {
		return getStr("contacts_org");
	}

	public void setMobile(String mobile) {
		set("mobile", mobile);
	}
	
	public String getMobile() {
		return getStr("mobile");
	}

	public void setEmail(String email) {
		set("email", email);
	}
	
	public String getEmail() {
		return getStr("email");
	}

	public void setNote(String note) {
		set("note", note);
	}
	
	public String getNote() {
		return getStr("note");
	}

	public void setNote1(String note1) {
		set("note1", note1);
	}
	
	public String getNote1() {
		return getStr("note1");
	}

	public void setNote2(String note2) {
		set("note2", note2);
	}
	
	public String getNote2() {
		return getStr("note2");
	}

	public void setBusiness(String business) {
		set("business", business);
	}
	
	public String getBusiness() {
		return getStr("business");
	}

	public void setIfSubmit(String ifSubmit) {
		set("if_submit", ifSubmit);
	}
	
	public String getIfSubmit() {
		return getStr("if_submit");
	}

	public void setIfComplete(String ifComplete) {
		set("if_complete", ifComplete);
	}
	
	public String getIfComplete() {
		return getStr("if_complete");
	}

	public void setIfAgree(String ifAgree) {
		set("if_agree", ifAgree);
	}
	
	public String getIfAgree() {
		return getStr("if_agree");
	}

	public void setProcInsId(String procInsId) {
		set("proc_ins_id", procInsId);
	}
	
	public String getProcInsId() {
		return getStr("proc_ins_id");
	}

	public void setDes(String des) {
		set("des", des);
	}

	public String getDes() {
		return getStr("des");
	}
}
