package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOaBumphOrg<M extends BaseOaBumphOrg<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}

	public java.lang.String getId() {
		return get("id");
	}

	public void setOrgid(java.lang.String orgid) {
		set("orgid", orgid);
	}

	public java.lang.String getOrgid() {
		return get("orgid");
	}

	public void setOrgname(java.lang.String orgname) {
		set("orgname", orgname);
	}

	public java.lang.String getOrgname() {
		return get("orgname");
	}

	public void setBumphId(java.lang.String bumphId) {
		set("bumph_id", bumphId);
	}

	public java.lang.String getBumphId() {
		return get("bumph_id");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}

	public java.lang.String getType() {
		return get("type");
	}

}
