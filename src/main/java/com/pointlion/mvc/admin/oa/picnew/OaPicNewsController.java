package com.pointlion.mvc.admin.oa.picnew;

import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaBumph;
import com.pointlion.mvc.common.model.OaPicNews;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaPicNewsController extends BaseController {
	public static final OaPicNewsService service = OaPicNewsService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;

	/***
	 * get list page
	 */
	public void getListPage() {
		renderIframe("list.html");
	}
	/***
	 * list page data
	 **/
	/*
	 * public void listData(){ String curr = getPara("pageNumber"); String pageSize
	 * = getPara("pageSize"); String endTime = getPara("endTime",""); String
	 * startTime = getPara("startTime",""); String applyUser =
	 * getPara("applyUser",""); Page<Record> page =
	 * service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,
	 * endTime,applyUser); renderPage(page.getList(),"",page.getTotalRow()); }
	 */
	/***
	 * save data
	 */
	/*
	 * public void save(){ OaPicNews o = getModel(OaPicNews.class);
	 * if(StrKit.notBlank(o.getId())){ o.update(); }else{
	 * o.setId(UuidUtil.getUUID()); //o.setCreateTime(DateUtil.getCurrentTime());
	 * o.setCdate(DateUtil.getCurrentTime()); o.save(); } renderSuccess(); }
	 */

	/***
	 * del
	 * 
	 * @throws Exception
	 */

	public void delete() throws Exception {
		String ids = getPara("ids");
		String[] idsA = ids.split(",");
		for (String id : idsA) {
			OaPicNews.dao.deleteById(id);
		}
		renderSuccess("删除成功!");
	}

	/***
	 * submit
	 */
	public void startProcess() {
		/*
		 * String id = getPara("id"); OaPicNews o = OaPicNews.dao.getById(id);
		 * o.setIfSubmit(Constants.IF_SUBMIT_YES); String insId =
		 * wfservice.startProcess(id, o,null,null); o.setProcInsId(insId); o.update();
		 * renderSuccess("submit success");
		 */}

	/***
	 * callBack
	 */
	public void callBack() {
		/*
		 * String id = getPara("id"); try{ OaPicNews o = OaPicNews.dao.getById(id);
		 * wfservice.callBack(o.getProcInsId()); o.setIfSubmit(Constants.IF_SUBMIT_NO);
		 * o.setProcInsId(""); o.update(); renderSuccess("callback success");
		 * }catch(Exception e){ e.printStackTrace(); renderError("callback fail"); }
		 */}

	/***
	 * 编辑公文起草页面
	 */
	public void getDraftEditPage() {

		// String parentPath =
		// this.getRequest().getServletPath().substring(0,this.getRequest().getServletPath().lastIndexOf("/")+1);

		// 添加和修改
		String id = getPara("id");// 修改
		String view = getPara("view");// 查看
		setAttr("view", view);
		if (StrKit.notBlank(id)) {// 修改
			OaBumph o = OaBumph.dao.findById(id);
			setAttr("o", o);
			// 获取主送和抄送单位
			// service.setAttrFirstSecond(this,o.getId());
			// 是否是查看详情页面
			if ("detail".equals(view)) {
				if (StrKit.notBlank(o.getProcInsId())) {
					setAttr("procInsId", o.getProcInsId());
					setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
				}
			}
		} else {// 新增
			OaPicNews o = new OaPicNews();
			// String userId = ShiroKit.getUserId();//用户主键
			// SysUser user = SysUser.dao.getById(userId);//用户对象
			// SysOrg org = SysOrg.dao.getById(user.getOrgid());//单位对象
			/*
			 * o.setDocNumYear(DateUtil.getCurrentYear()); o.setSenderId(userId);
			 * o.setSenderName(user.getName()); o.setSenderOrgid(org.getId());
			 * o.setSenderOrgname(org.getName());
			 */
			setAttr("o", o);
		}
		setAttr("formModelName", StringUtil.toLowerCaseFirstOne(OaPicNews.class.getSimpleName()));// 模型名称
		renderIframe("edit.html");
	}

	public void getListData() {
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		Page<OaPicNews> page = OaPicNews.dao.getPage(Integer.valueOf(curr), Integer.valueOf(pageSize));
		renderPage(page.getList(), "", page.getTotalRow());
	}

	/***
	 * 获取通知公告起草页面
	 */
	public void getEditPage() {
		// String parentPath =
		// this.getRequest().getServletPath().substring(0,this.getRequest().getServletPath().lastIndexOf("/")+1);

		// 是否是查看详情页面
		String view = getPara("view");
		if ("detail".equals(view)) {
			setAttr("view", view);
		}
		String id = getPara("id");
		if (StrKit.notBlank(id)) {
			setAttr("o", OaPicNews.dao.findById(id));
		} else {
			OaPicNews o = new OaPicNews();
			String userId = ShiroKit.getUserId();// 用户主键
			SysUser user = SysUser.dao.getById(userId);// 用户对象
			// SysOrg org = SysOrg.dao.getById(user.getOrgid());//单位对象
			// o.setSenderId(userId);
			o.setCuser(user.getName());
			o.setCdate(DateUtil.getCurrentTime());
//    		o.setSenderName(user.getName());
//    		o.setSenderOrgId(org.getId());
//    		o.setSenderOrgName(org.getName());
			setAttr("o", o);
		}
		renderIframe("edit.html");
	}

	/***
	 * 保存
	 */
	public void save() {
		OaPicNews model = getModel(OaPicNews.class);
		service.save(model);
		System.out.println("-----------------" + model.getId());
		renderSuccess();
	}

	/***
	 * 业务数据上传附件
	 */
	public void getBusinessUploadListPage() {
		String busid = getPara("busid", "");
		String view = getPara("view", "");
		setAttr("busid", busid);
		setAttr("view", view);
		renderIframeOpen("businessUploadList.html");
	}

	/**
	 * 设置照片轮播图显示
	 */

	public void setShowPic() {

		String ids = getPara("ids");
		String busid = getPara("busid");
		// 执行删除
		service.setShowPic(busid, ids);
		renderSuccess("设置成功!");
	}

	/**
	 * 
	 * @Title: showAllPic
	 * @Description: 显示图片新闻
	 * @date 2020年8月27日下午1:20:44
	 */
	public void showAllPic() {

		String busid = getPara("busid");
		OaPicNews oaPicN = OaPicNews.dao.findById(busid);

		List<SysAttachment> piclist = SysAttachment.dao
				.find("select * from  sys_attachment  where business_id='" + busid + "' and id !='"+oaPicN.getImgurl()+"'  order by  create_time  asc");

		setAttr("o", oaPicN);

		set("piclist", piclist);

		renderIframe("showPicNs.html");

	}

	public void showPicNewsList() {

		renderIframe("listPicD.html");
	}
}