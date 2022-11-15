package com.pointlion.mvc.admin.oa.letter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaLetter;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StepUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaLetterService{
	public static final OaLetterService me = new OaLetterService();
	public static final String TABLE_NAME = OaLetter.tableName;
	
	/***
	 * query by id
	 */
	public OaLetter getById(String id){
		return OaLetter.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaLetter o = me.getById(id);
    		o.delete();
    		
    		StepUtil.deleteSteps(id);
    	}
	}
	
	/***
	 * get page
	 */
	public Page<Record> getListPage(int pnum,int psize){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where o.cuserid='"+userId+"' order by ctime desc";
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		//sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getListPage(int pnum,int psize,String startTime,String endTime,String lnum,String lfromer,String lfromnum,String lstate){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where 1=1";
		

		if(Constants.ADMIN_USER.indexOf(ShiroKit.getUsername())!=-1) {
			
		}else {
			sql=sql+" and o.cuserid='"+userId+"'";
		}
		
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.fromtime>='"+ startTime+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.fromtime<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(lnum)){
			sql = sql + " and o.num like '%"+lnum+"%'";
		}
		if(StrKit.notBlank(lfromer)){
			sql = sql + " and o.fromer like '%"+lfromer+"%'";
		}
		if(StrKit.notBlank(lfromnum)){
			sql = sql + " and o.fromnum like '%"+lfromnum+"%'";
		}
		if(StrKit.notBlank(lstate)){
			sql = sql + " and o.status = '"+lstate+"'";
		}
		
		
		sql = sql + " order by o.ctime desc";
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		//sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	
	public Page<Record> getPLetterListPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o  left join  oa_steps s on o.id=s.oid   where  s.userid='"+ShiroKit.getUserId()+"' and s.ifcomplete='0'";
 
		sql = sql + " order by o.ctime desc,s.ifcomplete ";
		return Db.paginate(pnum, psize, " select o.*,s.id as sid,s.step as sstep, s.ifcomplete", sql);
	}
	
	 
		public File exportExcel(String path,String lnum,String lfromer,String lfromnum,String startTime,String endTime,String lstate){
			
			String userId = ShiroKit.getUserId();
			
			String sql  = " from "+TABLE_NAME+" o   where 1=1";
			
			
			String adminUser = Constants.ADMIN_USER;
			SysUser user = SysUser.dao.findById(userId);
			
			if(user.getPosition().equals("2") || user.getPosition().equals("6")||adminUser.indexOf(ShiroKit.getUsername())!=-1) {
				
			}else {
				
				sql=sql+" and  o.cuserid='"+userId+"'";
			}
			if(StrKit.notBlank(startTime)){
				sql = sql + " and o.fromtime>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
			}
			if(StrKit.notBlank(endTime)){
				sql = sql + " and o.fromtime<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
			}
			if (StrKit.notBlank(lnum)) {
				sql = sql + " and o.num like '%"+lnum +"%'";
			}
		 
			if (StrKit.notBlank(lfromer)) {
				sql = sql + " and o.fromer like '%"+lfromer +"%'";
			}
			if (StrKit.notBlank(lfromnum)) {
				sql = sql + " and o.fromnum like '%"+lfromnum+"%'";
			}
			
			if (StrKit.notBlank(lstate)) {
				sql = sql + " and o.status='"+lstate+"'";
			}
			
			sql = sql + " order by o.ctime desc";


			List<OaLetter> list = OaLetter.dao.find(" select * "+sql);//查询全部
			List<List<String>> rows = new ArrayList<List<String>>();
			List<String> first = new ArrayList<String>();
			first.add("类型");
			first.add("信访编号");//0
			first.add("登记日期");
			first.add("信访人");//1
			first.add("联系方式");//2
			first.add("单位");//3
			first.add("来文字号");//3
			first.add("类别");//3
			first.add("来源");//3
			first.add("关键字");//3
			first.add("反应内容");//3
			first.add("结果");//3
			first.add("原因");//3
			first.add("信访人意见");//3
			
			
			rows.add(first);
			for(OaLetter letter:list){
				List<String> row = new ArrayList<String>();
				row.add(letter.getType().toString().equals("1")?"普通信访":"重要信访");//0
				row.add(letter.getNum());//0
				row.add(letter.getFromtime());//0
				row.add(letter.getFromer());//0
				row.add(letter.getContact());//0
				row.add(letter.getFromunit());//0
				row.add(letter.getFromnum());//0
				row.add(formatLettertype(letter.getLettertype()));//0
				row.add(formatFromchannel(letter.getFromchannel()));//0
				row.add(letter.getKeywords());//0
				row.add(letter.getContents());//0
				row.add(formatLetterresult(letter.getLetterresult()));//0
				row.add(formatLetterreason(letter.getLetterreason()));//0
				row.add(formatFromersug(letter.getFromersug()));//0
				
				
	 			rows.add(row);
			}
			File file = ExcelUtil.listToFile(path,rows);
			return file;
		}
	
		
		
		public String formatFromchannel(String fromchannel) {
			String fFromchannel="";
			switch (fromchannel) {
			case "1":
				fFromchannel="国家局转信";
				break;
			case "2":
				fFromchannel="国家局转访";
				break;
			case "3":
				fFromchannel="市转来信";
				break;
			case "4":
				fFromchannel="市转来访";
				break;
			case "5":
				fFromchannel="市委领导信箱";
				break;
			case "6":
				fFromchannel="市长信箱";
				break;
			case "7":
				fFromchannel="委转来信";
				break;
			case "8":
				fFromchannel="委转来访";
				break;
			case "9":
				fFromchannel="委转来电";
				break;
			case "10":
				fFromchannel="中心来访";
				break;
			case "11":
				fFromchannel="中心来信";
				break;
			case "12":
				fFromchannel="交通网";
				break;
			case "13":
				fFromchannel="主任信箱";
				break;
			case "14":
				fFromchannel="投诉受理信箱";
				break;
			case "15":
				fFromchannel="市交通委信箱";
				break;

			default:
				break;
			}
			return fFromchannel;
			
		}
	
		
		

		public String formatLettertype(String lettertype) {
			String flettertype="";
			switch (lettertype) {
			case "1":
				flettertype="申述";
				break;
			case "2":
				flettertype="求决";
				break;
			case "3":
				flettertype="举报";
				break;
			case "4":
				flettertype="意见建议";
				break;
			case "5":
				flettertype="其他";
				break;

			default:
				break;
			}
			return flettertype;
			
		}
		
		
		public String formatLetterresult(String letterresult) {
			String fletterresult="";
			if(letterresult==null) {
				
			}else {
				
				switch (letterresult) {
				case "1":
					fletterresult="解决";
					break;
				case "2":
					fletterresult="部分解决";
					break;
				case "3":
					fletterresult="视为解决";
					break;
				case "4":
					fletterresult="未解决";
					break;
				case "5":
					fletterresult="留作参考";
					break;
					
				default:
					break;
				}
			}
			
			return fletterresult;
			
		}
		

		public String formatLetterreason(String letterreason) {
			String fLetterreason="";
			
			if(letterreason==null) {
				
			}else {
				
				switch (letterreason) {
				case "1":
					fLetterreason="无理/失实";
					break;
				case "2":
					fLetterreason="政策所限";
					break;
				case "3":
					fLetterreason="客观所限";
					break;
				case "4":
					fLetterreason="要求过高";
					break;
					
				default:
					break;
				}
			}
			return fLetterreason;
			
		}
		
		public String formatFromersug(String fromersug) {
			String fFromersug="";
			if(fromersug==null) {
				
			}else {
				
				switch (fromersug) {
				case "1":
					fFromersug="同意";
					break;
				case "2":
					fFromersug="不同意";
					break;
				case "3":
					fFromersug="未有明确意见";
					break;
				default:
					break;
				}
			}
			
			return fFromersug;
			
		}
}