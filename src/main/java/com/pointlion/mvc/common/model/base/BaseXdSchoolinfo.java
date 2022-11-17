package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXdSchoolinfo<M extends BaseXdSchoolinfo<M>> extends Model<M> implements IBean {

	public void setId(Integer id) {
		set("id", id);
	}
	
	public Integer getId() {
		return getInt("id");
	}

	public void setEid(Integer eid) {
		set("eid", eid);
	}
	
	public Integer getEid() {
		return getInt("eid");
	}

	public void setEdubackgroud(Integer edubackgroud) {
		set("edubackgroud", edubackgroud);
	}
	
	public Integer getEdubackgroud() {
		return getInt("edubackgroud");
	}

	public void setSchool(String school) {
		set("school", school);
	}
	
	public String getSchool() {
		return getStr("school");
	}

	public void setSpecialty(String specialty) {
		set("specialty", specialty);
	}
	
	public String getSpecialty() {
		return getStr("specialty");
	}

	public void setCtime(String ctime) {
		set("ctime", ctime);
	}
	
	public String getCtime() {
		return getStr("ctime");
	}

	public void setCuser(String cuser) {
		set("cuser", cuser);
	}
	
	public String getCuser() {
		return getStr("cuser");
	}

}
