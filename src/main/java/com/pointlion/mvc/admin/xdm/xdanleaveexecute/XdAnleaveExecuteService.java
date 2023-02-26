package com.pointlion.mvc.admin.xdm.xdanleaveexecute;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdAnleaveExecute;
import com.pointlion.mvc.common.model.XdSeniorityAllowance;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XdAnleaveExecuteService{
	public static final XdAnleaveExecuteService me = new XdAnleaveExecuteService();
	public static final String TABLE_NAME = XdAnleaveExecute.tableName;
	
	/***
	 * query by id
	 */
	public XdAnleaveExecute getById(String id){
		return XdAnleaveExecute.dao.findById(id);
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
    		XdAnleaveExecute o = me.getById(id);
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