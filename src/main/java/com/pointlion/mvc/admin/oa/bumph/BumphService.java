package com.pointlion.mvc.admin.oa.bumph;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.OaBumph;
import com.pointlion.mvc.common.model.OaBumphOrg;
import com.pointlion.mvc.common.model.OaBumphRz;
import com.pointlion.mvc.common.model.OaBumphUser;
import com.pointlion.mvc.common.model.OaSteps;
import com.pointlion.mvc.common.model.OaStepsRz;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.VTasklist;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.ListUtil;
import com.pointlion.mvc.common.utils.NodesUtils;
import com.pointlion.mvc.common.utils.StepUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.office.word.WordExportUtil;
import com.pointlion.plugin.shiro.ShiroKit;

public class BumphService {
	public static final BumphService me = new BumphService();
	public static final WorkFlowService workFlowService = new WorkFlowService();
	
	public static  final String  typeStep="1";
	/***
	 * 保存 
	 * @param bumph
	 * @param fidstr
	 * @param secondStr
	 */
	@Before(Tx.class)
	public String save(OaBumph bumph,String fidstr ,String secondStr){
		OaBumphOrg.dao.deleteOrgByBumphId(bumph.getId());//先清空掉所有单位
		if(StrKit.notBlank(bumph.getId())){
    		bumph.update();//更新公文
    		saveOrg("1",bumph.getId(),fidstr);//更新主送单位
    		saveOrg("0",bumph.getId(),secondStr);//更新抄送单位
    		return bumph.getStr("id");
    	}else{
    		String id = UuidUtil.getUUID();
    		bumph.setId(id);
    		bumph.setDocNumSource("沪交行收文");
    		bumph.setDocNum(bumph.getDocNumSource()+"("+bumph.getDocType()+")"+bumph.getDocNumYear()+"第 ("+bumph.getDocNumN()+")"+"号");
    		bumph.setCreateTime(DateUtil.getCurrentTime());
    		bumph.setCreateOrgId(ShiroKit.getUserOrgId());
        	bumph.setCreateUserId(ShiroKit.getUserId());
        	bumph.setCreateOrgName(ShiroKit.getUserOrgName());
        	bumph.setCreateUserName(ShiroKit.getUsername());
        	bumph.setCreateTime(DateUtil.getCurrentTime());
    		bumph.save();//保存公文
    		saveOrg("1",bumph.getId(),fidstr);//更新主送单位
    		saveOrg("0",bumph.getId(),secondStr);//更新抄送单位
    		saveBumphUsers("1", bumph.getId(), fidstr, bumph.getBackup1());
    		
    		saveBumphUsers("0", bumph.getId(), secondStr, bumph.getBackup1());
    		return id;
    	}
	}
	@Before(Tx.class)
	public String saveV1(OaBumph bumph,String fidstr ,String secondStr){
		OaBumphOrg.dao.deleteOrgByBumphId(bumph.getId());//先清空掉所有单位
		if(StrKit.notBlank(bumph.getId())){
			bumph.update();//更新公文
			saveOrg("1",bumph.getId(),fidstr);//更新主送单位
			saveOrg("0",bumph.getId(),secondStr);//更新抄送单位
			return bumph.getStr("id");
		}else{
			String id = UuidUtil.getUUID();
			bumph.setId(id);
			bumph.setDocNumSource("沪交行收文");
			bumph.setDocNum(bumph.getDocNumSource()+"("+bumph.getDocType()+")"+bumph.getDocNumYear()+"第 ("+bumph.getDocNumN()+")"+"号");
			bumph.setCreateTime(DateUtil.getCurrentTime());
			bumph.setCreateOrgId(ShiroKit.getUserOrgId());
			bumph.setCreateUserId(ShiroKit.getUserId());
			bumph.setCreateOrgName(ShiroKit.getUserOrgName());
			bumph.setCreateUserName(ShiroKit.getUsername());
			bumph.setCreateTime(DateUtil.getCurrentTime());
			bumph.save();//保存公文
			saveOrg("1",bumph.getId(),fidstr);//更新主送单位
			saveOrg("0",bumph.getId(),secondStr);//更新抄送单位
			if(bumph.getBackup2().equals("1")) {
				saveBumphUsers1("1", bumph.getId(), fidstr,secondStr, bumph.getBackup1());
			}
			//saveBumphUsersV1("0", bumph.getId(), secondStr, bumph.getBackup1());
			return id;
		}
	}
	@Before(Tx.class)
	public String saveV2(OaBumph bumph){
		OaBumphOrg.dao.deleteOrgByBumphId(bumph.getId());//先清空掉所有单位
		if(StrKit.notBlank(bumph.getId())){
			bumph.update();//更新公文
			/*saveOrg("1",bumph.getId(),fidstr);//更新主送单位
			saveOrg("0",bumph.getId(),secondStr);//更新抄送单位
*/			return bumph.getStr("id");
		}else{
			String id = UuidUtil.getUUID();
			bumph.setId(id);
			bumph.setDocNumSource("沪交行收文");
			bumph.setDocNum(bumph.getDocNumSource()+"("+bumph.getDocType()+")"+bumph.getDocNumYear()+"第 ("+bumph.getDocNumN()+")"+"号");
			bumph.setCreateTime(DateUtil.getCurrentTime());
			bumph.setCreateOrgId(ShiroKit.getUserOrgId());
			bumph.setCreateUserId(ShiroKit.getUserId());
			bumph.setCreateOrgName(ShiroKit.getUserOrgName());
			bumph.setCreateUserName(ShiroKit.getUsername());
			bumph.setCreateTime(DateUtil.getCurrentTime());
			bumph.save();//保存公文
			//saveOrg("1",bumph.getId(),fidstr);//更新主送单位
			//saveOrg("0",bumph.getId(),secondStr);//更新抄送单位
			if(bumph.getBackup2().equals("1")) {
				//saveBumphUsers1("1", bumph.getId(), fidstr,secondStr, bumph.getBackup1());
				saveBumpuUsersOnSave(bumph);
			}
			return id;
		}
	}
	
