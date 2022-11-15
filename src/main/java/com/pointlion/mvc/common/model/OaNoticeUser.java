package com.pointlion.mvc.common.model;

import java.util.List;

import com.pointlion.mvc.common.model.base.BaseOaNoticeUser;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class OaNoticeUser extends BaseOaNoticeUser<OaNoticeUser> {
	public static final OaNoticeUser dao = new OaNoticeUser();
	
	/***
	 * 查询所有被通知人
	 * @param id
	 * @return
	 */
	public List<OaNoticeUser> getNoticeUserListByNoticeId(String id){
		return OaNoticeUser.dao.find("select * from oa_notice_user where notice_id = '"+id+"'");
	}
	
	public OaNoticeUser getNoticeUserByNoticeIdAndUserid(String id,String userid){
		return OaNoticeUser.dao.findFirst("select * from oa_notice_user u where u.notice_id = '"+id+"' and u.user_id='"+userid+"'");
	}
}
