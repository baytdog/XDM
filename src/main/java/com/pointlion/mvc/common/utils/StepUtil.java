/**
 * 
 */
package com.pointlion.mvc.common.utils;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.jfinal.plugin.activerecord.Db;
import com.pointlion.mvc.common.model.OaStepHistory;
import com.pointlion.mvc.common.model.OaSteps;
import com.pointlion.mvc.common.model.OaStepsRz;
import com.pointlion.plugin.shiro.ShiroKit;

/** 
 * @ClassName: StepUtil 
 * @Description: TODO
 * @date: 2020年11月9日 下午2:16:50  
 */
public class StepUtil {

	/** 
	* @Title: insertStepHistory 
	* @Description: 插入历史 
	* @param o void
	* @date 2020年11月6日上午11:16:39
	*/ 
	public static void insertStepHistory(String id,String title,String type) {
		
		Db.delete("delete  from oa_step_history where actorid='"+ShiroKit.getUserId()+"' and oid='"+id+"'");
		
		
		OaStepHistory cos=new OaStepHistory();
		cos.setId(UuidUtil.getUUID());
		cos.setOid(id);
		cos.setTitle(title);
		cos.setActorid(ShiroKit.getUserId());
		cos.setActorname(ShiroKit.getUserName());
		cos.setActtime(DateUtil.getCurrentTime());
		cos.setType(type);
		cos.setCtime(DateUtil.getCurrentTime());
		cos.setCuser(ShiroKit.getUserName());
		cos.save();
	}
	
	
	
	
	/**
	 * 删除记录 删除关联步骤表
	 */
	
	public static void   deleteSteps(String oid) {
		
		
		Db.delete("delete  from  oa_steps  where oid='"+oid+"'");
		
		Db.delete("delete  from  oa_step_history  where oid='"+oid+"'");
		
		Db.delete("delete  from  oa_nodes  where oid='"+oid+"'");
	}
	
	
}
