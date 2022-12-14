package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseGrapUrl<M extends BaseGrapUrl<M>> extends Model<M> implements IBean {

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

	public void setUrl(java.lang.String url) {
		set("url", url);
	}
	
	public java.lang.String getUrl() {
		return getStr("url");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}
	
	public java.lang.String getType() {
		return getStr("type");
	}

	public void setWebsiteId(java.lang.String websiteId) {
		set("website_id", websiteId);
	}
	
	public java.lang.String getWebsiteId() {
		return getStr("website_id");
	}

	public void setCreateTime(java.lang.String createTime) {
		set("create_time", createTime);
	}
	
	public java.lang.String getCreateTime() {
		return getStr("create_time");
	}
	
	public void setGetDataImplClass(java.lang.String getdata_impl_class) {
		set("getdata_impl_class", getdata_impl_class);
	}
	
	public java.lang.String getGetDataImplClass() {
		return getStr("getdata_impl_class");
	}
	
	public void setDbSourceId(java.lang.String dbsource_id) {
		set("dbsource_id", dbsource_id);
	}
	
	public java.lang.String getDbSourceId() {
		return getStr("dbsource_id");
	}
	
}
