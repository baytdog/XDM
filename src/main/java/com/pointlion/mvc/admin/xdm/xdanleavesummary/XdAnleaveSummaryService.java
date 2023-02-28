package com.pointlion.mvc.admin.xdm.xdanleavesummary;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XdAnleaveSummaryService{
	public static final XdAnleaveSummaryService me = new XdAnleaveSummaryService();
	public static final String TABLE_NAME = XdAnleaveSummary.tableName;
	
	/***
	 * query by id
	 */
	public XdAnleaveSummary getById(String id){
		return XdAnleaveSummary.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String name,String dept,String year){
		String sql  = " from "+TABLE_NAME+" o    where 1=1";
		if(StrKit.notBlank(name)){
			sql = sql + " and o.emp_name like '%"+ name+"%'";
		}
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_id='"+dept+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.year ='"+year+"'";
		}
		sql = sql + " order by o.emp_name,o.year desc ";
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
    		XdAnleaveSummary o = me.getById(id);
    		o.delete();
    	}
	}

	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {

		final Map<String,Object> result = new HashMap<String,Object>();
		final List<String> message = new ArrayList<String>();
		Map<String, String> orgMapping = DictMapping.orgMapping("1");
		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMapping();
		Map<String, String> unitMap = stringMapMap.get("unit");
		Map<String, String> projectsMap = DictMapping.projectsMapping();

		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>2){
						String year = list.get(0).get(0).replaceAll("\\s*", "").replaceAll("[^(0-9)]", "");

						for(int i = 2;i<list.size();i++){

							List<String> anList = list.get(i);
							if(anList.get(1).equals("")){
								continue;
							}
							XdAnleaveSummary summary=new XdAnleaveSummary();
							summary.setYear(year);
//							String month = rpList.get(0).replaceAll("月", "");
							//rpDetail.setMonth(Integer.parseInt(month)<10?0+month:month);

							String department = anList.get(1);
							String deptId = orgMapping.get(department);
							summary.setDeptId(deptId);
							summary.setDeptName(department);
							String projectName = anList.get(2);
							String projectValue = projectsMap.get(projectName);
							summary.setProjectName(projectName);
							if(projectValue!=null){
								summary.setProjectValue(projectValue);
							}
							summary.setEmpNum(anList.get(3));

							String empName = anList.get(4);
							summary.setEmpName(empName);
							String idNum = anList.get(5);
							summary.setIdnum(idNum);
							summary.setEmpRelation(anList.get(6));
							summary.setHireDate(anList.get(7));
							summary.setLeaveDays(anList.get(8));
							summary.setSurplusDays(anList.get(9));
							summary.setRemarks(anList.get(23));
							summary.setDepartime(anList.get(24));
							summary.save();
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