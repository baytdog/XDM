/**
 * @author Lion
 * @date 2017年1月24日 下午12:02:35
 * @qq 439635374
 */
package com.pointlion.mvc.common.base;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.render.JsonRender;
import com.pointlion.mvc.common.dto.BootstrapValid;
import com.pointlion.mvc.common.dto.RenderBean;
import com.pointlion.mvc.common.model.OaBumph;
import com.pointlion.mvc.common.model.OaBumphUser;
import com.pointlion.mvc.common.model.OaNotices;
import com.pointlion.mvc.common.model.OaShowHome;
import com.pointlion.mvc.common.model.OaShowinfo;
import com.pointlion.mvc.common.model.OaStepHistory;
import com.pointlion.mvc.common.model.OaSteps;
import com.pointlion.mvc.common.model.OaTypes;
import com.pointlion.mvc.common.model.SysCustomSetting;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.plugin.shiro.ShiroKit;

public abstract class BaseController extends Controller {
	protected static String messageSuccess = "操作成功";	
	protected static String messageFail = "操作失败";
	/**
	 * 重写renderJson，避免出现IE8下出现下载弹出框
	 */
	@Override
	public void renderJson(Object object) {
		String userAgent = getRequest().getHeader("User-Agent");
		if(userAgent.toLowerCase().indexOf("msie") != -1){
			render(new JsonRender(object).forIE());
		}else{
			super.renderJson(object);
		}
	}
	
	/**
	 * 解决IE8下下载失败的问题
	 */
	@Override
	public void renderFile(File file) {
		String userAgent = getRequest().getHeader("User-Agent");
		if(userAgent.toLowerCase().indexOf("msie") != -1){
			getResponse().reset(); 
		}
		super.renderFile(file);
	}

	/**
	 * 解决IE8下下载失败的问题
	 */
	public void renderFile(File file, String downloadSaveFileName) {
		String userAgent = getRequest().getHeader("User-Agent");
		if(userAgent.toLowerCase().indexOf("msie") != -1){
			getResponse().reset(); 
			 
		}
		renderFile(file, downloadSaveFileName);
	}
	
	/***
	 * 分页数据
	 */
	public void renderPage(Object data,String msg ,int count){
		RenderBean renderBean = new RenderBean();
		renderBean.setSuccess(true);
		renderBean.setRows(data);
		renderBean.setMessage(msg);
		renderBean.setTotal(count);
		renderJson(renderBean);
	}
	
	/**
	 * 自定义render
	 * @param data 返回数据
	 * @param description 描述
	 * 描述：公共render，所有的renderJson都必须返回RenderObject，包含处理状态、返回数据、失败下的状态码、失败描述
	 */
	public void renderSuccess(Object data, String description) {
		RenderBean renderBean = new RenderBean();
		renderBean.setSuccess(true);
		renderBean.setData(data);
		if(StrKit.isBlank(description)){
			renderBean.setMessage(messageSuccess);
		}else{
			renderBean.setMessage(description);
		}
		renderJson(renderBean);
	}
	public void renderSuccess(String message) {
		RenderBean renderBean = new RenderBean();
		renderBean.setSuccess(true);
		renderBean.setMessage(message);
		renderJson(renderBean);
	}
	public void renderSuccess() {
		RenderBean renderBean = new RenderBean();
		renderBean.setSuccess(true);
		renderBean.setMessage(messageSuccess);
		renderJson(renderBean);
	}
	public void renderValidSuccess(){
		BootstrapValid v = new BootstrapValid();
		v.setValid(true);
		renderJson(v);
	}
	public void renderValidFail(){
		BootstrapValid v = new BootstrapValid();
		v.setValid(false);
		renderJson(v);
	}
	/**
	 * 自定义render失败
	 * @param data 返回数据
	 * @param description 描述
	 * 描述：公共render，所有的renderJson都必须返回RenderObject，包含处理状态、返回数据、失败下的状态码、失败描述
	 */
	public void renderError(Object data, String description) {
		RenderBean renderBean = new RenderBean();
		renderBean.setSuccess(false);
		renderBean.setMessage(description);
		renderJson(renderBean);
	}
	public void renderError(Object data) {
		RenderBean renderBean = new RenderBean();
		renderBean.setSuccess(false);
		renderBean.setMessage(messageFail);
		renderJson(renderBean);
	}
	public void renderError(String message) {
		RenderBean renderBean = new RenderBean();
		renderBean.setSuccess(false);
		renderBean.setMessage(message);
		renderJson(renderBean);
	}
	public void renderError() {
		RenderBean renderBean = new RenderBean();
		renderBean.setSuccess(false);
		renderBean.setMessage(messageFail);
		renderJson(renderBean);
	}