	/**
	 * 
	* @Title: saveV2 
	* @Description: 送拟办 
	* @param bumph
	* @return String
	* @date 2020年11月7日下午4:53:13
	 */
	
	@Before(Tx.class)
	public String saveNb(OaBumph bumph){
		//OaBumphOrg.dao.deleteOrgByBumphId(bumph.getId());//先清空掉所有单位
/*		if(StrKit.notBlank(bumph.getId())){
			bumph.update();//更新公文
	 		
			return bumph.getStr("id");
		}else{*/
			//String id = UuidUtil.getUUID();
			//bumph.setId(id);
			bumph.setDocNumSource("沪交行收文");
			bumph.setDocNum(bumph.getDocNumSource()+"("+bumph.getDocType()+")"+bumph.getDocNumYear()+"第 ("+bumph.getDocNumN()+")"+"号");
			bumph.setCreateTime(DateUtil.getCurrentTime());
			bumph.setCreateOrgId(ShiroKit.getUserOrgId());
			bumph.setCreateUserId(ShiroKit.getUserId());
			bumph.setCreateOrgName(ShiroKit.getUserOrgName());
			bumph.setCreateUserName(ShiroKit.getUsername());
			bumph.setCreateTime(DateUtil.getCurrentTime());
			bumph.setStatus("1");
			bumph.save();//保存公文
		 
			//if(bumph.getBackup2().equals("1")) {
				//saveBumpuUsersOnSave(bumph);
				 //OaBumphUser buser =new OaBumphUser();
				 OaSteps buser=new OaSteps();
				 buser.setId(UuidUtil.getUUID());
				 buser.setOid(bumph.getId());
				 buser.setType(typeStep);
				 buser.setTitle(bumph.getTitle());
				 buser.setUserid(ShiroKit.getUserId());
				 buser.setUsername(ShiroKit.getUserName());
				 SysOrg org = SysOrg.dao.findById(ShiroKit.getUserOrgId());
				 buser.setOrgid(ShiroKit.getUserOrgId());
				 buser.setOrgname(org.getName());
				 buser.setIfshow("1");//显示
				 buser.setIfcomplete("1");
				 buser.setCompletetime(DateUtil.getCurrentTime());
				 buser.setCtime(DateUtil.getCurrentTime());
				 buser.setCusername(ShiroKit.getUserName());
				 buser.setCuserid(ShiroKit.getUserId());
				 buser.setStep("1");
				 buser.setButtontype("1");
				 buser.save();
				 
				 StepUtil.insertStepHistory(bumph.getId(), bumph.getTitle(), typeStep);
				 
				 //nodestate 
				 
				 List<OaSteps> stepsList= new ArrayList<OaSteps>();
				 //nodeend
				 
				 
				 
				 
				 
				 String[] nbrids = bumph.getNbrids().split(",");
				 
				 for (String nbrid : nbrids) {
					 
					 SysUser user = SysUser.dao.findById(nbrid);
					 org = SysOrg.dao.findById(user.getOrgid());
					// OaBumphUser buserNb =new OaBumphUser();
					 OaSteps buserNb=new OaSteps();
					 buserNb.setId(UuidUtil.getUUID());
					 buserNb.setOid(bumph.getId());
					 buserNb.setType(typeStep);
					 buserNb.setTitle(bumph.getTitle());
					 buserNb.setUserid(nbrid);
					 buserNb.setUsername(user.getName());
					 buserNb.setOrgid(org.getId());
					 buserNb.setOrgname(org.getName());
					 buserNb.setIfshow("1");//显示
					//buser.setParentid(buserP.getId());
					 buserNb.setIfcomplete("0");
					 //buserNb.setCompletetime(DateUtil.getCurrentTime());
					 buserNb.setCtime(DateUtil.getCurrentTime());
					 buserNb.setCusername(ShiroKit.getUserName());
					 buserNb.setCuserid(ShiroKit.getUserId());
					 buserNb.setStep("2");
					 buserNb.setButtontype("1");
					 buserNb.save();
					//nodestate 
					 stepsList.add(buserNb);
					 //nodeend
				}
				 
				 
				//nodestate 
				 NodesUtils.insetNodes(buser, stepsList, bumph.getId());
				 //nodeend
			//}
		
		//}
			return bumph.getId();
	}
	
	
	
	
	/**
	 * 
	* @Title: saveBumpuUsersOnSave 
	* @Description: TODO 
	* @param o void
	* @author bkkco
	* @date 2020年10月29日下午2:48:38
	 */
	
