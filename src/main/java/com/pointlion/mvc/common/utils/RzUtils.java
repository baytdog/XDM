/**
 * 
 */
package com.pointlion.mvc.common.utils;

import com.pointlion.mvc.common.model.OaNotices;
import com.pointlion.mvc.common.model.OaRz;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.plugin.shiro.ShiroKit;

/** 
 * @ClassName: RzUtils 
 * @Description: TODO
 * @date: 2021年2月24日 下午2:09:20  
 */
public class RzUtils {
	
	/**
	 * 
	* @Title: insertOaRz 
	* @Description: TODO 
	* @param id
	* @param type 1通知查看、2附件下载、3附件查看、4首页查询
	* @date 2021年2月24日下午2:10:04
	 */
	public static  void  insertOaRz(String id,String type) {
		
		String  keywords="";
		switch (type) {
		case "1":
			OaNotices notices = OaNotices.dao.findById(id);
			keywords=notices.getNoticename();
			break;
		case "2":
			SysAttachment attachment = SysAttachment.dao.findById(id);
			keywords=attachment.getFileName();
			break;
		case "3":
			
			break;
			
		case "4":
			keywords=id;
			break;
		default:
			break;
		}
		
		
		OaRz rz=new OaRz();
		rz.setId(UuidUtil.getUUID());
		rz.setType(type);
		rz.setKeywords(keywords);
		rz.setOid(id);
		rz.setCtime(DateUtil.getCurrentTime());
		rz.setCuser(ShiroKit.getUserId());
		rz.save();
		
		
		
	}
	
	
	
	

}
