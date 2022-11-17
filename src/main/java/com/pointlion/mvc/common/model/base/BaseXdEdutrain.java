package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXdEdutrain<M extends BaseXdEdutrain<M>> extends Model<M> implements IBean {

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

	public void setEnrolldate(String enrolldate) {
		set("enrolldate", enrolldate);
	}
	
	public String getEnrolldate() {
		return getStr("enrolldate");
	}

	public void setGraduatdate(String graduatdate) {
		set("graduatdate", graduatdate);
	}
	
	public String getGraduatdate() {
		return getStr("graduatdate");
	}

	public void setTrainOrgname(String trainOrgname) {
		set("trainOrgname", trainOrgname);
	}
	
	public String getTrainOrgname() {
		return getStr("trainOrgname");
	}

	public void setMajor(String major) {
		set("major", major);
	}
	
	public String getMajor() {
		return getStr("major");
	}

	public void setEdubg(String edubg) {
		set("edubg", edubg);
	}
	
	public String getEdubg() {
		return getStr("edubg");
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
