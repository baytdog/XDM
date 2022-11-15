package com.pointlion.mvc.admin.oa.hotline;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaHotline;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StepUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;

public class OaHotlineService{
	public static final OaHotlineService me = new OaHotlineService();
	public static final String TABLE_NAME = OaHotline.tableName;
 
	
	/***
	 * query by id
	 */
	public OaHotline getById(String id){
		return OaHotline.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where 1=1"; 
		
	if(Constants.ADMIN_USER.indexOf(ShiroKit.getUsername())!=-1) {
			
		}else {
			sql=sql+" and o.cuserid='"+userId+"'";
		}
		
		
		
		
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	public Page<Record> getPageBySearch(int pnum,int psize,String startTime,String endTime,String hnum,String hfromer,String hfromnum,String hstate){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where 1=1"; 
		
		if(Constants.ADMIN_USER.indexOf(ShiroKit.getUsername())!=-1) {
			
		}else {
			sql=sql+" and o.cuserid='"+userId+"'";
		}
		
		
		
		
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.fromtime>='"+ startTime+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.fromtime<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(hnum)){
			sql = sql + " and o.num like '%"+hnum+"%'";
		}
		if(StrKit.notBlank(hfromer)){
			sql = sql + " and o.fromer like '%"+hfromer+"%'";
		}
		if(StrKit.notBlank(hfromnum)){
			sql = sql + " and o.fromnum like '%"+hfromnum+"%'";
		}
		if(StrKit.notBlank(hstate)){
			sql = sql + " and o.status = '"+hstate+"'";
		}
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser,String num ,String selectType){
		//String userId = ShiroKit.getUserId();
		String sql  = " from  v_hotline_letter o   where  1=1";
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.ctime>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.ctime<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.fromer like '%"+applyUser+"%'";
		}
		if(StrKit.notBlank(num)) {
			sql = sql + " and o.num like '%"+num+"%'";
		}
		if(StrKit.notBlank(selectType)) {
			sql = sql + " and o.type ='"+selectType+"'";
		}
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser,String type){
		String sql  = " from "+TABLE_NAME+" o   where  o.cuserid='"+ShiroKit.getUserId()+"'";
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	public Page<Record> getPage(int pnum,int psize){
		//String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  left join  oa_hotline_user hu on o.id=hu.hotlinid   where 1=1 and  hu.userid='"+ShiroKit.getUserId()+"' and hu.ifshow='1'";
		/*if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}*/
		/*if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}*/
		sql = sql + " order by o.ctime desc,hu.ifcomplete ";
		return Db.paginate(pnum, psize, " select o.*, hu.ifcomplete", sql);
	}
	public Page<Record> getPHotLineListPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o  left join  oa_steps s on o.id=s.oid   where  s.userid='"+ShiroKit.getUserId()+"' and s.ifcomplete='0'";
 
		sql = sql + " order by o.ctime desc,s.ifcomplete ";
		return Db.paginate(pnum, psize, " select o.*,s.id as sid,s.step as sstep, s.ifcomplete", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaHotline o = me.getById(id);
    		o.delete();
    		StepUtil.deleteSteps(id);
    		
    	}
	}
	
	
	
	
	
	
	
	/***
	 * 导入
	 */
	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
	  Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>1){
						SimpleUser user = ShiroKit.getLoginUser();
						/*String orgid = ShiroKit.getUserOrgId();
						String orgName = ShiroKit.getUserOrgName();*/
						String time = DateUtil.getCurrentTime();
						for(int i = 1;i<list.size();i++){//从第二行开始取
							List<String> l = list.get(i);
								OaHotline hotLine=new OaHotline();
								hotLine.setId(UuidUtil.getUUID());
								hotLine.setCtime(time);
								hotLine.setCuserid(user.getId());
								hotLine.setCusername(user.getName());
								hotLine.setBackup1("1");//数据来源 0 系统录入1 导入
								hotLine.setNum(getStr(l,0));
								hotLine.setFromtime(getStr(l,1));
								hotLine.setFromer(getStr(l,2));
								hotLine.setContact(getStr(l,3));
								hotLine.setFromchannel(getStr(l,4));
								hotLine.setHotlinetype(getStr(l,5));
								hotLine.setBackup2(getStr(l,6));
								hotLine.setCbrremark(getStr(l,7));
								hotLine.setStatus("6");
								hotLine.save();
								 
					 
						}
						if(result.get("success")==null){
							result.put("success",true);//正常执行完毕
						}
					}else{
						result.put("success",false);//正常执行完毕
						message.add("excel中无内容");
						result.put("message", StringUtils.join(message," "));
					}
					result.put("message",StringUtils.join(message," "));
					if((Boolean) result.get("success")){//正常执行完毕
						return true;
					}else{//回滚
						return false;
					}
				}catch(Exception e){
					return false;
				}
			}
		});
		return result;
	}

	public static String getStr(List<String> l,int i){
		String re = "";
		try{
			if(i==1) {
				re = l.get(i).substring(0, 10);
			}else {
				re = l.get(i);
			}
		}catch(Exception e){
			re = "";
		}
		return re;
	}
}