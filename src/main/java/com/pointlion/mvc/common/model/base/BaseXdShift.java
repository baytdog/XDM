package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXdShift<M extends BaseXdShift<M>> extends Model<M> implements IBean {

	public void setId(String id) {
		set("id", id);
	}
	
	public String getId() {
		return getStr("id");
	}

	public void setShiftname(String shiftname) {
		set("shiftname", shiftname);
	}
	
	public String getShiftname() {
		return getStr("shiftname");
	}

	public void setBusitime(String busitime) {
		set("busitime", busitime);
	}
	
	public String getBusitime() {
		return getStr("busitime");
	}

	public void setUnbusitime(String unbusitime) {
		set("unbusitime", unbusitime);
	}
	
	public String getUnbusitime() {
		return getStr("unbusitime");
	}

	public void setTimebucket(String timebucket) {
		set("timebucket", timebucket);
	}
	
	public String getTimebucket() {
		return getStr("timebucket");
	}

	public void setHours(String hours) {
		set("hours", hours);
	}
	
	public String getHours() {
		return getStr("hours");
	}

	public void setMiddleshift(String middleshift) {
		set("middleshift", middleshift);
	}
	
	public String getMiddleshift() {
		return getStr("middleshift");
	}

	public void setNigthshift(String nigthshift) {
		set("nigthshift", nigthshift);
	}
	
	public String getNigthshift() {
		return getStr("nigthshift");
	}

	public void setShiftcost(String shiftcost) {
		set("shiftcost", shiftcost);
	}
	
	public String getShiftcost() {
		return getStr("shiftcost");
	}

	public void setDutyamount(String dutyamount) {
		set("dutyamount", dutyamount);
	}
	
	public String getDutyamount() {
		return getStr("dutyamount");
	}

	public void setDutydesc(String dutydesc) {
		set("dutydesc", dutydesc);
	}
	
	public String getDutydesc() {
		return getStr("dutydesc");
	}

	public void setSpanDay(String spanDay) {
		set("span_day", spanDay);
	}
	
	public String getSpanDay() {
		return getStr("span_day");
	}

	public void setCurdayHours(String curdayHours) {
		set("curday_hours", curdayHours);
	}
	
	public String getCurdayHours() {
		return getStr("curday_hours");
	}

	public void setSpanHours(String spanHours) {
		set("span_hours", spanHours);
	}
	
	public String getSpanHours() {
		return getStr("span_hours");
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

	public void setBakcup2(String bakcup2) {
		set("bakcup2", bakcup2);
	}
	
	public String getBakcup2() {
		return getStr("bakcup2");
	}

	public void setBackup3(String backup3) {
		set("backup3", backup3);
	}
	
	public String getBackup3() {
		return getStr("backup3");
	}

}