	/**
	 * 表单数组映射List<Model>
	 * @param modelClass
	 * @return
	 */
	public <T extends BaseModel<T>> List<T> getModels(Class<? extends T> modelClass){
		String modelName = modelClass.getSimpleName();
		String prefix = StrKit.firstCharToLowerCase(modelName) + "List";
		return getModels(modelClass, prefix, false);
	}

	/**
	 * 表单数组映射List<Model>
	 * @param modelClass
	 * @param skipConvertError
	 * @return
	 */
	public <T extends BaseModel<T>> List<T> getModels(Class<? extends T> modelClass, boolean skipConvertError){
		String modelName = modelClass.getSimpleName();
		String prefix = StrKit.firstCharToLowerCase(modelName) + "List";
		return getModels(modelClass, prefix, skipConvertError);
	}
	
	/**
	 * 表单数组映射List<Model>
	 * @param modelClass
	 * @param prefix
	 * @return
	 */
	public <T extends BaseModel<T>> List<T> getModels(Class<? extends T> modelClass, String prefix){
		return getModels(modelClass, prefix, false);
	}
	
	/**
	 * 表单数组映射List<Model>
	 * @param modelClass
	 * @param prefix
	 * 
	 * 描述：
	 * 		
	 * 		表单	
	 *		<input type="hidden" name="groupList[0].ids" value="111"/>
	 *		<input type="text" name="groupList[0].names" value="222"/>
	 *		
	 *		<input type="hidden" name="groupList[1].ids" value="333"/>
	 *		<input type="text" name="groupList[1].names" value="444"/>
	 *		
	 *		<input type="hidden" name="groupList[3].ids" value="555"/>
	 *		<input type="text" name="groupList[3].names" value="666"/>
	 * 
	 * 		调用方法 
	 * 		List<Group> groupList = ToolModelInjector.injectModels(getRequest(), Group.class, "groupList");
	 * 		
	 * 		// 默认的prefix是Model类的首字母小写加上List
	 * 		List<Group> groupList = ToolModelInjector.injectModels(getRequest(), Group.class); 
	 */
	public <T extends BaseModel<T>> List<T> getModels(Class<? extends T> modelClass, String prefix, boolean skipConvertError){
		int maxIndex = 0;	// 最大的数组索引
		boolean zeroIndex = false; // 是否存在0索引
		
		String arrayPrefix = prefix + "[";
		String key = null;
		Enumeration<String> names = getRequest().getParameterNames();
		while (names.hasMoreElements()) {
			key = names.nextElement();
			if (key.startsWith(arrayPrefix) && key.indexOf("]") != -1) {
				int indexTemp = Integer.parseInt(key.substring(key.indexOf("[") + 1, key.indexOf("]")));
				
				if(indexTemp == 0){
					zeroIndex = true; // 是否存在0索引
				} 
				
				if(indexTemp > maxIndex){
					maxIndex = indexTemp; // 找到最大的数组索引
				}
			}
		}
		
		List<T> modelList = new ArrayList<T>();
		for (int i = 0; i <= maxIndex; i++) {
			if((i == 0 && zeroIndex) || i != 0){ // 避免表单空值时调用产生一个无用的值
				T baseModel = (T) getModel(modelClass, prefix + "[" + i + "]", skipConvertError);
				modelList.add(baseModel);
			}
		}
		
		return modelList;
	}
	
	/**
	 * 表单数组映射List<Bean>
	 * @param beanClass
	 * @return
	 */
	public <T> List<T> getBeans(Class<T> beanClass){
		String modelName = beanClass.getSimpleName();
		String prefix = StrKit.firstCharToLowerCase(modelName) + "List";
		return getBeans(beanClass, prefix, false);
	}

	/**
	 * 表单数组映射List<Bean>
	 * @param beanClass
	 * @param skipConvertError
	 * @return
	 */
	public <T> List<T> getBeans(Class<T> beanClass, boolean skipConvertError){
		String modelName = beanClass.getSimpleName();
		String prefix = StrKit.firstCharToLowerCase(modelName) + "List";
		return getBeans(beanClass, prefix, skipConvertError);
	}
	
