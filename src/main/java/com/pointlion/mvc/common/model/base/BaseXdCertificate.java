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

	public void setPid(String pid) {
		set("pid", pid);
	}
	
	public String getPid() {
		return getStr("pid");
	}

	public void setPcertificateTitle(String pcertificateTitle) {
		set("pcertificateTitle", pcertificateTitle);
	}
	
	public String getPcertificateTitle() {
		return getStr("pcertificateTitle");
	}

	public void setProfessional(Integer professional) {
		set("professional", professional);
	}
	
	public Integer getProfessional() {
		return getInt("professional");
	}

	public void setHaveCertificate(Integer haveCertificate) {
		set("have_certificate", haveCertificate);
	}
	
	public Integer getHaveCertificate() {
		return getInt("have_certificate");
	}

	public void setHaveEnddate(Integer haveEnddate) {
		set("have_enddate", haveEnddate);
	}
	
	public Integer getHaveEnddate() {
		return getInt("have_enddate");
	}

	public void setContinueEdu(Integer continueEdu) {
		set("continue_edu", continueEdu);
	}
	
	public Integer getContinueEdu() {
		return getInt("continue_edu");
	}

	public void setOtherIndustries(String otherIndustries) {
		set("other_industries", otherIndustries);
	}
	
	public String getOtherIndustries() {
		return getStr("other_industries");
	}

	public void setCertType(String certType) {
		set("cert_type", certType);
	}
	
	public String getCertType() {
		return getStr("cert_type");
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
