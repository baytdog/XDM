package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSysUser<M extends BaseSysUser<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}

	public java.lang.String getId() {
		return get("id");
	}

	public void setSort(java.lang.Long sort) {
		set("sort", sort);
	}

	public java.lang.Long getSort() {
		return get("sort");
	}

	public void setUsername(java.lang.String username) {
		set("username", username);
	}

	public java.lang.String getUsername() {
		return get("username");
	}

	public void setPassword(java.lang.String password) {
		set("password", password);
	}

	public java.lang.String getPassword() {
		return get("password");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setSex(java.lang.String sex) {
		set("sex", sex);
	}

	public java.lang.String getSex() {
		return get("sex");
	}
	
	public void setStatus(java.lang.String status) {
		set("status", status);
	}

	public java.lang.String getStatus() {
		return get("status");
	}

	public void setOrgid(java.lang.String orgid) {
		set("orgid", orgid);
	}

	public java.lang.String getOrgid() {
		return get("orgid");
	}

	public void setStationid(java.lang.String stationid) {
		set("stationid", stationid);
	}

	public java.lang.String getStationid() {
		return get("stationid");
	}

	public void setEmail(java.lang.String email) {
		set("email", email);
	}

	public java.lang.String getEmail() {
		return get("email");
	}

	public void setIdcard(java.lang.String idcard) {
		set("idcard", idcard);
	}

	public java.lang.String getIdcard() {
		return get("idcard");
	}

	public void setIsAdmin(java.lang.String is_admin) {
		set("is_admin", is_admin);
	}

	public java.lang.String getIsAdmin() {
		return get("is_admin");
	}
	
	public void setMobile(java.lang.String mobile) {
		set("mobile", mobile);
	}

	public java.lang.String getMobile() {
		return get("mobile");
	}
	
	public void setImg(java.lang.String img) {
		set("img", img);
	}

	public java.lang.String getImg() {
		return get("img");
	}
	
	public void setType(java.lang.String type) {
		set("type", type);
	}

	public java.lang.String getType() {
		return get("type");
	}
	
	public void setEntryDate(java.lang.String entry_date) {
		set("entry_date", entry_date);
	}

	public java.lang.String getEntryDate() {
		return get("entry_date");
	}
	
	public void setYearHoliday(java.lang.String year_holiday) {
		set("year_holiday", year_holiday);
	}

	public java.lang.String getYearHoliday() {
		return get("year_holiday");
	}
	
	public void setInCompanyDate(java.lang.String in_company_date) {
		set("in_company_date", in_company_date);
	}

	public java.lang.String getInCompanyDate() {
		return get("in_company_date");
	}

	public void setDimissionDate(java.lang.String dimission_date) {
		set("dimission_date", dimission_date);
	}

	public java.lang.String getDimissionDate() {
		return get("dimission_date");
	}

	public void setExperience(java.lang.String experience) {
		set("experience", experience);
	}

	public java.lang.String getExperience() {
		return get("experience");
	}

	public void setEduExperience(java.lang.String edu_experience) {
		set("edu_experience", edu_experience);
	}

	public java.lang.String getEduExperience() {
		return get("edu_experience");
	}

	public void setWorkNum(java.lang.String work_num) {
		set("work_num", work_num);
	}

	public java.lang.String getWorkNum() {
		return get("work_num");
	}
	
	public void setBirthDate(java.lang.String birth_date) {
		set("birth_date", birth_date);
	}

	public java.lang.String getBirthDate() {
		return get("birth_date");
	}
	
	public void setPosition(java.lang.String position) {
		set("position", position);
	}

	public java.lang.String getPosition() {
		return get("position");
	}
	
}

