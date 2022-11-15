package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaTabFile;
import com.pointlion.mvc.common.utils.DateUtil;
@SuppressWarnings("serial")
public class OaTabFile extends BaseOaTabFile<OaTabFile> {
	public static final OaTabFile dao = new OaTabFile();
	public static final String tableName = "oa_tab_file";
	
	/***
	 * query by id
	 */
	public OaTabFile getById(String id){
		return OaTabFile.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaTabFile o = OaTabFile.dao.getById(id);
    		o.delete();
    	}
	}
	
	
	public Page<OaTabFile> getPage(int pnum,int psize,String type,String rdnum,String rtitle,String rFileNo,String rstate,String startTime,String endTime){
		//String sql = " from "+TABLE_NAME+" o   WHERE  o.backup2='1'   ";
		String sql = " from "+tableName+" o   WHERE 1=1  ";
		
		
		
		/*if(Constants.ADMIN_USER.indexOf(ShiroKit.getUsername())!=-1) {
			
		}else {
			//数据权限
			sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
			
		}*/
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(rdnum)){
			sql = sql + " and o.documentnumber like '%"+rdnum+"%'";
		}
		if(StrKit.notBlank(rtitle)) {
			
			sql = sql + " and o.title like '%"+rtitle+"%'";
		}
		if(StrKit.notBlank(rFileNo)) {
			
			sql = sql + " and o.file_no like '%"+rFileNo+"%'";
		}
		if(StrKit.notBlank(rstate)) {
			
			sql = sql + " and o.status ='"+rstate+"'";
		}
	 
		
		
		sql = sql + " ORDER BY timeflag DESC";
		return OaTabFile.dao.paginate(pnum, psize, "select o.*", sql);
	}
}