package com.pointlion.mvc.admin.xdm.xdemployee;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.XdOperUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.utils.DateUtil;

import java.util.List;

public class XdEmployeeService{
	public static final XdEmployeeService me = new XdEmployeeService();
	public static final String TABLE_NAME = XdEmployee.tableName;
	
	/***
	 * query by id
	 */
	public XdEmployee getById(String id){
		return XdEmployee.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o where cuser='"+ShiroKit.getUserId()+"'";
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idArr[] = ids.split(",");
		String userOrgId = ShiroKit.getUserOrgId();

		for(String id : idArr){
			XdOperUtil.queryLastVersion(id);
			XdEmployee o = me.getById(id);
			if("1".equals(userOrgId)){
				o.delete();
			}else{
				XdOperUtil.insertEmpoloyeeSteps(o,"","1","","","D");
			}

			XdOperUtil.logSummary(id,o,XdOperEnum.D.name(),XdOperEnum.WAITAPPRO.name(),0);

			List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from xd_work_exper where eid='" + id + "'");
			for (XdWorkExper workExper : workExperList) {
				if("1".equals(userOrgId)){
					workExper.delete();
				}
				XdOperUtil.logSummary(id,workExper,XdOperEnum.D.name(),XdOperEnum.WAITAPPRO.name(),0);
			}
			List<XdEdutrain> edutrainList = XdEdutrain.dao.find("select * from xd_edutrain where eid='" + id + "'");
			for (XdEdutrain xdEdutrain : edutrainList) {
				if("1".equals(userOrgId)){
					xdEdutrain.delete();
				}
				XdOperUtil.logSummary(id,xdEdutrain,XdOperEnum.D.name(),XdOperEnum.WAITAPPRO.name(),0);
			}
		}
	}
	
}