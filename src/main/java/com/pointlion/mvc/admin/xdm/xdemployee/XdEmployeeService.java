package com.pointlion.mvc.admin.xdm.xdemployee;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.JSONUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.XdOperUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.utils.DateUtil;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
			o.setBackup1("D");
			o.update();
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


	public void modifyObj(XdEmployee newEmp,XdEmployee oldEmp,List<XdEdutrain> gridList1,List<XdWorkExper> gridList2){
		boolean rs ="1".equals(ShiroKit.getUserOrgId());
		String summaryStatus=XdOperEnum.WAITAPPRO.name();
		if (rs) {
			summaryStatus=XdOperEnum.UNAPPRO.name();
		}

		boolean flag=false;
		String employeeChanges = XdOperUtil.getChangedMetheds(newEmp, oldEmp);
		employeeChanges = employeeChanges.replaceAll("-$","");
		List<XdOplogDetail> list =new ArrayList<>();
		if(!"".equals(employeeChanges)){
			String lid=UuidUtil.getUUID();
			String[] empCArray = employeeChanges.split("-");
			for (String change : empCArray) {
				change="{"+change+"}";
				System.out.println(change);
				XdOplogDetail logDetail = JSONUtil.jsonToBean(change, XdOplogDetail.class);
				logDetail.setRsid(lid);
				list.add(logDetail);
			}
			flag=true;
			XdOperUtil.logSummary(lid,oldEmp.getId(),newEmp,oldEmp,XdOperEnum.U.name(),summaryStatus);
		}
		if(rs){
			newEmp.update();
		}

		List<XdEdutrain> eduTrainList = XdEdutrain.dao.find("select * from xd_edutrain where eid='" + oldEmp.getId() + "'");
		Map<String,XdEdutrain> eduMap =new HashMap<>();
		for (XdEdutrain xdEdutrain : eduTrainList) {
			eduMap.put(xdEdutrain.getId(),xdEdutrain);
		}
		if(gridList1.size() == 0) {
			for (XdEdutrain xdEdutrain : eduTrainList){
				if(rs){
					xdEdutrain.delete();
				}
				XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),null,xdEdutrain,XdOperEnum.D.name(),summaryStatus);
				flag=true;
			}
		}else{
			for (XdEdutrain xdEdutrain : gridList1) {
				if("".equals(xdEdutrain.getId())){
					xdEdutrain.setEid(oldEmp.getId());
					if (rs) {
						xdEdutrain.save(xdEdutrain);
					}
					XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),null,xdEdutrain,XdOperEnum.C.name(),summaryStatus);
				}else{
					XdEdutrain oldXdEduTrain = eduMap.get(xdEdutrain.getId());
					if(oldXdEduTrain!=null){
						xdEdutrain.setEnrolldate(xdEdutrain.getEnrolldate().length()>9?xdEdutrain.getEnrolldate().substring(0,10):"");
						xdEdutrain.setGraduatdate(xdEdutrain.getGraduatdate().length()>9?xdEdutrain.getGraduatdate().substring(0,10):"");

						String changedEduTrain = XdOperUtil.getChangedMetheds(xdEdutrain, oldXdEduTrain);
						changedEduTrain= changedEduTrain.replaceAll("-$", "");
						eduMap.remove(oldXdEduTrain.getId());
						if(!"".equals(changedEduTrain)){//有变动插入日志汇总和日志详情表
							if (rs) {
								xdEdutrain.update();
							}
							flag=true;
							String lid =UuidUtil.getUUID();
							XdOperUtil.logSummary(lid,oldEmp.getId(),xdEdutrain,oldXdEduTrain,XdOperEnum.U.name(),summaryStatus);
							String[] eduCArry = changedEduTrain.split("-");
							for (String edu : eduCArry) {
								edu="{"+edu+"}";
								XdOplogDetail logDetail = JSONUtil.jsonToBean(edu, XdOplogDetail.class);
								logDetail.setRsid(lid);
								list.add(logDetail);
							}

						}
					}
				}
				Collection<XdEdutrain> entries = eduMap.values();
				Iterator<XdEdutrain> iterator = entries.iterator();
				while (iterator.hasNext()){
					XdEdutrain next = iterator.next();
					if (rs) {
						next.delete();
					}
					flag=true;
					XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),null,next,XdOperEnum.D.name(),summaryStatus);
				}
			}
		}

		List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from xd_work_exper where eid='" + oldEmp.getId() + "'");
		Map<String,XdWorkExper> workMap =new HashMap<>();
		for (XdWorkExper workExper : workExperList) {
			workMap.put(workExper.getId(),workExper);
		}
		if(gridList2.size() == 0) {
			for (XdWorkExper workExper : workExperList){
				if (rs) {
					workExper.delete();
				}
				XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),null,workExper,XdOperEnum.D.name(),summaryStatus);
				flag=true;
			}
		}else{
			for (XdWorkExper workExper : gridList2) {
				if("".equals(workExper.getId())){
					workExper.setEid(oldEmp.getId());
					if (rs) {
						workExper.save(workExper);
					}
					XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),null,workExper,XdOperEnum.C.name(),summaryStatus);
					flag=true;
				}else{
					XdWorkExper oldWorkExper = workMap.get(workExper.getId());
					if(oldWorkExper!=null){
						workExper.setEntrydate(workExper.getEntrydate().length()>9?workExper.getEntrydate().substring(0,10):"");
						workExper.setDepartdate(workExper.getDepartdate().length()>9?workExper.getDepartdate().substring(0,10):"");
						String changedWorkExper = XdOperUtil.getChangedMetheds(workExper, oldWorkExper);
						changedWorkExper= changedWorkExper.replaceAll("-$", "");
						workMap.remove(oldWorkExper.getId());
						if(!"".equals(changedWorkExper)){//有变动插入日志汇总和日志详情表
							if (rs) {
								workExper.update();
							}
							flag=true;
							String lid =UuidUtil.getUUID();
							XdOperUtil.logSummary(lid,oldEmp.getId(),workExper,oldWorkExper,XdOperEnum.U.name(),summaryStatus);
							String[] workCArry = changedWorkExper.split("-");
							for (String work : workCArry) {

								work="{"+work+"}";
								XdOplogDetail logDetail = JSONUtil.jsonToBean(work, XdOplogDetail.class);
								logDetail.setRsid(lid);
								list.add(logDetail);
							}
						}
					}
				}
				Collection<XdWorkExper> entries = workMap.values();
				Iterator<XdWorkExper> iterator = entries.iterator();
				while (iterator.hasNext()){
					XdWorkExper next = iterator.next();
					if (rs) {
						next.delete();
					}
					flag=true;
					XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),null,next,XdOperEnum.D.name(),summaryStatus);
				}
			}
		}


		for (XdOplogDetail detail : list) {
			detail.setId(UuidUtil.getUUID());
			detail.save();
		}

		if(!rs && flag){
			XdOperUtil.insertEmpoloyeeSteps(newEmp,"","1","","","U");
		}
	}
}