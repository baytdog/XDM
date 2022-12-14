package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseGrapDbSource<M extends BaseGrapDbSource<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}
	
	public java.lang.String getName() {
		return getStr("name");
	}

	public void setDbType(java.lang.String dbType) {
		set("db_type", dbType);
	}
	
	public java.lang.String getDbType() {
		return getStr("db_type");
	}

	public void setDbIp(java.lang.String dbIp) {
		set("db_ip", dbIp);
	}
	
	public java.lang.String getDbIp() {
		return getStr("db_ip");
	}

	public void setDbName(java.lang.String dbName) {
		set("db_name", dbName);
	}
	
	public java.lang.String getDbName() {
		return getStr("db_name");
	}

	public void setDbUserName(java.lang.String dbUserName) {
		set("db_user_name", dbUserName);
	}
	
	public java.lang.String getDbUserName() {
		return getStr("db_user_name");
	}

	public void setDbPassword(java.lang.String dbPassword) {
		set("db_password", dbPassword);
	}
	
	public java.lang.String getDbPassword() {
		return getStr("db_password");
	}

	public void setCreateTime(java.lang.String createTime) {
		set("create_time", createTime);
	}
	
	public java.lang.String getCreateTime() {
		return getStr("create_time");
	}

	public void setPort(java.lang.String port) {
		set("port", port);
	}
	
	public java.lang.String getPort() {
		return getStr("port");
	}
	
	public void setDbConfigName(java.lang.String db_config_name) {
		set("db_config_name", db_config_name);
	}
	
	public java.lang.String getDbConfigName() {
		return getStr("db_config_name");
	}
}
