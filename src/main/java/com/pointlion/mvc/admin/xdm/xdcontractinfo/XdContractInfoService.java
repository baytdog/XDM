package com.pointlion.mvc.admin.xdm.xdcontractinfo;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdContractInfo;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import com.pointlion.util.DictMapping;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XdContractInfoService{
	public static final XdContractInfoService me = new XdContractInfoService();
	public static final String TABLE_NAME = XdContractInfo.tableName;
	
	/***
	 * query by id
	 */
	public XdContractInfo getById(String id){
		return XdContractInfo.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pNum,int pSize,String name,String empNum,String startTime){
//		String userId = ShiroKit.getUserId();
		String userOrgId = ShiroKit.getUserOrgId();

		String sql  = "  from  xd_employee  e LEFT JOIN xd_contract_info c on e.name=c.backup1 where 1=1";
		if(!userOrgId.equals("1")){
			sql = sql + " and e.department  ='"+userOrgId+"'";
		}

		if(StrKit.notBlank(name)){

			sql = sql + " and e.name  like '%"+name+"%'";
//			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}

		if(StrKit.notBlank(empNum)){
			sql = sql + " and e.empnum  like '%"+empNum+"%'";
//			sql = sql + " and o.create_time<='"+""+"'";
		}
		if(StrKit.notBlank(startTime)){
			sql = sql + " and c.contractstartdate >= '"+startTime+"'";
		}
		sql = sql + " order by e.empnum ";
		return Db.paginate(pNum, pSize, "select c.id,e.empnum,e.idnum,e.department,e.name,e.age,e.emprelation,c.contractstartdate," +
				"c.contractenddate,c.contractclauses,c.contractnature", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdContractInfo o = me.getById(id);
    		o.delete();
    	}
	}


	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Map<String, String>> dictMapping= DictMapping.dictMapping();
		Map<String, String> eudMap = dictMapping.get("edu");
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>2){
						SimpleUser user = ShiroKit.getLoginUser();
						String time = DateUtil.getCurrentTime();
						for(int i = 2;i<list.size();i++){//从第二行开始取
							List<String> eduStr = list.get(i);
							XdContractInfo contractInfo=new XdContractInfo();
							contractInfo.setId(UuidUtil.getUUID());
							contractInfo.setCtime(time);
							contractInfo.setCuser(user.getId());
							if(eduStr.get(3)==null ||"".equals(eduStr.get(3).trim())){
								continue;
							}
							XdEmployee employee = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + eduStr.get(3) + "'");
							contractInfo.setEid(employee==null?"":employee.getId());
							contractInfo.setBackup1(eduStr.get(3));

							boolean flag=false;
							int j=7;
							while (true){
								int k = (j-6) % 4;
								if(j>=39 ||(k==1 && "".equals(eduStr.get(j)))){
									flag=true;
									break;
								}else{
									switch (k){
										case 1:
											contractInfo.setContractstartdate(eduStr.get(j)==null?"":eduStr.get(j));
											break;
										case 2:
											contractInfo.setContractenddate(eduStr.get(j)==null?"":eduStr.get(j));
											break;
										case 3:
											contractInfo.setContractclauses(Integer.valueOf(eduStr.get(j)==null?"0":eduStr.get(j)));
											break;
										case 0:
											contractInfo.setContractnature(eduStr.get(j)==null?"":eduStr.get(j));
											contractInfo.setId(UuidUtil.getUUID());
											contractInfo.save();
											break;
									}

								}
								j++;
							}

							if (flag) {
								continue;
							}


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