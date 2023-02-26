package com.pointlion.mvc.admin.xdm.xdanleaveplan;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdAnleaveExecute;
import com.pointlion.mvc.common.model.XdAnleavePlan;
import com.pointlion.mvc.common.model.XdSeniorityAllowance;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.util.DictMapping;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XdAnleavePlanService{
	public static final XdAnleavePlanService me = new XdAnleavePlanService();
	public static final String TABLE_NAME = XdAnleavePlan.tableName;

	/***
	 * query by id
	 */
	public XdAnleavePlan getById(String id){
		return XdAnleavePlan.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String year){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o    where 1=1";
		if(StrKit.notBlank(year)){
			sql = sql + " and o.year='"+year+"'";
		}
		sql = sql + " order by o.create_date desc";
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
    		XdAnleavePlan o = me.getById(id);
    		o.delete();
    	}
	}


	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {

		final Map<String,Object> result = new HashMap<String,Object>();
		final List<String> message = new ArrayList<String>();
		Map<String, String> orgMapping = DictMapping.orgMapping("1");
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>2){
						int year = Integer.valueOf(list.get(0).get(1).replaceAll("\\s*", "").replaceAll("[^(0-9)]", ""));
						for(int i = 2;i<list.size();i++){

							List<String> planList = list.get(i);
							/*if(planList.get(0).equals("")){
								continue;
							}*/
							XdAnleavePlan plan=new XdAnleavePlan();
							plan.setDeptName(planList.get(0));
							plan.setDeptId(orgMapping.get(planList.get(0)));
							plan.setYear(year);
							plan.setAnleaveDays(Integer.valueOf(planList.get(1)));
							Class<? super XdAnleavePlan> superclass = XdAnleavePlan.class.getSuperclass();
							Method method=null;
							for (int j = 1; j <13 ; j++) {
								method= superclass.getMethod("setMonth" + j, Double.class);
								method.invoke(plan,Double.valueOf(planList.get(j+1)));
							}
							plan.setSum(Double.valueOf(planList.get(14)));

							plan.setCreateDate(DateUtil.getCurrentTime());
							plan.setCreateUser(ShiroKit.getUserId());

							plan.save();
							XdAnleaveExecute execute=new XdAnleaveExecute();
							execute.setDeptId(plan.getDeptId());
							execute.setDeptName(plan.getDeptName());
							execute.setAnleaveDays(plan.getAnleaveDays());
							execute.setYear(plan.getYear());
							execute.setCreateUser(ShiroKit.getUserId());
							execute.setCreateDate(DateUtil.getCurrentTime());
							execute.save();
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
	
}