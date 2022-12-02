package com.pointlion.mvc.admin.xdm.xdemployee;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.XdEdutrain;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.model.XdWorkExper;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.JSONUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.time.format.DateTimeFormatter;
import java.util.List;


public class XdEmployeeController extends BaseController {
	public static final XdEmployeeService service = XdEmployeeService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
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
    public void save(){
		DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String gridData1 = getPara("gridData1");
		String gridData2 = getPara("gridData2");
		XdEmployee o = getModel(XdEmployee.class);
		List<XdEdutrain> gridList1 = JSONUtil.jsonToBeanList(gridData1, XdEdutrain.class);
		List<XdWorkExper> gridList2 = JSONUtil.jsonToBeanList(gridData2, XdWorkExper.class);
		String id = o.getId();
		XdEmployee employee = XdEmployee.dao.findById(id);
		if(employee != null){

			o.update();
			if(gridList1.size() == 0) {
				Db.delete("delete from  xd_edutrain where eid='"+o.getId()+"'");
			}else{
				for (XdEdutrain xdEdutrain : gridList1) {
					if("".equals(xdEdutrain.getId())){
						xdEdutrain.setId(UuidUtil.getUUID());
						xdEdutrain.setEid(o.getId());
						xdEdutrain.setEnrolldate(xdEdutrain.getEnrolldate().length()>9?xdEdutrain.getEnrolldate().substring(0,10):"");
						xdEdutrain.setGraduatdate(xdEdutrain.getGraduatdate().length()>9?xdEdutrain.getGraduatdate().substring(0,10):"");
						xdEdutrain.setCuser(ShiroKit.getUserId());
						xdEdutrain.setCtime(DateUtil.getCurrentTime());
						xdEdutrain.save();
					}else{
						XdEdutrain.dao.deleteByIds(xdEdutrain.getId());
						xdEdutrain.setEnrolldate(xdEdutrain.getEnrolldate().length()>9?xdEdutrain.getEnrolldate().substring(0,10):"");
						xdEdutrain.setGraduatdate(xdEdutrain.getGraduatdate().length()>9?xdEdutrain.getGraduatdate().substring(0,10):"");
						xdEdutrain.save();
					}
				}
			}
			if(gridList2.size() == 0) {
				Db.delete("delete from  xd_work_exper where eid='"+o.getId()+"'");
			}else{
				for (XdWorkExper workExper : gridList2) {
					if("".equals(workExper.getId())){
						workExper.setId(UuidUtil.getUUID());
						workExper.setEid(o.getId());
						workExper.setEntrydate(workExper.getEntrydate().length()>9?workExper.getEntrydate().substring(0,10):"");
						workExper.setDepartdate(workExper.getDepartdate().length()>9?workExper.getDepartdate().substring(0,10):"");
						workExper.setCtime(DateUtil.getCurrentTime());
						workExper.setCuser(ShiroKit.getUserId());
						workExper.save();
					}else{
						XdWorkExper.dao.deleteByIds(workExper.getId());
						workExper.save();
					}
				}
			}

		}else{
			o.setId(UuidUtil.getUUID());
    		o.setCtime(DateUtil.getCurrentTime());
    		o.setCuser(ShiroKit.getUserId());
    		o.save();
    		if(gridList1.size()!=0){
				for (int i = 0; i < gridList1.size(); i++) {
					XdEdutrain xdEdutrain = gridList1.get(i);
					xdEdutrain.setId(UuidUtil.getUUID());
					xdEdutrain.setEid(o.getId());
					xdEdutrain.setEnrolldate(xdEdutrain.getEnrolldate().length()>9?xdEdutrain.getEnrolldate().substring(0,10):"");
					xdEdutrain.setGraduatdate(xdEdutrain.getGraduatdate().length()>9?xdEdutrain.getGraduatdate().substring(0,10):"");
					xdEdutrain.setCuser(ShiroKit.getUserId());
					xdEdutrain.setCtime(DateUtil.getCurrentTime());
					xdEdutrain.save();
				}
			}
    		if(gridList2.size()!=0){
				for (XdWorkExper workExper : gridList2) {
					workExper.setId(UuidUtil.getUUID());
					workExper.setEid(o.getId());
					workExper.setEntrydate(workExper.getEntrydate().length()>9?workExper.getEntrydate().substring(0,10):"");
					workExper.setDepartdate(workExper.getDepartdate().length()>9?workExper.getDepartdate().substring(0,10):"");
					workExper.setCtime(DateUtil.getCurrentTime());
					workExper.setCuser(ShiroKit.getUserId());
					workExper.save();
				}
			}


		}






//    	if(StrKit.notBlank(o.getId())){
//    		o.update();
//    	}else{
//    		o.setId(UuidUtil.getUUID());
//    		o.setCreateTime(DateUtil.getCurrentTime());
//    		o.save();
//    	}
			renderSuccess();
		}
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdEmployee o = new XdEmployee();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    		}
    	}else{
//    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
//    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
//			o.setOrgId(org.getId());
//			o.setOrgName(org.getName());
//			o.setUserid(user.getId());
//			o.setApplyerName(user.getName());
//			 UUIDUtil.uuid().toString();
			String uuid = UuidUtil.getUUID();
			System.out.println(uuid);
			o.setId(uuid);
		}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdEmployee.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{

		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }



}