package com.pointlion.mvc.admin.oa.notices;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaNotices;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaNoticesService{
	public static final OaNoticesService me = new OaNoticesService();
	public static final String TABLE_NAME = OaNotices.tableName;
	
	/***
	 * query by id
	 */
	public OaNotices getById(String id){
		return OaNotices.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaNotices o = me.getById(id);
    		o.delete();
    	}
	}

	@Before(Tx.class)
	public void save(OaNotices notices) {
		//picnew.setTextContent(StringUtil.delHTMLTag(picnew.getContent()));//设置纯文本
		if(StrKit.notBlank(notices.getId()==null?"":notices.getId().toString())){//更新
			notices.update();//更新公共
		}else{//保存
			//picnew.setId(UuidUtil.getUUID());
			//notices.setCdate(DateUtil.getCurrentTime());
			
			String id = UuidUtil.getUUID();
			notices.setId(id);
	 
			notices.save();//保存公告
		}
	 
		
	
	}
	
}