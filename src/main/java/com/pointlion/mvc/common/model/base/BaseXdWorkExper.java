package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import com.pointlion.annotation.ChangeFields;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXdWorkExper<M extends BaseXdWorkExper<M>> extends Model<M> implements IBean {

	public void setId(String id) {
		set("id", id);
	}
	@ChangeFields(value="id",desc = "id")
	public String getId() {
		return getStr("id");
	}

	public void setEid(String eid) {
		set("eid", eid);
	}
	@ChangeFields(value="eid",desc = "id")
	public String getEid() {
		return getStr("eid");
	}

	public void setEntrydate(String entrydate) {
		set("entrydate", entrydate);
	}
	@ChangeFields(value="entrydate",desc = "入职时间")
	public String getEntrydate() {
		return getStr("entrydate");
	}

	public void setDepartdate(String departdate) {
		set("departdate", departdate);
	}
	@ChangeFields(value="departdate",desc = "离职时间")
	public String getDepartdate() {
		return getStr("departdate");
	}

	public void setServiceunit(String serviceunit) {
		set("serviceunit", serviceunit);
	}
	@ChangeFields(value="serviceunit",desc = "服务单位")
	public String getServiceunit() {
		return getStr("serviceunit");
	}

	public void setJob(String job) {
		set("job", job);
	}
	@ChangeFields(value="job",desc = "职务")
	public String getJob() {
		return getStr("job");
	}

	public void setAddr(String addr) {
		set("addr", addr);
	}
	@ChangeFields(value="addr",desc = "地点")
	public String getAddr() {
		return getStr("addr");
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