	/**
	 * 表单数组映射List<Bean>
	 * @param beanClass
	 * @param prefix
	 * @return
	 */
	public <T> List<T> getBeans(Class<T> beanClass, String prefix){
		return getBeans(beanClass, prefix, false);
	}
	
	/**
	 * 表单数组映射List<Bean>
	 * @param beanClass
	 * @param prefix
	 */
	public <T> List<T> getBeans(Class<T> beanClass, String prefix, boolean skipConvertError){
		int maxIndex = 0;	// 最大的数组索引
		boolean zeroIndex = false; // 是否存在0索引
		
		String arrayPrefix = prefix + "[";
		String key = null;
		Enumeration<String> names = getRequest().getParameterNames();
		while (names.hasMoreElements()) {
			key = names.nextElement();
			if (key.startsWith(arrayPrefix) && key.indexOf("]") != -1) {
				int indexTemp = Integer.parseInt(key.substring(key.indexOf("[") + 1, key.indexOf("]")));
				
				if(indexTemp == 0){
					zeroIndex = true; // 是否存在0索引
				} 
				
				if(indexTemp > maxIndex){
					maxIndex = indexTemp; // 找到最大的数组索引
				}
			}
		}
		
		List<T> beanList = new ArrayList<T>();
		for (int i = 0; i <= maxIndex; i++) {
			if((i == 0 && zeroIndex) || i != 0){ // 避免表单空值时调用产生一个无用的值
				T baseModel = (T) getBean(beanClass, prefix + "[" + i + "]", skipConvertError);
				beanList.add(baseModel);
			}
		}
		
		return beanList;
	}

	/**
	 * 表单数组映射List<Model>
	 * @param modelClass
	 * @return
	 */
	public <T extends BaseModel<T>> Controller keepModels(Class<? extends T> modelClass) {
		String modelName = StrKit.firstCharToLowerCase(modelClass.getSimpleName());
		return keepModels(modelClass, modelName, false);
	}
	
	/**
	 * 表单数组映射List<Model>
	 * @param modelClass
	 * @param skipConvertError
	 * @return
	 */
	public <T extends BaseModel<T>> Controller keepModels(Class<? extends T> modelClass, boolean skipConvertError){
		String modelName = StrKit.firstCharToLowerCase(modelClass.getSimpleName());
		return keepModels(modelClass, modelName, skipConvertError);
	}

	/**
	 * 表单数组映射List<Model>
	 * @param modelClass
	 * @param modelName
	 * @return
	 */
	public <T extends BaseModel<T>> Controller keepModels(Class<? extends T> modelClass, String modelName) {
		return keepModels(modelClass, modelName, false);
	}
	
	/**
	 * 表单数组映射List<Model>
	 * @param modelClass
	 * @param modelName
	 * @param skipConvertError
	 * @return
	 */
	public <T extends BaseModel<T>> Controller keepModels(Class<? extends T> modelClass, String modelName, boolean skipConvertError) {
		if (StrKit.notBlank(modelName)) {
			List<T> model = getModels(modelClass, modelName, skipConvertError);
			setAttr(modelName, model);
		} else {
			keepPara();
		}
		return this;
	}
	
	/**
	 * 表单数组映射List<Bean>
	 * @param beanClass
	 * @return
	 */
	public <T> Controller keepBeans(Class<T> beanClass) {
		String modelName = StrKit.firstCharToLowerCase(beanClass.getSimpleName());
		return keepBeans(beanClass, modelName, false);
	}
	
	/**
	 * 表单数组映射List<Bean>
	 * @param beanClass
	 * @param skipConvertError
	 * @return
	 */
	public <T> Controller keepBeans(Class<T> beanClass, boolean skipConvertError){
		String modelName = StrKit.firstCharToLowerCase(beanClass.getSimpleName());
		return keepBeans(beanClass, modelName, skipConvertError);
	}

	/**
	 * 表单数组映射List<Bean>
	 * @param beanClass
	 * @param beanName
	 * @return
	 */
	public <T> Controller keepBeans(Class<T> beanClass, String beanName) {
		return keepBeans(beanClass, beanName, false);
	}
	
