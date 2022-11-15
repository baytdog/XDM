package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAmsAsset<M extends BaseAmsAsset<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public void setAssetName(java.lang.String asset_name) {
		set("asset_name", asset_name);
	}
	
	public java.lang.String getAssetName() {
		return getStr("asset_name");
	}

	public void setBelongOrgId(java.lang.String belongOrgId) {
		set("belong_org_id", belongOrgId);
	}
	
	public java.lang.String getBelongOrgId() {
		return getStr("belong_org_id");
	}

	public void setBelongOrgName(java.lang.String belongOrgName) {
		set("belong_org_name", belongOrgName);
	}
	
	public java.lang.String getBelongOrgName() {
		return getStr("belong_org_name");
	}

	public void setState(java.lang.String state) {
		set("state", state);
	}
	
	public java.lang.String getState() {
		return getStr("state");
	}

	public void setModelNum(java.lang.String modelNum) {
		set("model_num", modelNum);
	}
	
	public java.lang.String getModelNum() {
		return getStr("model_num");
	}

	public void setCount(java.lang.String count) {
		set("count", count);
	}
	
	public java.lang.String getCount() {
		return getStr("count");
	}

	public void setCreateUserId(java.lang.String createUserId) {
		set("create_user_id", createUserId);
	}
	
	public java.lang.String getCreateUserId() {
		return getStr("create_user_id");
	}

	public void setCreateUserName(java.lang.String createUserName) {
		set("create_user_name", createUserName);
	}
	
	public java.lang.String getCreateUserName() {
		return getStr("create_user_name");
	}

	public void setCreateTime(java.lang.String createTime) {
		set("create_time", createTime);
	}
	
	public java.lang.String getCreateTime() {
		return getStr("create_time");
	}

	public void setSumPrice(java.lang.String sumPrice) {
		set("sum_price", sumPrice);
	}
	
	public java.lang.String getSumPrice() {
		return getStr("sum_price");
	}

	public void setUnitPrice(java.lang.String unitPrice) {
		set("unit_price", unitPrice);
	}
	
	public java.lang.String getUnitPrice() {
		return getStr("unit_price");
	}
	
}
