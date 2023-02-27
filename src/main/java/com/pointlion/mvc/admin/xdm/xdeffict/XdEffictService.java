package com.pointlion.mvc.admin.xdm.xdeffict;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.*;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.util.DictMapping;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XdEffictService{
	public static final XdEffictService me = new XdEffictService();
	public static final String TABLE_NAME = XdEffict.tableName;
	
	/***
	 * query by id
	 */
	public XdEffict getById(String id){
		return XdEffict.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pNum,int pSize,String empName){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		if(StrKit.notBlank(empName)){
			sql = sql + " and o.emp_name  like '%"+ empName+"%'";
		}
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pNum, pSize, " select * ", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdEffict o = me.getById(id);
    		o.delete();
    	}
	}

	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {

		final Map<String,Object> result = new HashMap<String,Object>();
		final List<String> message = new ArrayList<String>();
		Map<String, String> orgMapping = DictMapping.orgMapping("1");
		Map<String, String> duty = DictMapping.dictMapping().get("duty");
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>1){
						//int year = Integer.valueOf(list.get(0).get(1).replaceAll("\\s*", "").replaceAll("[^(0-9)]", ""));
						for(int i = 1;i<list.size();i++){

							List<String> efList = list.get(i);
							if(efList.get(1).equals("")){
								continue;
							}
							XdEffict xdEffict=new XdEffict();
							xdEffict.setEmpNum(efList.get(1));
							xdEffict.setEmpName(efList.get(2));
							XdEmployee employee = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + efList.get(2) + "'");
							xdEffict.setEid(employee==null?"":employee.getId());
							xdEffict.setHireDate(efList.get(3));
							xdEffict.setAdjustReason(efList.get(4));
							xdEffict.setOldDeptName(efList.get(5));
							String deptId = orgMapping.get(efList.get(5));
							xdEffict.setOldDeptId(deptId==null?"":deptId);
							String pd = efList.get(6);
							xdEffict.setOldPdvalue(duty.get(pd)==null?"":duty.get(pd));
							xdEffict.setOldPdname(pd);
							xdEffict.setOldSalaryLevel(efList.get(7));
							if(efList.get(8).equals("")||efList.get(8).equals("-")){
								xdEffict.setOldSalary(0.0);
							}else{
								xdEffict.setOldSalary(Double.valueOf(efList.get(8)));
							}

							String newDetpId = orgMapping.get(efList.get(9));
							xdEffict.setNewDeptId(newDetpId);
							xdEffict.setNewDeptName(efList.get(9));
							String newpd = efList.get(10);
							xdEffict.setNewPdname(newpd);
							xdEffict.setNewPdvalue(duty.get(newpd)==null?"":duty.get(newpd));
							xdEffict.setNewSalaryLevel(efList.get(11));
							if(efList.get(12).equals("")||efList.get(12).equals("-")){
								xdEffict.setNewSalary(0.0);
							}else{
								xdEffict.setNewSalary(Double.valueOf(efList.get(12)));
							}
//							xdEffict.setNewSalary(Double.valueOf(efList.get(12)));
							xdEffict.setEffectDate(efList.get(13));
							xdEffict.setOtherRemarks(efList.get(14));
							xdEffict.setStatus("1");
							xdEffict.setCuser(ShiroKit.getUserId());
							xdEffict.setCtime(DateUtil.getCurrentTime());
							xdEffict.save();
							//employee.getChrecord()
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