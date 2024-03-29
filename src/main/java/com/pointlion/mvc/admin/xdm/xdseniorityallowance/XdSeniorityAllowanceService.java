package com.pointlion.mvc.admin.xdm.xdseniorityallowance;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdSeniorityAllowance;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XdSeniorityAllowanceService{
	public static final XdSeniorityAllowanceService me = new XdSeniorityAllowanceService();
	public static final String TABLE_NAME = XdSeniorityAllowance.tableName;
	
	/***
	 * query by id
	 */
	public XdSeniorityAllowance getById(String id){
		return XdSeniorityAllowance.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String year,String empName){
//		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where 1=1";
		String userOrgId = ShiroKit.getUserOrgId();
		if(!"1".equals(userOrgId)){
			sql = sql + " and o.emp_name  in (select name from  xd_employee where department='"+userOrgId+"')";
		}

		if(StrKit.notBlank(year)){
			sql = sql + " and o.year='"+ year+"'";
		}
		if(StrKit.notBlank(empName)){
			sql = sql + " and o.emp_name like '%"+empName+"%'";
		}
		sql = sql + " order by o.create_date desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/**
	 * @Method deleteByIds
	 * @param ids:	 要删除信息的ID
	 * @Date 2023/3/17 16:19
	 * @Description
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idArr[] = ids.split(",");
    	for(String id : idArr){
    		XdSeniorityAllowance o = me.getById(id);
    		o.delete();
    	}
	}


	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {

		final Map<String,Object> result = new HashMap<String,Object>();
		final List<String> message = new ArrayList<String>();

		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>1){
						int year = Integer.valueOf(list.get(0).get(10).replaceAll("\\s*", "").replaceAll("[^(0-9)]", ""));
						for(int i = 1;i<list.size();i++){

							List<String> saList = list.get(i);
							if(saList.get(0).equals("")){
								continue;
							}
							XdSeniorityAllowance sa=new XdSeniorityAllowance();
							sa.setEmpName(saList.get(0));
							Db.delete("delete from xd_seniority_allowance where emp_name='"+saList.get(0)+"' and  year='"+year+"'");

							sa.setIdnum(saList.get(1));
							sa.setHireDate(saList.get(2));
							sa.setExpirationDate(saList.get(3));
							sa.setMonth(Integer.valueOf(saList.get(4)));
							sa.setWorkYear(Double.valueOf(saList.get(5)));
							sa.setNewWorkYear(Double.valueOf(saList.get(6)));
							sa.setOldWokrYear(Double.valueOf(saList.get(7)));
							sa.setOldSenallow(Double.valueOf(saList.get(8)));
							sa.setAddDepart(Double.valueOf(saList.get(9)));
							sa.setSeniorityAllowance(Double.valueOf(saList.get(10)));
							sa.setEmpNature(saList.get(11));
							sa.setYEAR(year);
							sa.setCreateDate(DateUtil.getCurrentTime());
							sa.setCreateUser(ShiroKit.getUserId());
							sa.save();
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