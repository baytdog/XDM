package com.pointlion.mvc.admin.xdm.xdemployee;

import com.google.gson.JsonArray;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.XdEdutrain;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.model.XdWorkExper;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.JSONUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.util.List;


public class XdEmployeeController extends BaseController {
	public static final XdEmployeeService service = XdEmployeeService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
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

		String gridData1 = getPara("gridData1");
		String gridData2 = getPara("gridData2");
		System.out.println(gridData1);
		System.out.println("=============");
		System.out.println(gridData2);
		XdEmployee o = getModel(XdEmployee.class);
		System.out.println(o);

		String id = o.getId();
		XdEmployee employee = XdEmployee.dao.findById(id);
		if(employee != null){

		}else{
			o.setId(UuidUtil.getUUID());
    		o.setCtime(DateUtil.getCurrentTime());
    		o.setCuser(ShiroKit.getUserId());
    		//o.save();
    		if(!"".equals(gridData1)){

				/* JSONArray jsonArray=new  JSONArray(gridData1);
				for (int i = 0; i < jsonArray.size(); i++) {
					XdEdutrain xdEdutrain = jsonArray.get(i, XdEdutrain.class);
					XdWorkExper workExper = jsonArray.get(i, XdWorkExper.class,true);
					System.out.println(xdEdutrain);
				}*/

				/*JSONObject json =new JSONObject();
				JSONArray jsonArray = json.getJSONArray(gridData1);*/
				List<XdEdutrain> list = JSONUtil.jsonToBeanList(gridData1, XdEdutrain.class);
				for (int i = 0; i < list.size(); i++) {

					System.out.println(list.get(i));
				}


			}
    		/*if(!"".equals(gridData2)){

			}*/


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
//    			if(StrKit.notBlank(o.getProcInsId())){
//    				setAttr("procInsId", o.getProcInsId());
//    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
//    			}
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