	/**
	 * 表单数组映射List<Bean>
	 * @param beanClass
	 * @param beanName
	 * @param skipConvertError
	 * @return
	 */
	public <T> Controller keepBeans(Class<T> beanClass, String beanName, boolean skipConvertError) {
		if (StrKit.notBlank(beanName)) {
			List<T> model = getBeans(beanClass, beanName, skipConvertError);
			setAttr(beanName, model);
		} else {
			keepPara();
		}
		return this;
	}
	
	/**
	 * 获取checkbox值，数组
	 * @param name
	 * @return
	 */
	protected String[] getParas(String name) {
		return getParaValues(name);
	}

	/**************************************************************************/
	/***
	 * tab页签，iframe方式的统一处理方式
	 */
	@Override
	public void render(String view){
		String action = getPara("action","");
		SysCustomSetting setting = getAttr("setting");
		if(setting==null){
			setting = (SysCustomSetting)this.getSession().getAttribute("setting");
		}
		setAttr("setting",setting);
//		if(getAttr("uorgid")!=null&&!getAttr("uorgid").equals("")) {
//
//			SimpleDateFormat sdf  =new SimpleDateFormat("yyyy-MM-dd");
//			OaNotices oanotices = OaNotices.dao.findFirst("select  * from  oa_notices 	where  	sfpublish = '1' and publishdatetime !='' 	and showtime >= '"+sdf.format(new Date())+"' and departments LIKE '%"+ShiroKit.getUserOrgId()+"%' ORDER BY 	publishdatetime DESC");
//			this.getSession().setAttribute("oanotices", oanotices);
//
//			setAttr("oanotices", oanotices);
//		}
		if("openPage".equals(action)){//打开新页面
			String s = this.getViewPath();
			if(view.indexOf("/")==0){
				setAttr("iframeRenderUrl", view);
			}else{
				setAttr("iframeRenderUrl", s+view);
			}
			super.render("/common/include/iframe.html");
		}else{
			super.render(view);
		}
	}