	public void saveBumpuUsersOnSave(OaBumph o) {
		 OaBumphUser buser =new OaBumphUser();
		 buser.setId(UuidUtil.getUUID());
		 buser.setBumphid(o.getId());
		 buser.setUserid(ShiroKit.getUserId());
		 buser.setUsername(ShiroKit.getUsername());
		 buser.setOrgid(ShiroKit.getUserOrgId());
		 buser.setOrgname(ShiroKit.getUserOrgName());
		 buser.setLookornot("1");
		 buser.setSortnum(1);
		 buser.setLooked("0");
		 buser.setIfshow("1");//显示
		 buser.setIfcomplete("1");
		 buser.setCompletetime(DateUtil.getCurrentTime());
		 buser.setCreatetime(DateUtil.getCurrentTime());
		 buser.setCreateuser(ShiroKit.getUserName());
		 buser.setCreateuserid(ShiroKit.getUserId());
		 buser.setStep("1");
		 
		 buser.save();
		 
		 String[] nbrids = o.getNbrids().split(",");
		 
		 for (String nbrid : nbrids) {
			 
			 SysUser userNb = SysUser.dao.findById(nbrid);
			 SysOrg org = SysOrg.dao.findById(userNb.getOrgid());
			 
			 OaBumphUser buserNb =new OaBumphUser();
			 buserNb.setId(UuidUtil.getUUID());
			 buserNb.setBumphid(o.getId());
			 buserNb.setUserid(nbrid);
			 buserNb.setUsername(userNb.getUsername());
			 buserNb.setOrgid(org.getId());
			// SysOrg.dao.findById(user.getOrgid()).getName()
			 buserNb.setOrgname(org.getName());
			// buser.setUserposition(ShiroKit.getu);
			 buserNb.setLookornot("1");
			 //buser.setSortnum(String.valueOf(i));
			 buserNb.setSortnum(2);
			 buserNb.setLooked("0");
			 buserNb.setIfshow("1");//显示
			//buser.setParentid(buserP.getId());
			 buserNb.setIfcomplete("0");
			 buserNb.setCompletetime(DateUtil.getCurrentTime());
			 buserNb.setCreatetime(DateUtil.getCurrentTime());
			 buserNb.setCreateuser(ShiroKit.getUserName());
			 buserNb.setCreateuserid(ShiroKit.getUserId());
			 buserNb.setStep("2");
			 buserNb.save();
			 
			
		}
		 
	}
	
	
	/**
	 * 
	* @Title: saveBumphUsers 
	* @Description: 存储收文可查看相关人员  
	* @param type 类型 1 领导 0.部门
	* @param bumphId 收文ID
	* @param ids void 对应领导 或者部门的ID
	* @param bumphmodel void 公文模式
	 
	* @date 2020年9月3日下午9:53:18
	 */
	
	@Before(Tx.class)
	public void saveBumphUsers1(String type,String bumphid,String  ids,String orgids, String bumphmodel) {
		int  i=1;
		String[] idsA = ids.split(",");//领导职务对应的值
		String[] orgA = orgids.split(",");//部门对应的id
		
	//	if(type.equals("1")) {
		
			for (String id : idsA) {
				 
				//List<OaBumphUser> buserL = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
				 //List<SysUser> positionUsers = SysUser.dao.find("select  * from  sys_user where id='"+id+"'");
				 SysUser user = SysUser.dao.findById(id);
				// for ( SysUser  user: positionUsers) {
					 OaBumphUser buser =new OaBumphUser();
					 buser.setId(UuidUtil.getUUID());
					 buser.setBumphid(bumphid);
					 buser.setBumphmodel(bumphmodel);
					 //SysUser user = SysUser.dao.findById(id);
					 buser.setUserid(user.getId());
					 buser.setUsername(user.getUsername());
					 buser.setOrgid(user.getOrgid());
					 buser.setOrgname(SysOrg.dao.findById(user.getOrgid()).getName());
					 buser.setLookornot("1");
					 //buser.setSortnum(String.valueOf(i));
					 buser.setSortnum(i);
					 buser.setLooked("0");
					 buser.setIfshow("1");//显示
					//buser.setParentid(buserP.getId());
					 buser.setIfcomplete("0");
					 buser.setBackup1(user.getName());
					 buser.setBackup2(DateUtil.getCurrentTime());
					 buser.setReadtime(DateUtil.getAfterDayDate("30"));
					 if(i==1) {
						buser.setBackup3("1"); //可签阅
					 }else {
						 
						 buser.setBackup3("0"); //不可签阅
					 }
					 
					 buser.save();
				 
				//}
				//i++;
			}
			
			i++;
			for (String orgid : orgA) {
				
				List<SysUser> users = SysUser.dao.find("select  * from sys_user where position in ('7','8') and orgid='"+orgid+"'");
				for (SysUser user : users) {
					OaBumphUser buser =new OaBumphUser();
					buser.setId(UuidUtil.getUUID());
					buser.setBumphid(bumphid);
					buser.setBumphmodel(bumphmodel);
					//buser.setParentid(buserP.getId());
					buser.setIfcomplete("0");
					buser.setOrgid(user.getOrgid());
					buser.setOrgname(SysOrg.dao.findById(user.getOrgid()).getName());
					buser.setLookornot("1");
					buser.setLooked("0");
					buser.setIfshow("1");
					buser.setIfcomplete("0");
					buser.setUsername(user.getUsername());
					buser.setUserid(user.getId());
					//List<OaBumphUser> buserL = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
					//buser.setSortnum(String.valueOf(i));
					buser.setSortnum(i);
					buser.setBackup1(user.getName());
					buser.setBackup2(DateUtil.getCurrentTime());
					buser.setReadtime(DateUtil.getAfterDayDate("30"));
					buser.setBackup3("0"); //不可签阅
					buser.save();
				}
				//i++;
			}
		
	}
	
	
	/**
	 *  待确认？？？？？
	* @Title: saveBumphUsers 
	* @Description: 存储收文可查看相关人员  
	* @param type 类型 1 领导 0.部门
	* @param bumphId 收文ID
	* @param ids void 对应领导 或者部门的ID
	* @param bumphmodel void 公文模式
	 
	* @date 2020年9月3日下午9:53:18
	 */
	
