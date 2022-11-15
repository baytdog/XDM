package com.pointlion.mvc.admin.oa.old.tabfile;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaBumph;
import com.pointlion.mvc.common.model.OaTabFile;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaTabFileController extends BaseController {
	public static final OaTabFileService service = OaTabFileService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
    }
	/***
     * list page data
	 * @throws UnsupportedEncodingException 
     **/
    public void listData() throws UnsupportedEncodingException{
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String type = getPara("type");
    	
    	
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String rdnum = java.net.URLDecoder.decode(getPara("rdnum",""),"UTF-8");
		String rtitle = java.net.URLDecoder.decode(getPara("rtitle",""),"UTF-8");
		String rFileNo = java.net.URLDecoder.decode(getPara("rFileNo",""),"UTF-8");
		
		String rstate = getPara("rstate","");
    	
    	
    	Page<OaTabFile> page = OaTabFile.dao.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),type, rdnum,  rtitle,  rFileNo,  rstate,  startTime,  endTime);
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    
    
    /***
	 * 编辑公文起草页面
	 */
	public void getDraftEditPage(){
		
		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"' order by sort");
		setAttr("userList",userList);
		
		
		List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
		
		setAttr("orgLists",orgLists);
		
		
		List<SysUser> nbr = SysUser.dao.find("select * from  sys_user where orgid='"+ShiroKit.getUserOrgId()+"' and position in ('2','6') order by sort");
		
		setAttr("nbr",nbr);
		
		String type = getPara("type");
	 
		setAttr("type", type);
		setAttr("currentUserId",ShiroKit.getUserId());
		//添加和修改
    	String id = getPara("id");//修改
		String view = getPara("view");//查看
		setAttr("view", view);
		if(StrKit.notBlank(id)){//修改
			OaTabFile o = OaTabFile.dao.findById(id);
			setAttr("o", o);
			//获取主送和抄送单位
			//是否是查看详情页面
			if("detail".equals(view)){
    		}
    	}else{//新增
    		OaBumph o = new OaBumph();
    		String userId = ShiroKit.getUserId();//用户主键
    		SysUser user = SysUser.dao.getById(userId);//用户对象
    		SysOrg org = SysOrg.dao.getById(user.getOrgid());//单位对象
    		o.setDocNumYear(DateUtil.getCurrentYear());
    		o.setSenderId(userId);
    		o.setSenderName(user.getName());
    		o.setSenderOrgid(org.getId());
    		o.setSenderOrgname(org.getName());
    		o.setStatus("0");
    		o.setId(UuidUtil.getUUID());
    		
    		
    		setAttr("o",o);
    		
 
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaTabFile.class.getSimpleName()));//模型名称
    	renderIframe("edit.html");
	}
    
    
    
    /***
     * save data
     */
    public void save(){
    	OaTabFile o = getModel(OaTabFile.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.save();
    	}
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		OaTabFile o = new OaTabFile();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    		}
    	}else{
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaTabFile.class.getSimpleName()));
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
    /***
     * submit
     */
    public void startProcess(){
    	String id = getPara("id");
    	OaTabFile o = OaTabFile.dao.getById(id);
    	o.update();
    	renderSuccess("submit success");
    }
    /***
     * callBack
     */
    public void callBack(){
    	String id = getPara("id");
    	try{
    		OaTabFile o = OaTabFile.dao.getById(id);
        	o.update();
    		renderSuccess("callback success");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("callback fail");
    	}
    }

	
}