	public void renderIframe(String view){
		SysCustomSetting setting = getAttr("setting");
		if(setting==null){
			setting = (SysCustomSetting)this.getSession().getAttribute("setting");
		}
		String userId = ShiroKit.getUserId();//用户主键
		SysUser user = SysUser.dao.getById(userId);//用户对象
		setAttr("username",user.getUsername());
		setAttr("name",user.getName());

		if(view.indexOf("selectManyUser.html")==-1&& view.indexOf("selectManyUser1.html")==-1 && view.indexOf("selectManyOrg.html")==-1 &&
				view.indexOf("homeShowNotices.html")==-1&&view.indexOf("businessUploadList.html")==-1
				&& view.indexOf("selectOneUser.html")==-1&&view.indexOf("selectOneDctGroup.html")==-1
				&&view.indexOf("giveAuth.html")==-1
		) {
			setAttr("mlist",this.getSession().getAttribute("mlist"));
			setAttr("oanotices", this.getSession().getAttribute("oanotices"));
		}else {
			setAttr("mlist","");
			setAttr("oanotices", "");
		}




		setAttr("setting",setting);
		String s = this.getViewPath();
		if(view.indexOf("/")==0){
			setAttr("iframeRenderUrl", view);
		}else{
			setAttr("iframeRenderUrl", s+view);
		}


		/*OaTypes findFirst = OaTypes.dao.findFirst("select  * from oa_types where  status='1'");
		String oatypes=findFirst.getType();*/
		String oatypes="3";

		if(oatypes.equals("3")||view.indexOf("roleUser.html")!=-1||view.indexOf("selectOneUser.html")!=-1||view.indexOf("giveAuth.html")!=-1) {
			super.render("/common/include/iframe.html");
		} else {


//			List<OaBumphUser> find = OaBumphUser.dao.find("select  * from  oa_bumph_user where  lookornot ='1' and looked='0' and  username='"+ShiroKit.getUsername()+"'");

//			setAttr("bumpcount",find==null?0: find.size());
//
//
//			List<Record> hotlineList = Db.find("SELECT * FROM oa_hotline o LEFT JOIN oa_hotline_user hu ON o.id = hu.hotlinid"
//					+ " WHERE 1 = 1 AND hu.userid = '"+ShiroKit.getUserId()+"' AND hu.ifshow = '1' And hu.ifcomplete='0'");
//
//			setAttr("hotlineCount", hotlineList==null?0:hotlineList.size());

//			List<Record> sendDocList = Db.find("SELECT * FROM oa_senddoc o  WHERE 	1 = 1  and  o.cuserid != '"+ShiroKit.getUserId()+"' "
//					+ " and (  o.dofficesure ='"+ShiroKit.getUserOrgId()+"' or o.dhgksids = '"+ShiroKit.getUserOrgId()+"' )");
//
//			setAttr("senddocCount", sendDocList==null?0:sendDocList.size());

//
//			int todonum=(find==null?0: find.size())
//					+(hotlineList==null?0:hotlineList.size())+(sendDocList==null?0:sendDocList.size());
//			setAttr("todonum", todonum);



			if(oatypes.equals("2")) {

				if(view.endsWith("personalhome.html")) {
					setAttr("ifHome", true);
				}else {
					setAttr("ifHome", false);
				}
				super.render("/common/include/syshomeV2.html");
				//super.render("/common/include/syshomeV3.html");
			}else {
				super.render("/common/include/syshome.html");
			}

		}
		// super.render("/common/include/iframe.html");
	}
	public void renderIframebak(String view){
		//String action = getPara("action","");
		SysCustomSetting setting = getAttr("setting");
		if(setting==null){
			setting = (SysCustomSetting)this.getSession().getAttribute("setting");
		}
		String userId = ShiroKit.getUserId();//用户主键
		SysUser user = SysUser.dao.getById(userId);//用户对象
		setAttr("username",user.getUsername());
		setAttr("name",user.getName());
		
		
		if(view.indexOf("selectManyUser.html")==-1&& view.indexOf("selectManyUser1.html")==-1 && view.indexOf("selectManyOrg.html")==-1 && 
				view.indexOf("homeShowNotices.html")==-1&&view.indexOf("businessUploadList.html")==-1
				&& view.indexOf("selectOneUser.html")==-1&&view.indexOf("selectOneDctGroup.html")==-1
				&&view.indexOf("giveAuth.html")==-1
				) {
			setAttr("mlist",this.getSession().getAttribute("mlist"));
			setAttr("oanotices", this.getSession().getAttribute("oanotices"));
		}else {
			setAttr("mlist","");
			setAttr("oanotices", "");
		}
		
		
		
		
		setAttr("setting",setting);
		String s = this.getViewPath();
		if(view.indexOf("/")==0){
			setAttr("iframeRenderUrl", view);
		}else{
			setAttr("iframeRenderUrl", s+view);
		}
		if(ShiroKit.getUsername().equals("moa")||ShiroKit.getUsername().equals("tdog")||view.indexOf("roleUser.html")!=-1||view.indexOf("selectOneUser.html")!=-1||view.indexOf("giveAuth.html")!=-1) {
			super.render("/common/include/iframe.html");
		} else {
			
			
			List<OaBumphUser> find = OaBumphUser.dao.find("select  * from  oa_bumph_user where  lookornot ='1' and looked='0' and  username='"+ShiroKit.getUsername()+"'");
			
			setAttr("bumpcount",find==null?0: find.size());
			
			/*	List<OaEmail> emailList = OaEmail.dao.find("select  * from oa_email  where  suserid like '%"+ShiroKit.getUserId()+"%' and  opstatis='0'");
	    	setAttr("emailcount", emailList==null?0:emailList.size());
			 */
			List<Record> hotlineList = Db.find("SELECT * FROM oa_hotline o LEFT JOIN oa_hotline_user hu ON o.id = hu.hotlinid"
					+ " WHERE 1 = 1 AND hu.userid = '"+ShiroKit.getUserId()+"' AND hu.ifshow = '1' And hu.ifcomplete='0'");
			
			setAttr("hotlineCount", hotlineList==null?0:hotlineList.size());
			
			List<Record> sendDocList = Db.find("SELECT * FROM oa_senddoc o  WHERE 	1 = 1  and  o.cuserid != '"+ShiroKit.getUserId()+"' "
					+ " and (  o.dofficesure ='"+ShiroKit.getUserOrgId()+"' or o.dhgksids = '"+ShiroKit.getUserOrgId()+"' )");
			
			setAttr("senddocCount", sendDocList==null?0:sendDocList.size());
			
			
			int todonum=(find==null?0: find.size())/*+(emailList==null?0:emailList.size())*/
					+(hotlineList==null?0:hotlineList.size())+(sendDocList==null?0:sendDocList.size());
			setAttr("todonum", todonum);
			
			
			//收文
			String receiveSql = " select * from oa_bumph o  left join  oa_steps s on o.id=s.oid  WHERE  s.ifcomplete='0' ";
			receiveSql = receiveSql + " and s.userid='"+ShiroKit.getUserId()+"'";
			int receiveSize = Db.find(receiveSql).size();
			setAttr("receiveSize",receiveSize);
			//发文
			String sendSql = " select * from oa_senddoc o  left join oa_steps s on o.id=s.oid where  s.userid ='"+ShiroKit.getUserId()+"' and  s.ifcomplete='0'";
			int sendSize = Db.find(sendSql).size();
			setAttr("sendSize",sendSize);
			//邮件
			String emailSql = "select  * from oa_email  where  suserid like '%"+ShiroKit.getUserId()+"%'  and opstatis='0'";
			int emailSize = Db.find(emailSql).size();
			setAttr("emailSize",emailSize);
			
			//热线
			String hotSql = "select  * from oa_hotline  o  left join  oa_steps s on o.id=s.oid   where  s.userid='"+ShiroKit.getUserId()+"' and s.ifcomplete='0'";
			int hotSize = Db.find(hotSql).size();
			setAttr("hotSize",hotSize);
			//信访
			String letterSql = "select  * from oa_letter o left join  oa_steps s on o.id=s.oid   where  s.userid='"+ShiroKit.getUserId()+"' and s.ifcomplete='0'";
			int letterSize = Db.find(letterSql).size();
			setAttr("letterSize",letterSize);
			if(ShiroKit.getUsername().equals("tdog1")) {
				
				if(view.endsWith("personalhome.html")) {
					setAttr("ifHome", true);
				}else {
					setAttr("ifHome", false);
				}
				super.render("/common/include/syshomeV2.html");
				//super.render("/common/include/syshomeV3.html");
			}else {
				super.render("/common/include/syshome.html");
			}
			
		}
		// super.render("/common/include/iframe.html");
	}
	public void renderIframe(String view,String param){
		//String action = getPara("action","");
		SysCustomSetting setting = getAttr("setting");
		if(setting==null){
			setting = (SysCustomSetting)this.getSession().getAttribute("setting");
		}
		String userId = ShiroKit.getUserId();//用户主键
		SysUser user = SysUser.dao.getById(userId);//用户对象
		System.out.println(user.getName());
		setAttr("username",user.getUsername());
		setAttr("name",user.getName());
		
		
		if(view.indexOf("selectManyUser.html")==-1&&view.indexOf("selectManyUser1.html")==-1 && view.indexOf("selectManyOrg.html")==-1 && 
				view.indexOf("homeShowNotices.html")==-1&&view.indexOf("businessUploadList.html")==-1
				&& view.indexOf("selectOneUser.html")==-1&&view.indexOf("selectOneDctGroup.html")==-1
				&&view.indexOf("giveAuth.html")==-1
				) {
			setAttr("mlist",this.getSession().getAttribute("mlist"));
			setAttr("oanotices", this.getSession().getAttribute("oanotices"));
		}else {
			setAttr("mlist","");
			setAttr("oanotices", "");
		}
		
		
		
		
		setAttr("setting",setting);
		String s = this.getViewPath();
		if(view.indexOf("/")==0){
			setAttr("iframeRenderUrl", view);
		}else{
			setAttr("iframeRenderUrl", s+view);
		}
		if(ShiroKit.getUsername().equals("moa")||view.indexOf("roleUser.html")!=-1||view.indexOf("selectOneUser.html")!=-1||view.indexOf("giveAuth.html")!=-1) {
			super.render("/common/include/iframe.html");
		} else {
			
			
			List<OaBumphUser> find = OaBumphUser.dao.find("select  * from  oa_bumph_user where  lookornot ='1' and looked='0' and  username='"+ShiroKit.getUsername()+"'");
			
			setAttr("bumpcount",find==null?0: find.size());
			
			/*	List<OaEmail> emailList = OaEmail.dao.find("select  * from oa_email  where  suserid like '%"+ShiroKit.getUserId()+"%' and  opstatis='0'");
	    	setAttr("emailcount", emailList==null?0:emailList.size());
			 */
			List<Record> hotlineList = Db.find("SELECT * FROM oa_hotline o LEFT JOIN oa_hotline_user hu ON o.id = hu.hotlinid"
					+ " WHERE 1 = 1 AND hu.userid = '"+ShiroKit.getUserId()+"' AND hu.ifshow = '1' And hu.ifcomplete='0'");
			
			setAttr("hotlineCount", hotlineList==null?0:hotlineList.size());
			
			List<Record> sendDocList = Db.find("SELECT * FROM oa_senddoc o  WHERE 	1 = 1  and  o.cuserid != '"+ShiroKit.getUserId()+"' "
					+ " and (  o.dofficesure ='"+ShiroKit.getUserOrgId()+"' or o.dhgksids = '"+ShiroKit.getUserOrgId()+"' )");
			
			setAttr("senddocCount", sendDocList==null?0:sendDocList.size());
			
			
			int todonum=(find==null?0: find.size())/*+(emailList==null?0:emailList.size())*/
					+(hotlineList==null?0:hotlineList.size())+(sendDocList==null?0:sendDocList.size());
			setAttr("todonum", todonum);
			
			
			//收文
	/*		String receiveSql = " select * from oa_bumph o  left join  oa_bumph_user bu on o.id=bu.bumphid  WHERE  bu.ifcomplete='0' ";
			receiveSql = receiveSql + " AND bu.userid='"+ShiroKit.getUserId()+"'";
			int receiveSize = Db.find(receiveSql).size();
			setAttr("receiveSize",receiveSize);*/
			//发文
			String sendSql = " select * from oa_senddoc o  left join oa_senddoc_step os on o.id=os.sdid where  os.actorsid ='"+ShiroKit.getUserId()+"' and  os.ifcomplete='0'";
			int sendSize = Db.find(sendSql).size();
			setAttr("sendSize",sendSize);
			//邮件
			String emailSql = "select  * from oa_email  where  suserid like '%"+ShiroKit.getUserId()+"%'  and opstatis='0'";
			int emailSize = Db.find(emailSql).size();
			setAttr("emailSize",emailSize);
			
			//热线
			String hotSql = "select  * from oa_hotline  o  left join  oa_steps s on o.id=s.oid   where  s.userid='"+ShiroKit.getUserId()+"' and s.ifcomplete='0'";
			int hotSize = Db.find(hotSql).size();
			setAttr("hotSize",hotSize);
			//信访
			String letterSql = "select  * from oa_letter o left join  oa_letter_user hu on o.id=hu.hotlinid   where  hu.userid='"+ShiroKit.getUserId()+"' and hu.ifcomplete='0'";
			int letterSize = Db.find(letterSql).size();
			setAttr("letterSize",letterSize);
		 
			super.render("/common/include/syshomeV2.html");
		}
		// super.render("/common/include/iframe.html");
	}
	public void renderIframeOpen(String view){
		//String action = getPara("action","");
		SysCustomSetting setting = getAttr("setting");
		if(setting==null){
			setting = (SysCustomSetting)this.getSession().getAttribute("setting");
		}
		String userId = ShiroKit.getUserId();//用户主键
		SysUser user = SysUser.dao.getById(userId);//用户对象
		System.out.println(user.getName());
		setAttr("username",user.getUsername());
		/*	if(view.indexOf("homePage.html")==-1) {
			setAttr("showHead", "1");
		}else {
			setAttr("showHead", "0");
		}*/
		
		if(view.indexOf("selectManyUser.html")==-1 && view.indexOf("selectManyUser1.html")==-1 && view.indexOf("selectManyOrg.html")==-1 && 
				view.indexOf("homeShowNotices.html")==-1&&view.indexOf("businessUploadList.html")==-1
				&& view.indexOf("selectOneUser.html")==-1&&view.indexOf("selectOneDctGroup.html")==-1
				&&view.indexOf("selectOneOrg.html")==-1&&view.indexOf("selectOneOrg.html")==-1
				) {
			setAttr("mlist",this.getSession().getAttribute("mlist"));
			setAttr("oanotices", this.getSession().getAttribute("oanotices"));
		}else {
			setAttr("mlist","");
			setAttr("oanotices", "");
		}
		
		
		/*else {
			setAttr("mlist","");
		}*/
		
		setAttr("setting",setting);
		String s = this.getViewPath();
		if(view.indexOf("/")==0){
			setAttr("iframeRenderUrl", view);
		}else{
			setAttr("iframeRenderUrl", s+view);
		}
	/*	if(ShiroKit.getUsername().equals("admin")) {
			super.render("/common/include/iframe.html");
		} else {
			super.render("/common/include/test.html");
		}*/
	  super.render("/common/include/iframe.html");
	}
}