	@Before(Tx.class)
	public void saveBumphUsers(String type,String bumphid,String  ids,String orgids, String bumphmodel) {
		int  i=1;
		String[] idsA = ids.split(",");//领导职务对应的值
		String[] orgA = orgids.split(",");//部门对应的id
		
	//	if(type.equals("1")) {
		
			for (String id : idsA) {
				 
				//List<OaBumphUser> buserL = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
				 List<SysUser> positionUsers = SysUser.dao.find("select  * from  sys_user where position='"+id+"'");
			 
				 for ( SysUser  user: positionUsers) {
					 OaBumphUser buser =new OaBumphUser();
					 buser.setId(UuidUtil.getUUID());
					 buser.setBumphid(bumphid);
					 buser.setBumphmodel(bumphmodel);
					 //SysUser user = SysUser.dao.findById(id);
					 buser.setUserid(user.getId());
					 buser.setUsername(user.getUsername());
					 buser.setOrgid(user.getOrgid());
					 buser.setOrgname(SysOrg.dao.findById(user.getOrgid()).getName());
					 buser.setLookornot("1");
					// buser.setSortnum(String.valueOf(i));
					 buser.setSortnum(i);
					 buser.setLooked("0");
					 buser.setIfshow("1");//显示
					//buser.setParentid(buserP.getId());
					 buser.setIfcomplete("0");
					 buser.setBackup1(user.getName());
					 buser.setBackup2(DateUtil.getCurrentTime());
					 buser.setReadtime(DateUtil.getAfterDayDate("30"));
					 if(i==1) {
						buser.setBackup3("1"); //可签阅
					 }else {
						 
						 buser.setBackup3("0"); //不可签阅
					 }
					 
					 buser.save();
				 
				}
				i++;
			}
			
			
			for (String orgid : orgA) {
				
				List<SysUser> users = SysUser.dao.find("select  * from sys_user where position in ('7','8') and orgid='"+orgid+"'");
				for (SysUser user : users) {
					OaBumphUser buser =new OaBumphUser();
					buser.setId(UuidUtil.getUUID());
					buser.setBumphid(bumphid);
					buser.setBumphmodel(bumphmodel);
					//buser.setParentid(buserP.getId());
					buser.setIfcomplete("0");
					buser.setOrgid(user.getOrgid());
					buser.setOrgname(SysOrg.dao.findById(user.getOrgid()).getName());
					buser.setLookornot("1");
					buser.setLooked("0");
					buser.setIfshow("1");
					buser.setIfcomplete("0");
					buser.setUsername(user.getUsername());
					buser.setUserid(user.getId());
					//List<OaBumphUser> buserL = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
					//buser.setSortnum(String.valueOf(i));
					buser.setSortnum(i);
					buser.setBackup1(user.getName());
					buser.setBackup2(DateUtil.getCurrentTime());
					buser.setReadtime(DateUtil.getAfterDayDate("30"));
					buser.setBackup3("0"); //不可签阅
					buser.save();
				}
				//i++;
			}
			
			
			
		/*}
		
		
		if(type.equals("0")) {*/
			
		/*	for (String id : idsA) {
				OaBumphUser buserP =new OaBumphUser();
				buserP.setId(UuidUtil.getUUID());
				buserP.setBumphid(bumphid);
				buserP.setBumphmodel(bumphmodel);
				buserP.setIfcomplete("0");
				SysOrg org = SysOrg.dao.findById(id);
				buserP.setOrgid(org.getId());
				buserP.setOrgname(org.getName());
				buserP.setLookornot("0");
				buserP.setIfshow("0");
				List<OaBumphUser> buserList = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
				buserP.setSortnum(String.valueOf(buserList.size()+1));
				buserP.setBackup2(DateUtil.getCurrentTime());
				buserP.setReadtime(DateUtil.getAfterDayDate("30"));
				buserP.save();
				
				
				
			}*/
		//}
	}
	
	
	/**
	 * 
	 * @Title: saveBumphUsers 
	 * @Description: 存储收文可查看相关人员  
	 * @param type 类型 1 领导 0.部门
	 * @param bumphId 收文ID
	 * @param ids void 对应领导 或者部门的ID
	 * @param bumphmodel void 公文模式
	 
	 * @date 2020年9月3日下午9:53:18
	 */
	
