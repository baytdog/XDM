package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCmsContent<M extends BaseCmsContent<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}

	public java.lang.String getId() {
		return get("id");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}

	public java.lang.String getTitle() {
		return get("title");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}

	public java.lang.String getType() {
		return get("type");
	}
	
	public void setWriterId(java.lang.String writerId) {
		set("writer_id", writerId);
	}

	public java.lang.String getWriterId() {
		return get("writer_id");
	}

	public void setWriterName(java.lang.String writerName) {
		set("writer_name", writerName);
	}

	public java.lang.String getWriterName() {
		return get("writer_name");
	}

	public void setWriterOrgid(java.lang.String writerOrgid) {
		set("writer_orgid", writerOrgid);
	}

	public java.lang.String getWriterOrgid() {
		return get("writer_orgid");
	}

	public void setWriterOrgname(java.lang.String writerOrgname) {
		set("writer_orgname", writerOrgname);
	}

	public java.lang.String getWriterOrgname() {
		return get("writer_orgname");
	}

	public void setCreateTime(java.lang.String createTime) {
		set("create_time", createTime);
	}

	public java.lang.String getCreateTime() {
		return get("create_time");
	}

	public void setPublishTime(java.lang.String publishTime) {
		set("publish_time", publishTime);
	}

	public java.lang.String getPublishTime() {
		return get("publish_time");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return get("content");
	}

	public void setTextContent(java.lang.String text_content) {
		set("text_content", text_content);
	}

	public java.lang.String getTextContent() {
		return get("text_content");
	}

	public void setIfPublish(java.lang.String ifPublish) {
		set("if_publish", ifPublish);
	}

	public java.lang.String getIfPublish() {
		return get("if_publish");
	}
	
	public void setGroup(java.lang.String group) {
		set("group", group);
	}

	public java.lang.String getGroup() {
		return get("group");
	}
	public void setImg(java.lang.String img) {
		set("img", img);
	}

	public java.lang.String getImg() {
		return get("img");
	}
	
	public void setYear(java.lang.String year) {
		set("year", year);
	}

	public java.lang.String getYear() {
		return get("year");
	}
}
