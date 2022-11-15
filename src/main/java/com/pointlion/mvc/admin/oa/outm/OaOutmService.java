package com.pointlion.mvc.admin.oa.outm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.OaBumph;
import com.pointlion.mvc.common.model.OaBumphOrg;
import com.pointlion.mvc.common.model.OaOutm;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.ListUtil;
import com.pointlion.mvc.common.utils.UuidUtil;

public class OaOutmService{
	public static final OaOutmService me = new OaOutmService();
	public static final String TABLE_NAME = OaOutm.TABLE_NAME;
	
	public static final WorkFlowService workFlowService = new WorkFlowService();
	
	/***
	 * query by id
	 */
	public OaOutm getById(String id){
		return OaOutm.dao.findById(id);
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
    		OaOutm o = me.getById(id);
    		o.delete();
    	}
	}
	
	
	/***
	 * 保存主送和抄送
	 * @param bumph
	 * @param fidstr
	 * @param secondStr
	 */
	@Before(Tx.class)
	public String save(OaOutm outm){
		OaBumphOrg.dao.deleteOrgByBumphId(outm.getId());//先清空掉所有单位
		if(StrKit.notBlank(outm.getId())){
			outm.update();//更新公文
    	/*	saveOrg("1",bumph.getId(),fidstr);//更新主送单位
    		saveOrg("0",bumph.getId(),secondStr);//更新抄送单位
*/    		return outm.getStr("id");
    	}else{
    		String id = UuidUtil.getUUID();
    		outm.setId(id);
    		outm.setDocNumSource("行政发");
    		outm.setDocNum(outm.getDocNumSource()+"["+outm.getDocNumYear()+"]"+outm.getDocNumN()+"号");
    		outm.setCreateTime(DateUtil.getCurrentTime());
    		outm.setCreateOrgId(ShiroKit.getUserOrgId());
    		outm.setCreateUserId(ShiroKit.getUserId());
    		outm.setCreateOrgName(ShiroKit.getUserOrgName());
    		outm.setCreateUserName(ShiroKit.getUsername());
    		outm.setCreateTime(DateUtil.getCurrentTime());
    		outm.save();//保存公文
    	/*	saveOrg("1",bumph.getId(),fidstr);//更新主送单位
    		saveOrg("0",bumph.getId(),secondStr);//更新抄送单位
*/    		return id;
    	}
	}
	
	
	
	/***
	 * 启动流程
	 */
	@Before(Tx.class)
	public void startProcess(String id){
		WorkFlowService service = new WorkFlowService();
		Map<String, Object> var = new HashMap<String,Object>();
		//查询主送和抄送所有的用户
		/*List<OaBumphOrg> list = OaBumphOrg.dao.getList(id, null);//主送单位和抄送单位
		List<String> userlist = new ArrayList<String>();//需要阅读公文的人员列表
		for(OaBumphOrg org:list){
			List<SysUser> ulist = SysUser.dao.getUserListByOrgId(org.getOrgid());//单位下的用户
			for(SysUser u:ulist){
				userlist.add(u.getUsername());
			}
		}
		var.put("userlist", ListUtil.removeDuplicate(userlist));//需要阅读公文的所有人员
*/		
		
		OaOutm outm = OaOutm.dao.findById(id);
		
		var.put("ishq", outm.getIshq());
		
		String procInsId = service.startProcess(id, outm,outm.getTitle(),var);
		outm.setProcInsId(procInsId);
		outm.setIfSubmit(Constants.IF_SUBMIT_YES);
		outm.update();
	}

	@Before(Tx.class)
	public void callBack(String id){
		OaOutm outm = OaOutm.dao.findById(id);
		String procid = outm.getProcInsId();//流程实例id
/*		outm.setFirstLeaderAudit("");//清空审批信息
		outm.setSecondLeaderAudit("");*/
		outm.setProcInsId("");
		outm.setIfSubmit(Constants.IF_SUBMIT_NO);
		outm.update();
    	workFlowService.callBack(procid);
	}
	
	
	
	@Before(Tx.class)
	public void delete(String id){
		OaOutm.dao.findById(id).delete();
		/*OaBumphOrg.dao.deleteOrgByBumphId(id);*/
	}
}