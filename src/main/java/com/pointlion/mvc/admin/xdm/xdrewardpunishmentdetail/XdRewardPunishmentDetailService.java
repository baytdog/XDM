package com.pointlion.mvc.admin.xdm.xdrewardpunishmentdetail;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.util.CheckAttendanceUtil;
import com.pointlion.util.DictMapping;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class XdRewardPunishmentDetailService{
	public static final XdRewardPunishmentDetailService me = new XdRewardPunishmentDetailService();
	public static final String TABLE_NAME = XdRewardPunishmentDetail.tableName;
	
	/***
	 * query by id
	 */
	public XdRewardPunishmentDetail getById(String id){
		return XdRewardPunishmentDetail.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String year,String empName){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		if(StrKit.notBlank(year)){
			sql = sql + " and o.year>='"+ year+"'";
		}
		if(StrKit.notBlank(empName)){
			sql = sql + " and o.emp_name like '%"+empName+"%'";
		}
//		sql = sql + " order by o.create_time desc";
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
    		XdRewardPunishmentDetail o = me.getById(id);

			XdRewardPunishmentSummary rpSummary = XdRewardPunishmentSummary.dao.findFirst("select * from  xd_reward_punishment_summary where year='"+o.getYear()+"' and  dept_id='" + o.getDeptId() + "'");
			Double rewardPunishment = o.getRewardPunishment();
			String val =o.getCanDistribution();
			 rpSummary.setQuota(rpSummary.getQuota()-rewardPunishment);
			if(val.equals("是")){
				rpSummary.setAbleQuota(rpSummary.getAbleQuota()-rewardPunishment);
			}else{
				rpSummary.setDisableQuota(rpSummary.getDisableQuota()-rewardPunishment);
			}
			rpSummary.update();


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
					if(list.size()>1){
						for(int i = 1;i<list.size();i++){

							List<String> rpList = list.get(i);
							if(rpList.get(0).equals("")){
								continue;
							}
							XdRewardPunishmentDetail rpDetail=new XdRewardPunishmentDetail();
							rpDetail.setYear("2023");
							String month = rpList.get(0).replaceAll("月", "");
							rpDetail.setMonth(Integer.parseInt(month)<10?0+month:month);

							String department = rpList.get(1);
							String deptId = orgMapping.get(department);
							rpDetail.setDeptId(deptId);
							rpDetail.setDeptName(department);
							String unitName = rpList.get(2);
							String unitValue = unitMap.get(unitName);
							rpDetail.setUnitName(unitName);
							rpDetail.setUnitId(unitValue);
							String projectName = rpList.get(3);
							String projectValue = projectsMap.get(projectName);
							rpDetail.setProjectName(projectName);
							if(projectValue!=null){
								rpDetail.setProjectId(Integer.valueOf(projectValue));
							}

							String empName = rpList.get(4);
							rpDetail.setEmpName(empName);
							String idNum = rpList.get(5);
							rpDetail.setIdnum(idNum);
							Double rewardPunishment = Double.valueOf(rpList.get(6));
							rpDetail.setRewardPunishment(rewardPunishment);
							rpDetail.setRemarks(rpList.get(7));
							String canUse = rpList.get(8);
							rpDetail.setCanDistribution(canUse);
							rpDetail.save();
							System.out.println(rpDetail.getEmpName());
							XdRewardPunishmentSummary rpSummary = XdRewardPunishmentSummary.dao.findFirst("select * from  xd_reward_punishment_summary where year='2023' and  dept_id='" + deptId + "'");
							rpSummary.setQuota(rpSummary.getQuota()+rewardPunishment);
							if(canUse.equals("是")){
								rpSummary.setAbleQuota(rpSummary.getAbleQuota()+rewardPunishment);
							}else{
								rpSummary.setDisableQuota(rpSummary.getDisableQuota()+rewardPunishment);
							}
							rpSummary.update();
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