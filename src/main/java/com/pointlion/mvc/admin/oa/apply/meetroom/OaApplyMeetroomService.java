package com.pointlion.mvc.admin.oa.apply.meetroom;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaApplyMeetroom;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateTimeIfOverlayUtil;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaApplyMeetroomService{
	public static final OaApplyMeetroomService me = new OaApplyMeetroomService();
	private static final String TABLE_NAME = OaApplyMeetroom.tableName;
	
	/***
	 * 根据主键查询
	 */
	public OaApplyMeetroom getById(String id){
		return OaApplyMeetroom.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_ where 1=1 ";
		String userId = ShiroKit.getUserId();
		//数据权限
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select o.*,p.PROC_DEF_ID_ defid ", sql);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyMeetroom o = me.getById(id);
    		o.delete();
    	}
	}
	/***
	 * 根据车id是否在使用
	 */
	public Boolean isInUse(String id,String startTime,String endTime){
		String now = DateUtil.getCurrentTime();
		String sql = "select * from "+TABLE_NAME+" o where  o.meet_room_id='"+id+"'  ";//排除自己，当天之后的申请
		if(StrKit.notBlank(startTime)&&StrKit.notBlank(endTime)){
			sql = sql + " and o.end_time>'"+now+"' ";
		}
//		if(StrKit.notBlank(applyId)){
//			sql =sql + " and o.id!='"+applyId+"' ";
//		}
		List<OaApplyMeetroom> list = OaApplyMeetroom.dao.find(sql);//查询借用历史
		List<String> time = new ArrayList<String>();
		if(StrKit.notBlank(startTime)&&StrKit.notBlank(endTime)){
			time.add(startTime+"@@@@@"+endTime);
		}
		for(OaApplyMeetroom aroom : list ){
			time.add(aroom.getStartTime()+"@@@@@"+aroom.getEndTime());
		}
		if(DateTimeIfOverlayUtil.checkOverlap(time)){//时间是否重叠
			return true;//有借用的
		}else{
			return false;//没有借用的
		}
	}
}