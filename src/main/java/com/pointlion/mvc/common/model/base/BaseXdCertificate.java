package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXdCertificate<M extends BaseXdCertificate<M>> extends Model<M> implements IBean {

	public void setId(String id) {
		set("id", id);
	}
	
	public String getId() {
		return getStr("id");
	}

	public void setCertificateTitle(String certificateTitle) {
		set("certificateTitle", certificateTitle);
	}
	
	public String getCertificateTitle() {
		return getStr("certificateTitle");
	}

	public void setNum(String num) {
		set("num", num);
	}
	
	public String getNum() {
		return getStr("num");
	}

	public void setPid(java.lang.String pid) {
		set("pid", pid);
	}

	public java.lang.String getPid() {
		return getStr("pid");
	}

	public void setPcertificateTitle(java.lang.String pcertificateTitle) {
		set("pcertificateTitle", pcertificateTitle);
	}

	public java.lang.String getPcertificateTitle() {
		return getStr("pcertificateTitle");
	}

	public void setHaveCertificate(Integer haveCertificate) {
		set("haveCertificate", haveCertificate);
	}
	
	public Integer getHaveCertificate() {
		return getInt("haveCertificate");
	}

	public void setHaveEndDate(Integer haveEndDate) {
		set("haveEndDate", haveEndDate);
	}
	
	public Integer getHaveEndDate() {
		return getInt("haveEndDate");
	}

	public void setContinuEdu(Integer continuEdu) {
		set("continuEdu", continuEdu);
	}
	
	public Integer getContinuEdu() {
		return getInt("continuEdu");
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
