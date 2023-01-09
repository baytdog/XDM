
package com.pointlion.mvc.admin.sys.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pointlion.mvc.common.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowIdentityService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;

/***
 * 用户管理控制器
 * @author Administrator
 *
 */

public class UserController extends BaseController {
	public static final WorkFlowIdentityService idService = WorkFlowIdentityService.me;
	/***
	 * 获取管理首页
	 */
	public void getListPage(){
		//setAttr("orgid", ShiroKit.getUserOrgId());
    	renderIframe("list.html");
    }
	
	
	public void getListUser(){
		setAttr("orgid", ShiroKit.getUserOrgId());
    	renderIframe("orgUserList.html");
    }
	
	
	
	
	/***
     * 获取分页数据
     **/
    public void listData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String orgid = getPara("orgid");
    	String usernameSearch = getPara("usernameSearch","");
    	String nameSearch = getPara("nameSearch","");
    	if(StrKit.notBlank(usernameSearch)||StrKit.notBlank(nameSearch)){
    		orgid="";
    	}
    	Page<Record> page = SysUser.dao.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),orgid,usernameSearch,nameSearch);
    	List<Record> pageList = page.getList();
    	for(Record r : pageList){
    		List<SysRole> list = SysRole.dao.getAllRoleByUserid(r.getStr("id"));
    		List<String> nameList = new ArrayList<String>();
    		for(SysRole role : list){
    			nameList.add(role.getName());
    		}
    		r.set("userRoleName", StringUtils.join(nameList, ","));
    	}
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    /***
     * 根据角色获取用户
     */
    public void getListDataByRoleid(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String roleid = getPara("roleid");
    	if(StrKit.isBlank(roleid)){
    		renderPage(null,"" ,0);
    	}else{
    		Page<Record> page = SysUser.dao.getPageByRoleid(Integer.valueOf(curr),Integer.valueOf(pageSize),roleid);
    		renderPage(page.getList(),"" ,page.getTotalRow());
    	}
    }
    
    
    public void pwdJM() {
    	PasswordService svc = new DefaultPasswordService();
    	
    	
   
    	
    	
    }
    
    
    /***
     * 保存
     */
    public void save(){
    	SysUser newUser = getModel(SysUser.class);
    	String userRoleId = getPara("userRoleId","");
    	if(StrKit.notBlank(newUser.getId())){
    		String password = newUser.getPassword();
    		SysUser old = SysUser.dao.findById(newUser.getId());
    		if(StrKit.isBlank(password)){//如果没传递密码
    			password = old.getPassword();//获取原始密码
    			newUser.setPassword(password);//设置为原始密码
    		}else{//传递了密码 , 设置新密码
    			PasswordService svc = new DefaultPasswordService();
    			newUser.setPassword(svc.encryptPassword(password));//加密新密码
    		}
    		newUser.update();
    		if(StrKit.notBlank(userRoleId)){
				SysUser.dao.giveUserRole(newUser.getId(),userRoleId);
			}
    	     //this.redirect("/admin/login/logout");
    	        
    	       // this.redirect("/admin/login/logout");
    		
    		
    		
    		   Subject currentUser = SecurityUtils.getSubject();
    	        try {
    	            currentUser.logout();
    	            this.removeSessionAttr("user");
    	        } catch (SessionException ise) {
    	        } 
    	        
    	}else{
    		newUser.setId(UuidUtil.getUUID());
    		PasswordService svc = new DefaultPasswordService();
    	//	newUser.setPassword(svc.encryptPassword(newUser.getPassword()));//加密新密码
    		newUser.setPassword(svc.encryptPassword("000000"));//默认密码6个0
    		newUser.save();
			if(StrKit.notBlank(userRoleId)){
				SysUser.dao.giveUserRole(newUser.getId(),userRoleId);//这里面有添加用户，添加分组
			}
			
		  
    	}
    	 renderSuccess();
    }
    
    
    /**
     * 修改密码
     */
    public void updatePwd() {
    	
    	String password = getPara("password","");
    	SimpleUser loginUser = ShiroKit.getLoginUser();
    	
    	SysUser sysUser = SysUser.dao.findById(loginUser.getId());
  		PasswordService svc = new DefaultPasswordService();
  		sysUser.setPassword(svc.encryptPassword(password));
  		sysUser.update();
    	System.out.println(password);
    	renderSuccess();
    	
    	
    }
    
    
    
    /***
     * 获取编辑页面
     */
    public void getEditPage(){
    	//添加和修改
    	String id = getPara("id");
    	List<OaDict> positionList = OaDict.dao.find("select  * from  xd_dict where type='position' order by sortnum ");
    	setAttr("positionList", positionList);
    	if(StrKit.notBlank(id)){//修改
    		SysUser o = SysUser.dao.getById(id);
    		String orgid = o.getOrgid();
    		SysOrg org = SysOrg.dao.getById(orgid);
    		setAttr("org", org);
    		setAttr("o", o);
    		List<SysRole> list = SysRole.dao.getAllRoleByUserid(o.getId());
    		List<String> idList = new ArrayList<String>();
    		List<String> nameList = new ArrayList<String>();
    		for(SysRole role : list){
    			idList.add(role.getId());
    			nameList.add(role.getName());
    		}
    		setAttr("userRoleId", StringUtils.join(idList, ","));
    		setAttr("userRoleName", StringUtils.join(nameList, ","));
    	}else{
    		String orgid = getPara("orgid");
    		if(StrKit.notBlank(orgid)){
    			SysOrg org = SysOrg.dao.getById(orgid);
    			setAttr("org", org);
    		}
    	}
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);

		List<XdEmployee> emps = XdEmployee.dao.find("select * from  xd_employee");
		setAttr("emps",emps);
    	renderIframe("edit.html");
    }
    
    
    public void getUserInfo() {
    	
    	
    	//添加和修改
    	String id = ShiroKit.getUserId();
    	List<OaDict> positionList = OaDict.dao.find("select  * from  oa_dict where type='position' order by sortnum ");
    	setAttr("positionList", positionList);
    	if(StrKit.notBlank(id)){//修改
    		SysUser o = SysUser.dao.getById(id);
    		String orgid = o.getOrgid();
    		SysOrg org = SysOrg.dao.getById(orgid);
    		setAttr("org", org);
    		setAttr("o", o);
    		List<SysRole> list = SysRole.dao.getAllRoleByUserid(o.getId());
    		List<String> idList = new ArrayList<String>();
    		List<String> nameList = new ArrayList<String>();
    		for(SysRole role : list){
    			idList.add(role.getId());
    			nameList.add(role.getName());
    		}
    		setAttr("userRoleId", StringUtils.join(idList, ","));
    		setAttr("userRoleName", StringUtils.join(nameList, ","));
    	}else{
    		String orgid = getPara("orgid");
    		if(StrKit.notBlank(orgid)){
    			SysOrg org = SysOrg.dao.getById(orgid);
    			setAttr("org", org);
    		}
    	}
    	
    	
    	renderIframe("pInfo.html");
    	
    	
    }
    
    
    
    
    public void userSetting(){

    	String id = getPara("id");
    	if(StrKit.notBlank(id)){//修改
    		SysUser o = SysUser.dao.getById(id);
    		String orgid = o.getOrgid();
    		SysOrg org = SysOrg.dao.getById(orgid);
    		setAttr("org", org);
    		setAttr("o", o);
    		List<SysRole> list = SysRole.dao.getAllRoleByUserid(o.getId());
    		List<String> idList = new ArrayList<String>();
    		List<String> nameList = new ArrayList<String>();
    		for(SysRole role : list){
    			idList.add(role.getId());
    			nameList.add(role.getName());
    		}
    		setAttr("userRoleId", StringUtils.join(idList, ","));
    		setAttr("userRoleName", StringUtils.join(nameList, ","));
    	}
    	renderIframe("userSetting.html");
    }
    
    /***
     * 验证用户 , 是否被注册
     */
    public void validUsername(){
    	SysUser user = getModel(SysUser.class);
    	if(user!=null){
    		String username = user.getUsername();
    		if(StrKit.notBlank(username)){
    			SysUser o =  SysUser.dao.getByUsername(username);
    			if(o==null){//用户不存在 
    				renderValidSuccess();
    			}else{
    				renderValidFail();
    			}
    		}else{
    			renderValidSuccess();
    		}
    	}else{
    		renderValidSuccess();
    	}
    }
    
    /***
     * 给用户赋值角色
     */
    public void getGiveUserRolePage(){
    	String userid = getPara("userid");
    	setAttr("userid", userid);
    	renderIframe("giveUserRole.html");
    }
    /***
     * 给用户赋值角色
     */
    public void giveUserRole(){
    	String userid = getPara("userid");
    	String data = getPara("data");
    	SysUser.dao.giveUserRole(userid,data);
    	renderSuccess();
    }
    /***
     * 删除
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
    	//执行删除
		SysUser.dao.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
    
    /***
     * 打开选择多个人员的页面
     */
    public void openSelectManyUserPage(){
    	String orgid = getPara("orgid");
    	String oldData = getPara("oldData");
    	setAttr("selectedId", "");
		setAttr("selectedName", "");
    	if(StrKit.notBlank(oldData)){
    		String[] old = oldData.split(",");
    		List<String> username = new ArrayList<String>();
    		List<String> name = new ArrayList<String>();
    		List<String> org = new ArrayList<String>();
    		for(String userid:old){
    			SysUser user = SysUser.dao.getById(userid);
    			if(user!=null){
    				username.add(user.getUsername());
    				name.add(user.getName());
    				org.add(SysOrg.dao.getOrgNameById(user.getOrgid()));
    			}
    		}
			setAttr("selectedId", oldData);
			setAttr("selectedUsername", StringUtils.join(username,","));
			setAttr("selectedName", StringUtils.join(name,","));
			setAttr("selectedOrgName", StringUtils.join(org,","));
    	}
    	setAttr("orgid", orgid);
    	renderIframeOpen("selectManyUser.html");
    }
    public void openSelectManyUserPage1(){
    	String orgid = getPara("orgid");
    	String oldData = getPara("oldData");
    	setAttr("selectedId", "");
    	setAttr("selectedName", "");
    	if(StrKit.notBlank(oldData)){
    		String[] old = oldData.split(",");
    		List<String> username = new ArrayList<String>();
    		List<String> name = new ArrayList<String>();
    		List<String> org = new ArrayList<String>();
    		for(String userid:old){
    			SysUser user = SysUser.dao.getById(userid);
    			if(user!=null){
    				username.add(user.getUsername());
    				name.add(user.getName());
    				org.add(SysOrg.dao.getOrgNameById(user.getOrgid()));
    			}
    		}
    		setAttr("selectedId", oldData);
    		setAttr("selectedUsername", StringUtils.join(username,","));
    		setAttr("selectedName", StringUtils.join(name,","));
    		setAttr("selectedOrgName", StringUtils.join(org,","));
    	}
    	setAttr("orgid", orgid);
    	renderIframeOpen("selectManyUser1.html");
    }
    /***
     * 打开选择单个人员的页面
     */
    public void openSelectOneUserPage(){
    	String orgid = getPara("orgid");
    	setAttr("orgid", orgid);
    	renderIframe("selectOneUser.html");
    }
    /***
     * 使用角色打开选择人员的页面
     */
    public void openSelectOneUserUseRolePage(){
    	String roleKey = getPara("roleKey");
    	if(StrKit.notBlank(roleKey)){
    		String firstRoleKey = roleKey.split(",")[0];
    		SysRole firstRole = SysRole.dao.getRoleByRoleKey(firstRoleKey);
    		if(firstRole!=null){
    			setAttr("firstRoleId", firstRole.getId());
    		}
    	}
    	setAttr("roleKey", roleKey);
    	renderIframe("selectOneUserUseRole.html");
    }

    /**
     * 导入老数据更新密码
     */
    public void updateOPwd() {
   List<SysUser> find = SysUser.dao.find("select * from  sys_user  where username not in ('lion','moa','ceshi')");
    	System.out.println(find.size()+"==========");
    	
     Map<String, String> orgMap = new HashMap<String, String>();
     orgMap.put("4", "ef7e017b3e5642f58304d230ecbad672");
     orgMap.put("2", "67eefe00c16b44a8b2c516cdc92d2029");
     orgMap.put("1", "062cdfa99ef44813a9075b6d0b9c5c2c");
     orgMap.put("3", "e6e2df8e82514eecbe5546bce923f4c2");
     orgMap.put("5", "7606e431d41843ae8b27538a3c500263");
     orgMap.put("6", "83feba0018c0461dbf89365ed428ce0b");
     orgMap.put("10", "2adbd2f5401f46d2ab3280872bdfbcaa");
     orgMap.put("9", "68368847c64c4128adfbaff87afa2542");
     
     Map<String, String> positionMap = new HashMap<String, String>();
     
     positionMap.put("7", "ccf271bef0d14b44893ce90a7c9df3cb");
     positionMap.put("8", "c8bae2a5e3074555a48e6a03ed07ed78");
     positionMap.put("01", "e196ac99ee594c81ab5007cd9296aad9");
     positionMap.put("02", "70ff0663450940d8800291746a878ab4");
     positionMap.put("9", "4000a101cb684d17b08af9997ba36698");
     positionMap.put("10", "2db30f821cbd497fbc7904a024ccf342");
     positionMap.put("2", "684f8d02e22b42fda8e04da10cb14a9a");
     positionMap.put("6", "ac7fac1cfed54a09b5c0d93e484b3246");
     positionMap.put("3", "66b23e929e1340f58c85036e5d0bc6a0");
    	
     PasswordService svc = new DefaultPasswordService();
    	for (SysUser sysUser : find) {
    		
    		String pwd="NULL";
    		String orgid="";
    		String position="";
    		if(sysUser.getPassword().equals("NULL")) {
    			System.out.println(sysUser.getName());
    		}else {
    			System.out.println(sysUser.getPassword());
    			pwd=svc.encryptPassword(sysUser.getPassword());
    		} 
    		String[] split = sysUser.getOrgid().split(",");
    		
    		for (int i = 0; i < split.length; i++) {
    			
    			orgid=orgMap.get(split[i]);
    			if(i!=split.length-1) {
    				orgid=orgid+",";
    			}
				
			}
    		position=positionMap.get(sysUser.getPosition());
    		
    		
    		sysUser.setPassword(pwd);
    		sysUser.setOrgid(orgid);
    		sysUser.setPosition(position);
    		
    		
    		System.out.println("===================");
    		System.out.println( sysUser.getName());
    		System.out.println( sysUser.getOrgid());
    		System.out.println( sysUser.getPassword());
    		System.out.println( sysUser.getPosition());
    		
    		System.out.println("===================");
	  		sysUser.update();
		}
    	renderSuccess();
    }
    
    
    
    
}
