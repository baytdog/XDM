package com.pointlion.mvc.admin.oa.dct.resourcemeetroom;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.DctResourceMeetroom;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;


public class DctResourceMeetroomController extends BaseController {
	public static final DctResourceMeetroomService service = DctResourceMeetroomService.me;
	/***
	 * 列表页面
	 */
	public void getListPage(){

    	renderIframe("list.html");
    }
	/***
     * 获取分页数据
     **/
    public void listData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * 保存
     */
    public void save(){
    	DctResourceMeetroom o = getModel(DctResourceMeetroom.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.save();
    	}
    	renderSuccess();
    }
    /***
     * 获取编辑页面
     */
    public void getEditPage(){

    	//添加和修改
    	String id = getPara("id");
    	if(StrKit.notBlank(id)){//修改
    		DctResourceMeetroom o = service.getById(id);
    		setAttr("o", o);
    		//是否是查看详情页面
    		String view = getPara("view");//查看
    		if("detail".equals(view)){
    			setAttr("view", view);
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		setAttr("user", user);
    		setAttr("org", org);
    	}
    	renderIframe("edit.html");
    }
    /***
     * 删除
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
    	//执行删除
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
    
}