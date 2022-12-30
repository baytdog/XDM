package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import com.pointlion.annotation.ChangeFields;
import com.pointlion.annotation.NeedMapping;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXdEmployee<M extends BaseXdEmployee<M>> extends Model<M> implements IBean {

	public void setId(String id) {
		set("id", id);
	}
	@ChangeFields(value="id",desc = "id")
	public String getId() {
		return getStr("id");
	}

	public void setName(String name) {
		set("name", name);
	}
	@ChangeFields(value="name",desc = "姓名")
	public String getName() {
		return getStr("name");
	}

	public void setEmpnum(String empnum) {
		set("empnum", empnum);
	}
	@ChangeFields(value="empnum",desc = "员工编号")
	public String getEmpnum() {
		return getStr("empnum");
	}

	public void setEmprelation(String emprelation) {
		set("emprelation", emprelation);
	}
	@ChangeFields(value="emprelation",desc = "员工关系")
	public String getEmprelation() {
		return getStr("emprelation");
	}
	@NeedMapping(dictName = "sex")
	public void setGender(String gender) {
		set("gender", gender);
	}
/*	@ChangeFields(value="gender",desc = "性别")*/
	public String getGender() {
		return getStr("gender");
	}
	@NeedMapping(dictName = "ismarry")
	public void setMarried(String married) {
		set("married", married);
	}
	@ChangeFields(value="married",desc = "婚姻状况")
	public String getMarried() {
		return getStr("married");
	}

	public void setAge(Integer age) {
		set("age", age);
	}
	/*@ChangeFields(value="age",desc = "年龄")*/
	public Integer getAge() {
		return getInt("age");
	}
	@NeedMapping(dictName = "nation")
	public void setNational(String national) {
		set("national", national);
	}
	@ChangeFields(value = "national",desc = "民族")
	public String getNational() {
		return getStr("national");
	}
	@NeedMapping(dictName = "polity")
	public void setPoliticsstatus(String politicsstatus) {
		set("politicsstatus", politicsstatus);
	}
	@ChangeFields(value = "politicsstatus",desc = "政治面貌")
	public String getPoliticsstatus() {
		return getStr("politicsstatus");
	}
	@NeedMapping(dictName = "edu")
	public void setTopedu(String topedu) {
		set("topedu", topedu);
	}
	@ChangeFields(value = "topedu",desc = "最高学历")
	public String getTopedu() {
		return getStr("topedu");
	}

	public void setSpecialty(String specialty) {
		set("specialty", specialty);
	}
	@ChangeFields(value = "specialty",desc = "特长")
	public String getSpecialty() {
		return getStr("specialty");
	}

	public void setTopdegree(String topdegree) {
		set("topdegree", topdegree);
	}
	@ChangeFields(value = "topdegree",desc = "最高学位")
	public String getTopdegree() {
		return getStr("topdegree");
	}

	public void setIdnum(String Idnum) {
		set("Idnum", Idnum);
	}
	@ChangeFields(value = "Idnum",desc = "身份证")
	public String getIdnum() {
		return getStr("Idnum");
	}

	public void setCensusregister(String censusregister) {
		set("censusregister", censusregister);
	}
	@ChangeFields(value = "censusregister",desc = "户籍")
	public String getCensusregister() {
		return getStr("censusregister");
	}

	public void setCensusregisteraddr(String censusregisteraddr) {
		set("censusregisteraddr", censusregisteraddr);
	}
	@ChangeFields(value = "censusregisteraddr",desc = "户籍地址")
	public String getCensusregisteraddr() {
		return getStr("censusregisteraddr");
	}

	public void setBirthplace(String birthplace) {
		set("birthplace", birthplace);
	}
	@ChangeFields(value = "birthplace",desc = "出生地")
	public String getBirthplace() {
		return getStr("birthplace");
	}

	public void setPresentaddr(String presentaddr) {
		set("presentaddr", presentaddr);
	}
	@ChangeFields(value = "presentaddr",desc = "现住地")
	public String getPresentaddr() {
		return getStr("presentaddr");
	}

	public void setNativeplace(String nativeplace) {
		set("nativeplace", nativeplace);
	}
	@ChangeFields(value = "nativeplace",desc = "籍贯")
	public String getNativeplace() {
		return getStr("nativeplace");
	}

	public void setTel(String tel) {
		set("tel", tel);
	}
	@ChangeFields(value = "tel",desc = "联系方式")
	public String getTel() {
		return getStr("tel");
	}

	public void setRetiretime(String retiretime) {
		set("retiretime", retiretime);
	}
	@ChangeFields(value = "retiretime",desc = "退休日期")
	public String getRetiretime() {
		return getStr("retiretime");
	}
	@NeedMapping(dictName = "solider")
	public void setIssoldier(String issoldier) {
		set("issoldier", issoldier);
	}
	@ChangeFields(value = "issoldier",desc = "退伍军人")
	public String getIssoldier() {
		return getStr("issoldier");
	}

	public void setEntrytime(String entrytime) {
		set("entrytime", entrytime);
	}
	@ChangeFields(value = "entrytime",desc = "入职时间")
	public String getEntrytime() {
		return getStr("entrytime");
	}

	public void setDepartime(String departime) {
		set("departime", departime);
	}
	@ChangeFields(value = "departime",desc = "离职时间")
	public String getDepartime() {
		return getStr("departime");
	}
	@NeedMapping(dictName = "officestatus")
	public void setInductionstatus(String inductionstatus) {
		set("inductionstatus", inductionstatus);
	}
	@ChangeFields(value = "inductionstatus",desc = "就职状况")
	public String getInductionstatus() {
		return getStr("inductionstatus");
	}

	@NeedMapping(dictName = "org")
	public void setDepartment(String department) {
		set("department", department);
	}
	@ChangeFields(value = "department",desc = "所属部门")
	public String getDepartment() {
		return getStr("department");
	}
	@NeedMapping(dictName = "unit")
	public void setUnitname(String unitname) {
		set("unitname", unitname);
	}
	@ChangeFields(value = "unitname",desc = "单元")
	public String getUnitname() {
		return getStr("unitname");
	}
	@NeedMapping(dictName = "projects")
	public void setCostitem(String costitem) {
		set("costitem", costitem);
	}
	@ChangeFields(value = "costitem",desc = "成本项目")
	public String getCostitem() {
		return getStr("costitem");
	}
	@NeedMapping(dictName = "position")
	public void setPosition(String position) {
		set("position", position);
	}
	@ChangeFields(value = "position",desc = "职位")
	public String getPosition() {
		return getStr("position");
	}

	public void setProtechgrade(String protechgrade) {
		set("protechgrade", protechgrade);
	}
	@ChangeFields(value = "protechgrade",desc = "专业技术等级")
	public String getProtechgrade() {
		return getStr("protechgrade");
	}

	public void setVocaqualifilevel(String vocaqualifilevel) {
		set("vocaqualifilevel", vocaqualifilevel);
	}
	@ChangeFields(value = "vocaqualifilevel",desc = "职业资格等级")
	public String getVocaqualifilevel() {
		return getStr("vocaqualifilevel");
	}

	public void setWorkstation(String workstation) {
		set("workstation", workstation);
	}
	@ChangeFields(value = "workstation",desc = "工作岗位")
	public String getWorkstation() {
		return getStr("workstation");
	}

	public void setProtechposts(String protechposts) {
		set("protechposts", protechposts);
	}
	@ChangeFields(value = "protechposts",desc = "专业技术职务")
	public String getProtechposts() {
		return getStr("protechposts");
	}

	public void setVocaQualifilevel1(String vocaQualifilevel1) {
		set("vocaQualifilevel1", vocaQualifilevel1);
	}
	@ChangeFields(value = "vocaQualifilevel1",desc = "职业资格等级")
	public String getVocaQualifilevel1() {
		return getStr("vocaQualifilevel1");
	}

	public void setCertificates(String certificates) {
		set("certificates", certificates);
	}
	@ChangeFields(value = "certificates",desc = "所持证书")
	public String getCertificates() {
		return getStr("certificates");
	}

	public void setContractstartdate(String contractstartdate) {
		set("contractstartdate", contractstartdate);
	}
	@ChangeFields(value = "contractstartdate",desc = "合同开始日期")
	public String getContractstartdate() {
		return getStr("contractstartdate");
	}

	public void setContractenddate(String contractenddate) {
		set("contractenddate", contractenddate);
	}
	@ChangeFields(value = "contractenddate",desc = "合同结束日期")
	public String getContractenddate() {
		return getStr("contractenddate");
	}

	public void setSeniority(String seniority) {
		set("seniority", seniority);
	}
	/*@ChangeFields(value = "seniority",desc = "工龄(年)")*/
	public String getSeniority() {
		return getStr("seniority");
	}

	public void setContractclauses(Integer contractclauses) {
		set("contractclauses", contractclauses);
	}
	@ChangeFields(value = "contractclauses",desc = "合同期数")
	public Integer getContractclauses() {
		return getInt("contractclauses");
	}

	public void setContractnature(String contractnature) {
		set("contractnature", contractnature);
	}
	@ChangeFields(value = "contractnature",desc = "合同性质")
	public String getContractnature() {
		return getStr("contractnature");
	}

	public void setWorktime(String worktime) {
		set("worktime", worktime);
	}
	@ChangeFields(value = "worktime",desc = "参加工作时间")
	public String getWorktime() {
		return getStr("worktime");
	}

	public void setEmcontact(String emcontact) {
		set("emcontact", emcontact);
	}
	@ChangeFields(value = "emcontact",desc = "紧急联系人")
	public String getEmcontact() {
		return getStr("emcontact");
	}

	public void setRecruitsource(String recruitsource) {
		set("recruitsource", recruitsource);
	}
	@ChangeFields(value = "recruitsource",desc = "招聘来源")
	public String getRecruitsource() {
		return getStr("recruitsource");
	}
	@NeedMapping(dictName = "hardstuff")
	public void setHardstaff(String hardstaff) {
		set("hardstaff", hardstaff);
	}
	@ChangeFields(value = "hardstaff",desc = "困难员工")
	public String getHardstaff() {
		return getStr("hardstaff");
	}

	public void setSalary(Integer salary) {
		set("salary", salary);
	}
	@ChangeFields(value = "salary",desc = "薪资")
	public Integer getSalary() {
		return getInt("salary");
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
	@NeedMapping(dictName = "edu")
	public void setEdubg1(String edubg1) {
		set("edubg1", edubg1);
	}
	@ChangeFields(value = "edubg1",desc = "学历(非全日制)")
	public String getEdubg1() {
		return getStr("edubg1");
	}

	public void setSchool1(String school1) {
		set("school1", school1);
	}
	@ChangeFields(value = "school1",desc = "学校(非全日制)")
	public String getSchool1() {
		return getStr("school1");
	}

	public void setMajor1(String major1) {
		set("major1", major1);
	}
	@ChangeFields(value = "major1",desc = "专业(非全日制)")
	public String getMajor1() {
		return getStr("major1");
	}
	@NeedMapping(dictName = "edu")
	public void setEdubg2(String edubg2) {
		set("edubg2", edubg2);
	}
	@ChangeFields(value = "edubg2",desc = "学历(全日制)")
	public String getEdubg2() {
		return getStr("edubg2");
	}

	public void setSchool2(String school2) {
		set("school2", school2);
	}
	@ChangeFields(value = "school2",desc = "学校(全日制)")
	public String getSchool2() {
		return getStr("school2");
	}

	public void setMajor2(String major2) {
		set("major2", major2);
	}
	@ChangeFields(value = "major2",desc = "专业(全日制)")
	public String getMajor2() {
		return getStr("major2");
	}

	public void setSaladjrecord(String saladjrecord) {
		set("saladjrecord", saladjrecord);
	}
	//@ChangeFields(value = "saladjrecord",desc = "调薪记录")
	public String getSaladjrecord() {
		return getStr("saladjrecord");
	}

	public void setChrecord(String chrecord) {
		set("chrecord", chrecord);
	}
	//@ChangeFields(value = "chrecord",desc = "异动记录")
	public String getChrecord() {
		return getStr("chrecord");
	}

	public void setBirthday(java.lang.String birthday) {
		set("birthday", birthday);
	}
	/*@ChangeFields(value = "birthday",desc = "出生日期")*/
	public java.lang.String getBirthday() {
		return getStr("birthday");
	}

	public void setPositivedate(java.lang.String positivedate) {
		set("positivedate", positivedate);
	}
	@ChangeFields(value = "positivedate",desc = "转正日期")
	public java.lang.String getPositivedate() {
		return getStr("positivedate");
	}

	public void setBanaccount(java.lang.String banaccount) {
		set("banaccount", banaccount);
	}
	@ChangeFields(value = "banaccount",desc = "银行账号")
	public java.lang.String getBanaccount() {
		return getStr("banaccount");
	}

	public void setFundaccount(java.lang.String fundaccount) {
		set("fundaccount", fundaccount);
	}
	@ChangeFields(value = "fundaccount",desc = "公积金账号")
	public java.lang.String getFundaccount() {
		return getStr("fundaccount");
	}
	public void setRetirestatus(java.lang.String retirestatus) {
		set("retirestatus", retirestatus);
	}
	@ChangeFields(value = "retirestatus",desc = "退休状态")
	public java.lang.String getRetirestatus() {
		return getStr("retirestatus");
	}

	public void setLeaveReason(java.lang.String leaveReason) {
		set("leave_reason", leaveReason);
	}

	public java.lang.String getLeaveReason() {
		return getStr("leave_reason");
	}

}