	@Before(Tx.class)
	public void saveBumphUsers(String type,String bumphid,String  ids,String bumphmodel) {
		
		if(bumphmodel.equals("1")) {
			String[] idsA = ids.split(",");
			
			if(type.equals("1")) {
				OaBumphUser buserP =new OaBumphUser();
				buserP.setId(UuidUtil.getUUID());
				buserP.setBumphid(bumphid);
				buserP.setBumphmodel(bumphmodel);
				buserP.setIfcomplete("0");
				List<SysOrg> orgList = SysOrg.dao.find("select *  from  sys_org where  name='中心领导'");
				buserP.setOrgid(orgList.get(0).getId());
				buserP.setOrgname(orgList.get(0).getName());
				buserP.setLookornot("0");
				buserP.setIfshow("0");
				List<OaBumphUser> buserList = OaBumphUser.dao.find("select  * from  oa_bumph_user where bumphid='"+bumphid+"'");
//				buserP.setSortnum(String.valueOf(buserList.size()+1));
				buserP.setSortnum(buserList.size()+1);
				buserP.setBackup2(DateUtil.getCurrentTime());
				buserP.setReadtime(DateUtil.getAfterDayDate("30"));
				buserP.save();
				for (String id : idsA) {
					OaBumphUser buser =new OaBumphUser();
					buser.setId(UuidUtil.getUUID());
					buser.setBumphid(bumphid);
					buser.setBumphmodel(bumphmodel);
					SysUser user = SysUser.dao.findById(id);
					buser.setUserid(id);
					buser.setUsername(user.getUsername());
					buser.setOrgid(user.getOrgid());
					buser.setOrgname(SysOrg.dao.findById(user.getOrgid()).getName());
					buser.setLookornot("1");
					List<OaBumphUser> buserL = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
	//				buser.setSortnum(String.valueOf(buserL.size()+1));
					buser.setSortnum(buserL.size()+1);
					buser.setLooked("0");
					buser.setIfshow("1");//显示
					buser.setParentid(buserP.getId());
					buser.setIfcomplete("0");
					buser.setBackup1(user.getName());
					buser.setBackup2(DateUtil.getCurrentTime());
					buser.setReadtime(DateUtil.getAfterDayDate("30"));
					buser.save();
				}
				
			}
			
			
			if(type.equals("0")) {
				
				for (String id : idsA) {
					OaBumphUser buserP =new OaBumphUser();
					buserP.setId(UuidUtil.getUUID());
					buserP.setBumphid(bumphid);
					buserP.setBumphmodel(bumphmodel);
					buserP.setIfcomplete("0");
					SysOrg org = SysOrg.dao.findById(id);
					buserP.setOrgid(org.getId());
					buserP.setOrgname(org.getName());
					buserP.setLookornot("0");
					buserP.setIfshow("0");
					List<OaBumphUser> buserList = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
					//buserP.setSortnum(String.valueOf(buserList.size()+1));
					buserP.setSortnum(buserList.size()+1);
					buserP.setBackup2(DateUtil.getCurrentTime());
					buserP.setReadtime(DateUtil.getAfterDayDate("30"));
					buserP.save();
					List<SysUser> users = SysUser.dao.find("select  * from sys_user where position in ('科长','副科长') and orgid='"+id+"'");
					for (SysUser user : users) {
						OaBumphUser buser =new OaBumphUser();
						buser.setId(UuidUtil.getUUID());
						buser.setBumphid(bumphid);
						buser.setBumphmodel(bumphmodel);
						buser.setParentid(buserP.getId());
						buser.setIfcomplete("0");
						buser.setOrgid(org.getId());
						buser.setOrgname(org.getName());
						buser.setLookornot("0");
						buser.setLooked("0");
						buser.setIfshow("1");
						buser.setIfcomplete("0");
						buser.setUsername(user.getUsername());
						buser.setUserid(user.getId());
						List<OaBumphUser> buserL = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
						//buser.setSortnum(String.valueOf(buserL.size()+1));
						buser.setSortnum(buserL.size()+1);
						buser.setBackup1(user.getName());
						buser.setBackup2(DateUtil.getCurrentTime());
						buser.setReadtime(DateUtil.getAfterDayDate("30"));
						buser.save();
					}
					
					
					
				}
			}
		}else {
			
			String[] idsA = ids.split(",");
			
			if(type.equals("1")) {
				OaBumphUser buserP =new OaBumphUser();
				buserP.setId(UuidUtil.getUUID());
				buserP.setBumphid(bumphid);
				buserP.setBumphmodel(bumphmodel);
				buserP.setIfcomplete("0");
				List<SysOrg> orgList = SysOrg.dao.find("select *  from  sys_org where  name='中心领导'");
				buserP.setOrgid(orgList.get(0).getId());
				buserP.setOrgname(orgList.get(0).getName());
				buserP.setLookornot("0");
				buserP.setIfshow("0");
				List<OaBumphUser> buserList = OaBumphUser.dao.find("select  * from  oa_bumph_user where bumphid='"+bumphid+"'");
				///buserP.setSortnum(String.valueOf(buserList.size()+1));
				buserP.setSortnum(buserList.size()+1);
				buserP.setBackup2(DateUtil.getCurrentTime());
				buserP.setReadtime(DateUtil.getAfterDayDate("30"));
				
				buserP.save();
				for (String id : idsA) {
					OaBumphUser buser =new OaBumphUser();
					buser.setId(UuidUtil.getUUID());
					buser.setBumphid(bumphid);
					buser.setBumphmodel(bumphmodel);
					SysUser user = SysUser.dao.findById(id);
					buser.setUserid(id);
					buser.setUsername(user.getUsername());
					buser.setOrgid(user.getOrgid());
					buser.setOrgname(SysOrg.dao.findById(user.getOrgid()).getName());
					buser.setLookornot("1");
					List<OaBumphUser> buserL = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
					//buser.setSortnum(String.valueOf(buserL.size()+1));
					buser.setSortnum(buserL.size()+1);
					buser.setLooked("0");
					buser.setIfshow("1");//显示
					buser.setParentid(buserP.getId());
					buser.setIfcomplete("0");
					buser.setBackup1(user.getName());
					buser.setBackup2(DateUtil.getCurrentTime());
					buser.setReadtime(DateUtil.getAfterDayDate("30"));
					buser.save();
				}
				
			}
			
			
			if(type.equals("0")) {
				
				for (String id : idsA) {
					OaBumphUser buserP =new OaBumphUser();
					buserP.setId(UuidUtil.getUUID());
					buserP.setBumphid(bumphid);
					buserP.setBumphmodel(bumphmodel);
					buserP.setIfcomplete("0");
					SysOrg org = SysOrg.dao.findById(id);
					buserP.setOrgid(org.getId());
					buserP.setOrgname(org.getName());
					buserP.setLookornot("0");
					buserP.setIfshow("0");
					List<OaBumphUser> buserList = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
					//buserP.setSortnum(String.valueOf(buserList.size()+1));
					buserP.setSortnum(buserList.size()+1);
					buserP.setReadtime(DateUtil.getAfterDayDate("30"));
					buserP.save();
					List<SysUser> users = SysUser.dao.find("select  * from sys_user where position in ('科长','副科长') and orgid='"+id+"'");
					for (SysUser user : users) {
						OaBumphUser buser =new OaBumphUser();
						buser.setId(UuidUtil.getUUID());
						buser.setBumphid(bumphid);
						buser.setBumphmodel(bumphmodel);
						buser.setParentid(buserP.getId());
						buser.setIfcomplete("0");
						buser.setOrgid(org.getId());
						buser.setOrgname(org.getName());
						buser.setLookornot("1");
						buser.setIfshow("1");
						buser.setIfcomplete("0");
						buser.setLooked("0");
						List<OaBumphUser> buserL = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+bumphid+"'");
						//buser.setSortnum(String.valueOf(buserL.size()+1));
						buser.setSortnum(buserL.size()+1);
						buser.setUsername(user.getUsername());
						buser.setUserid(user.getId());
						buser.setBackup1(user.getName());
						buser.setBackup2(DateUtil.getCurrentTime());
						buser.setReadtime(DateUtil.getAfterDayDate("30"));
						buser.save();
					}
					
					
					
				}
			}
			
			
		}
	}
	
	
	
	
	/***
	 * 存储主送和抄送单位
	 * @param type
	 * @param orgidstr
	 */
	@Before(Tx.class)
	public void saveOrg(String type,String bumphId,String orgidstr){
		if(StrKit.notBlank(orgidstr)){//单位字符串
			String idarr[] = orgidstr.split(",");
			for(String id :idarr){
				if(!"root".equals(id)){
					OaBumphOrg org = new OaBumphOrg();
					org.setId(UuidUtil.getUUID());
					org.setBumphId(bumphId);
					org.setOrgid(id);
					if(type.equals("1")) {
						//org.setOrgname(SysUser.dao.getById(id).getName());
						
					}else {
						org.setOrgname(SysOrg.dao.getById(id).getName());
					}
					org.setType(type);
					org.save();
				}
			}
		}
	}
	
	/***
	 * 删除
	 */
	@Before(Tx.class)
	public void delete(String id){
		
		OaBumph o = OaBumph.dao.findById(id);
		toRz(o);
		o.delete();
		
		
		
		OaBumphOrg.dao.deleteOrgByBumphId(id);
		
		StepUtil.deleteSteps(id);
	}
	
	
	
	public void  toRz(OaBumph o) {
		
		
		OaBumphRz orz=new OaBumphRz();
		
		orz.setId(o.getId());
		orz.setTitle(o.getTitle());
		orz.setSenderId(o.getSenderId());
		orz.setSenderName(o.getSenderName());
		orz.setSenderOrgid(o.getSenderOrgid());
		orz.setSenderOrgname(o.getSenderOrgname());
		orz.setDocNum(o.getDocNum());
		orz.setDocNumSource(o.getDocNumSource());
		orz.setDocNumYear(o.getDocNumYear());
		orz.setDocNumN(o.getDocNumN());
		orz.setContent(o.getContent());
		orz.setIfSubmit(o.getIfSubmit());
		orz.setIfSend(o.getIfSend());
		orz.setIfAgree(o.getIfAgree());
		orz.setIfComplete(o.getIfComplete());
		orz.setProcInsId(o.getProcInsId());
		orz.setFirstLeaderAudit(o.getFirstLeaderAudit());
		orz.setSecondLeaderAudit(o.getSecondLeaderAudit());
		orz.setSendTime(o.getSendTime());
		orz.setDocType(o.getDocType());
		orz.setType(o.getType());
		orz.setCreateTime(o.getCreateTime());
		orz.setCreateUserId(o.getCreateUserId());
		orz.setCreateUserName(o.getCreateUserName());
		orz.setCreateOrgId(o.getCreateOrgId());
		orz.setCreateOrgName(o.getCreateOrgName());
		orz.setReceiveTime(o.getReceiveTime());
		orz.setZc(o.getZc());
		orz.setDocFrom(o.getDocFrom());
		orz.setRegeitTime(o.getRegeitTime());
		orz.setFileOrnot(o.getFileOrnot());
		orz.setFileName(o.getFileName());
		orz.setFileNo(o.getFileNo());
		orz.setNbrids(o.getNbrids());
		orz.setNbrnames(o.getNbrnames());
		orz.setNbyj(o.getNbyj());
		orz.setClqk(o.getClqk());
		orz.setLeadersid(o.getLeadersid());
		orz.setLeadersname(o.getLeadersname());
		orz.setHostsid(o.getHostsid());
		orz.setHostsname(o.getHostsname());
		orz.setCustomersid(o.getCustomersid());
		orz.setCustomersname(o.getCustomersname());
		orz.setStatus(o.getStatus());
		orz.setBackup1(o.getBackup1());
		orz.setBackup2(o.getBackup2());
		orz.setBackup3(o.getBackup3());
		
		
		orz.save();
		
	List<OaSteps> steps = OaSteps.dao.find("select  * from  oa_steps  where oid='"+o.getId()+"'");
		
		for (OaSteps s : steps) {
		OaStepsRz steprz =new OaStepsRz();
		steprz.setId(s.getId());
		steprz.setOid(s.getOid());
		steprz.setType(s.getType());
		steprz.setStep(s.getStep());
		steprz.setTitle(s.getTitle());
		steprz.setUserid(s.getUserid());
		steprz.setUsername(s.getUsername());
		steprz.setOrgid(s.getOrgid());
		steprz.setOrgname(s.getOrgname());
		steprz.setSortnum(s.getSortnum());
		steprz.setIfshow(s.getIfshow());
		steprz.setRemarks(s.getRemarks());
		steprz.setParentid(s.getParentid());
		steprz.setIfcomplete(s.getIfcomplete());
		steprz.setCompletetime(s.getCompletetime());
		steprz.setCuserid(s.getCuserid());
		steprz.setCusername(s.getCusername());
		steprz.setCtime(s.getCtime());
		steprz.setButtondesc(s.getButtondesc());
		steprz.setButtontype(s.getButtontype());
		steprz.setStepdesc(s.getStepdesc());
		steprz.setBackup1(s.getBackup1());
		steprz.setBackup2(s.getBackup2());
		steprz.setBackup3(s.getBackup3());
		
		
		steprz.save();
		}
		
		
	}
 
	/***
	 * 启动流程
	 */
	@Before(Tx.class)
	public void startProcess(String id){
		WorkFlowService service = new WorkFlowService();
		Map<String, Object> var = new HashMap<String,Object>();
		//查询主送和抄送所有的用户
		List<OaBumphOrg> list = OaBumphOrg.dao.getList(id, null);//主送单位和抄送单位
		List<String> userlist = new ArrayList<String>();//需要阅读公文的人员列表
		for(OaBumphOrg org:list){
			List<SysUser> ulist = SysUser.dao.getUserListByOrgId(org.getOrgid());//单位下的用户
			for(SysUser u:ulist){
				userlist.add(u.getUsername());
			}
		}
		var.put("userlist", ListUtil.removeDuplicate(userlist));//需要阅读公文的所有人员
		OaBumph bumph = OaBumph.dao.findById(id);
		String procInsId = service.startProcess(id, bumph,bumph.getTitle(),var);
		bumph.setProcInsId(procInsId);
		bumph.setIfSubmit(Constants.IF_SUBMIT_YES);
		bumph.update();
	}
	
	@Before(Tx.class)
	public void callBack(String id){
		OaBumph bumph = OaBumph.dao.findById(id);
		String procid = bumph.getProcInsId();//流程实例id
    	bumph.setFirstLeaderAudit("");//清空审批信息
    	bumph.setSecondLeaderAudit("");
    	bumph.setProcInsId("");
    	bumph.setIfSubmit(Constants.IF_SUBMIT_NO);
    	bumph.update();
    	workFlowService.callBack(procid);
	}
	
	/***
	 * 提交任务
	 */
	@Before(Tx.class)
	public void completeTask(String pass,String comment,String taskid,OaBumph bumph){
		WorkFlowService service = new WorkFlowService();
		Map<String,Object> var = new HashMap<String,Object>();
		var.put("pass", pass);
		VTasklist task = VTasklist.dao.findById(taskid);
		if(task.getTASKDEFKEY().equals("bookersend")){//文书发文的任务
			bumph.setSendTime(DateUtil.getCurrentTime());
		}
    	service.completeTask(taskid,ShiroKit.getUsername(),comment,var);//提交任务
    	bumph.update();
	}
	
	/***
	 * 获取经办人 历史
	 */
	public Page<Record> getPartList(int curr, int pageSize, String title,String username){
		String sql =" FROM  "+OaBumph.TABLE_NAME+" b ,act_hi_identitylink i ,act_hi_procinst p where i.PROC_INST_ID_=p.ID_ AND i.PROC_INST_ID_ = b.proc_ins_id ";
		if(StrKit.notBlank(title)){
			sql += "and b.title like '%"+title+"%'";
		}
		if(StrKit.notBlank(username)){
			sql += " and i.USER_ID_='"+username+"' ";
		}
			sql +=" order by b.create_time desc ";
		Page<Record> page = Db.paginate(curr, pageSize, "SELECT DISTINCT b.*,p.PROC_DEF_ID_ defid ", sql);
		return page;
	}
	
	/***
	 * 导出
	 * @throws Exception 
	 */
	public File bumphExport(String id,HttpServletRequest request) throws Exception{
		String path = request.getSession().getServletContext().getRealPath("")+File.separator+"WEB-INF"+File.separator+"admin"+File.separator+"bumph"+File.separator+"template"+File.separator;
		OaBumph bumph = OaBumph.dao.findById(id);
		Map<String , Object > data = new HashMap<String , Object>();
		data.put("doc_num_n", bumph.getDocNumN());
		data.put("sender_orgname", bumph.getSenderOrgname());
		data.put("doc_num", bumph.getDocNum());
		String sendtime = bumph.getSendTime();
		if(StrKit.notBlank(sendtime)){//发文时间
			data.put("sendDate", DateUtil.dateToString(DateUtil.StringToDate(sendtime),7));
		}
		String receivetime = bumph.getSendTime();
		if(StrKit.notBlank(receivetime)){//收文时间
			data.put("receiveDate",DateUtil.dateToString(DateUtil.StringToDate(receivetime),7));
		}
		data.put("docName", bumph.getTitle());
		data.put("firstAuditComment", bumph.getFirstLeaderAudit());
		data.put("secondAuditComment", bumph.getSecondLeaderAudit());
		List<Record> list = workFlowService.getHisTaskParter(bumph.getProcInsId());
		List<String> receivelist = new ArrayList<String>();
		for(Record r : list){
			receivelist.add(r.getStr("name"));
		}
		data.put("receiver", StringUtils.join(receivelist,','));
		String exportURL = path+bumph.getTitle()+bumph.getId()+".docx";
		WordExportUtil.export(data, path+"bumph_"+bumph.getDocType()+".docx", exportURL);
		File file = new File(exportURL);
		if(file.exists()){
			return file;
		}else{
			return null;
		}
	}

	/***
	 * 获取主送和抄送单位
	 */
	public void setAttrFirstSecond(Controller c,String bumphId){
		//获取所有主送单位
		List<OaBumphOrg> flist = OaBumphOrg.dao.getList(bumphId,"1");
		List<String> f = new ArrayList<String>();
		List<String> fn = new ArrayList<String>();
		for(OaBumphOrg fo : flist){
			f.add(fo.getOrgid());
			fn.add(fo.getOrgname());
		}
		c.setAttr("firstOrgId", StringUtil.join(f,","));
		c.setAttr("firstOrgName", StringUtil.join(fn,","));
		c.setAttr("firstOrgName1", fn);
		c.setAttr("firstOrgId1", f);
		//获取所有抄送单位
		List<OaBumphOrg> slist = OaBumphOrg.dao.getList(bumphId,"0");
		List<String> s = new ArrayList<String>();
		List<String> sn = new ArrayList<String>();
		for(OaBumphOrg so : slist){
			s.add(so.getOrgid());
			sn.add(so.getOrgname());
		}
		c.setAttr("secondOrgId", StringUtil.join(s,","));
		c.setAttr("secondOrgName", StringUtil.join(sn,","));
		c.setAttr("secondOrgId1", s);
	}
}
