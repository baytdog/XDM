package com.pointlion.mvc.admin.workplan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaWorkPlan;
import com.pointlion.mvc.common.model.OaWorkPlanRemarks;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaWorkPlanController extends BaseController {
	public static final OaWorkPlanService service = OaWorkPlanService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
    }
	
	
	
	public void getWorkPlanPage() {
		
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		setAttr("today", sdf.format(new Date()));
		
		  Calendar cal = Calendar.getInstance();
          cal.setTime(new Date());
          // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一  
          cal.setFirstDayOfWeek(Calendar.MONDAY);  
          // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
          int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天  
          if (1 == dayWeek) {  
             cal.add(Calendar.DAY_OF_MONTH, -1);  
          }  
          // 获得当前日期是一个星期的第几天  
          int day = cal.get(Calendar.DAY_OF_WEEK); 
          // 获取该周第一天
          cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
          String beginDate = sdf.format(cal.getTime());
          // 获取该周最后一天
          cal.add(Calendar.DATE, 6);
          String endDate = sdf.format(cal.getTime());
		
		
          OaWorkPlanRemarks findFirst = OaWorkPlanRemarks.dao.findFirst("select  * from oa_work_plan_remarks where starttime ='"+beginDate+"'  and  endtime='"+endDate+"' and state='1'");
         
          
          
          
          setAttr("remarks",findFirst==null?"": findFirst.getRamarks());
		
		renderIframe("workplan.html");
		
	}
	public void workPlanPage() {
		
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		setAttr("today", sdf.format(new Date()));
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一  
		cal.setFirstDayOfWeek(Calendar.MONDAY);  
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天  
		if (1 == dayWeek) {  
			cal.add(Calendar.DAY_OF_MONTH, -1);  
		}  
		// 获得当前日期是一个星期的第几天  
		int day = cal.get(Calendar.DAY_OF_WEEK); 
		// 获取该周第一天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
		String beginDate = sdf.format(cal.getTime());
		// 获取该周最后一天
		cal.add(Calendar.DATE, 6);
		String endDate = sdf.format(cal.getTime());
		
		
		OaWorkPlanRemarks findFirst = OaWorkPlanRemarks.dao.findFirst("select  * from oa_work_plan_remarks where starttime ='"+beginDate+"'  and  endtime='"+endDate+"' and state='1'");
		
		String  remarks="";
		if(findFirst==null) {
			remarks="";
			
		}else {
			remarks=findFirst.getRamarks();
			
		}
		
		setAttr("remarks", remarks);
		
		renderIframe("showWorkPlan.html");
		
	}
	
	
	/***
	 * 保存
	 */
	public void save(){
		OaWorkPlan model = getModel(OaWorkPlan.class);
		//service.save(model);
		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
		model.setCuer(user.getUsername());
		model.setCdate(DateUtil.getCurrentTime());
		model.setEnd(model.getEnd()+"T12:01:00"); 
		model.save();//保存公告
		System.out.println("-----------------"+model.getId());
		renderSuccess();
	}
	
	
	public  void del() {
		
		OaWorkPlan model = getModel(OaWorkPlan.class);
		model.deleteById(model.getId());
		renderSuccess();
	}
	
	
	
	public void getWorkPlan(){
		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
				//List<OaWorkPlan> workPlanList = OaWorkPlan.dao.find("select  * from  oa_work_plan  where cuer='"+user.getUsername()+"'  and  title is not null   and  start is not  null  and  end  not like '%null%'  ");
				List<OaWorkPlan> workPlanList = OaWorkPlan.dao.find("select  * from  oa_work_plan  where   title is not null   and  start is not  null  and  end  not like '%null%'  ");
		//service.save(model);
		/*SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
		model.setCuer(user.getUsername());
		model.setCdate(DateUtil.getCurrentTime());
		model.save();//保存公告
		System.out.println("-----------------"+model.getId());
		renderSuccess();*/
		
				
				renderJson(workPlanList);
	}
	
	
	/***
     * list page data
     **/
    public void listData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String applyUser = getPara("applyUser","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,applyUser);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
 /*   public void save(){
    	OaWorkPlan o = getModel(OaWorkPlan.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.setCreateTime(DateUtil.getCurrentTime());
    		o.save();
    	}
    	renderSuccess();
    }*/
    /***
     * edit page
     */
   /* public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		OaWorkPlan o = new OaWorkPlan();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
			o.setOrgId(org.getId());
			o.setOrgName(org.getName());
			o.setUserid(user.getId());
			o.setApplyerName(user.getName());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaWorkPlan.class.getSimpleName()));
		renderIframe("edit.html");
    }*/
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
   /* *//***
     * submit
     *//*
    public void startProcess(){
    	String id = getPara("id");
    	OaWorkPlan o = OaWorkPlan.dao.getById(id);
    	o.setIfSubmit(Constants.IF_SUBMIT_YES);
		String insId = wfservice.startProcess(id, o,null,null);
    	o.setProcInsId(insId);
    	o.update();
    	renderSuccess("submit success");
    }*/
    /***
     * callBack
     */
/*    public void callBack(){
    	String id = getPara("id");
    	try{
    		OaWorkPlan o = OaWorkPlan.dao.getById(id);
        	wfservice.callBack(o.getProcInsId());
        	o.setIfSubmit(Constants.IF_SUBMIT_NO);
        	o.setProcInsId("");
        	o.update();
    		renderSuccess("callback success");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("callback fail");
    	}
    }*/

    
    
    public void saveRemark() {
    	  Calendar cal = Calendar.getInstance();
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          cal.setTime(new Date());
          // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一  
          cal.setFirstDayOfWeek(Calendar.MONDAY);  
          // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
          int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天  
          if (1 == dayWeek) {  
             cal.add(Calendar.DAY_OF_MONTH, -1);  
          }  
          // 获得当前日期是一个星期的第几天  
          int day = cal.get(Calendar.DAY_OF_WEEK); 
          // 获取该周第一天
          cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
          String beginDate = sdf.format(cal.getTime());
          // 获取该周最后一天
          cal.add(Calendar.DATE, 6);
          String endDate = sdf.format(cal.getTime());
          System.out.println(beginDate);
          System.out.println(endDate);
    	
          System.out.println(getPara("comment"));
          
          Db.update("update  oa_work_plan_remarks  set state='0' where starttime ='"+beginDate+"'  and  endtime='"+endDate+"' and state='1'");
          OaWorkPlanRemarks owr=new OaWorkPlanRemarks();
          
          
          
          owr.setId(UuidUtil.getUUID());
          owr.setStartTime(beginDate);
          owr.setEndTime(endDate);
          owr.setRamarks(getPara("comment"));
          owr.setCuser(ShiroKit.getUserId());
          owr.setCtime(DateUtil.getCurrentTime());
          owr.setState("1");
          owr.save();
          
          
    	
    	
    }
	
}