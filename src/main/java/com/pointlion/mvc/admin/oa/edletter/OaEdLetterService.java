package com.pointlion.mvc.admin.oa.edletter;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaEdLetter;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.util.Exports;

public class OaEdLetterService{
	public static final OaEdLetterService me = new OaEdLetterService();
	public static final String TABLE_NAME = OaEdLetter.tableName;
	
	/***
	 * query by id
	 */
	public OaEdLetter getById(String id){
		return OaEdLetter.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String lnum,String lfromer,String lstate){
		String userId = ShiroKit.getUserId();
		
		String adminUser = Constants.ADMIN_USER;
		SysUser user = SysUser.dao.findById(userId);
		
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		
		if(user.getPosition().equals("2") || user.getPosition().equals("6")||adminUser.indexOf(ShiroKit.getUsername())!=-1) {
			
		}else {
			
			sql=sql+" and  o.lettercbrid='"+userId+"'";
		}
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			//sql = sql + " and o.letterdate>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
			sql = sql + " and o.letterdate>='"+startTime+"'";
		}
		if(StrKit.notBlank(endTime)){
			//sql = sql + " and o.letterdate<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
			sql = sql + " and o.letterdate<='"+endTime+"'";
		}
		if (StrKit.notBlank(lnum)) {
			sql = sql + " and o.letternum like '%"+lnum +"%'";
		}
	 
		if (StrKit.notBlank(lfromer)) {
			sql = sql + " and o.letters like '%"+lfromer +"%'";
		}
		
		if (StrKit.notBlank(lstate)) {
			sql = sql + " and o.lettetype='"+lstate+"'";
		}
		
		sql = sql + " order by o.ctime desc";
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
    		OaEdLetter o = me.getById(id);
    		o.delete();
    	}
	}
	
	
	 
		public File exportExcel(String path,String lnum,String lfromer,String startTime,String endTime,String lstate){
			
			String userId = ShiroKit.getUserId();
			
			String sql  = " from "+TABLE_NAME+" o   where 1=1";
			
			
			String adminUser = Constants.ADMIN_USER;
			SysUser user = SysUser.dao.findById(userId);
			
			if(user.getPosition().equals("2") || user.getPosition().equals("6")||adminUser.indexOf(ShiroKit.getUsername())!=-1) {
				
			}else {
				
				sql=sql+" and  o.lettercbrid='"+userId+"'";
			}
			//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
			if(StrKit.notBlank(startTime)){
				//sql = sql + " and o.letterdate>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
				sql = sql + " and o.letterdate>='"+ startTime+"'";
			}
			if(StrKit.notBlank(endTime)){
				//sql = sql + " and o.letterdate<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
				sql = sql + " and o.letterdate<='"+endTime+"'";
			}
			if (StrKit.notBlank(lnum)) {
				sql = sql + " and o.letternum like '%"+lnum +"%'";
			}
		 
			if (StrKit.notBlank(lfromer)) {
				sql = sql + " and o.letters like '%"+lfromer +"%'";
			}
			
			if (StrKit.notBlank(lstate)) {
				sql = sql + " and o.lettetype='"+lstate+"'";
			}
			
			sql = sql + " order by o.ctime desc";


			List<OaEdLetter> list = OaEdLetter.dao.find(" select * "+sql);//????????????
			List<List<String>> rows = new ArrayList<List<String>>();
			List<String> first = new ArrayList<String>();
			first.add("????????????");//0
			first.add("?????????");//1
			first.add("????????????");//2
			first.add("????????????");//3
			first.add("??????");//4
			/*first.add("????????????");//5
			first.add("????????????");//6
			first.add("??????/??????");//7
			first.add("????????????");//8
			first.add("????????????");//9
			first.add("??????");//10
			first.add("??????");//11
			first.add("??????(RMB)");//12
			first.add("?????????(RMB)");//13
			first.add("????????????");//14
			first.add("????????????");//15
			first.add("???????????????");//16
			first.add("????????????");//17
			first.add("????????????");//18
			first.add("????????????");//19
			first.add("????????????");//20
			first.add("????????????");//21
			first.add("????????????");//22
			first.add("????????????");//23
			first.add("????????????");//24
			first.add("????????????");//25
			first.add("???????????????");//26
	*/		rows.add(first);
			for(OaEdLetter letter:list){
				List<String> row = new ArrayList<String>();
				row.add(letter.getLetternum());//0
				row.add(letter.getLetters());//1
				row.add(letter.getLettertel());//2
				row.add(letter.getLetterdate());//3
				row.add(chinaType(letter.getLettetype()));//4
				/*row.add(order.getCustomerName());//5
				row.add(order.getOrderCode());//6
				row.add(order.getTypeName());//7
				row.add(order.getOrderCount());//8
				row.add(order.getSellCount());//9
				row.add(order.getDollar());//10
				row.add(order.getExchangeRate());//11
				row.add(order.getPrice());//12
				row.add(order.getMoney());//13
				row.add(order.getProductCount());//14
				row.add(order.getOutPdtDate());//15
				row.add(order.getWaitCount());//16
				row.add(order.getLifelessCount());//17-??????
				row.add(order.getReceivePdtDate());//18
				row.add(order.getOrderNum());//19--????????????
				row.add(order.getAccountDate());//20--????????????
				row.add(order.getInvoiceDate());//21
				row.add(order.getInvoiceCode());//22
				row.add(order.getInvoiceMoney());//23
				row.add(order.getPayDate());//24
				row.add(order.getPayMoney());//25
				row.add(order.getOperateUserName());//26
	*/			rows.add(row);
			}
			File file = ExcelUtil.listToFile(path,rows);
			return file;
		}
		
		
		  
		
		
		
		
		public String  chinaType(String type) {
			
			String  chinaType="";
			switch (type) {
			case "1":
				chinaType="??????";
				break;
			case "2":
				chinaType="??????";
				break;
			case "3":
				chinaType="????????????";
				break;
			case "4":
				chinaType="app";
				break;
			case "5":
				chinaType="?????????";
				break;
			case "6":
				chinaType="????????????";
				break;
			case "7":
				chinaType="?????????";
				break;
			case "8":
				chinaType="???????????????";
				break;
			case "9":
				chinaType="??????";
				break;
			case "10":
				chinaType="???????????????";
				break;
			case "11":
				chinaType="???????????????";
				break;
			case "12":
				chinaType="??????";
				break;
			case "13":
				chinaType="?????????";
				break;
			case "14":
				chinaType="??????";
				break;
			case "15":
				chinaType="?????????";
				break;
			case "16":
				chinaType="?????????";
				break;
			case "17":
				chinaType="????????????";
				break;

			default:
				break;
			}
			
			
			return  chinaType;
			
		}
		
	
		

		public File exportTotalExcel(String path,String lnum,String lfromer,String startTime,String endTime,String lstate,HttpServletRequest request,HttpServletResponse response) throws Exception{
			
			String userId = ShiroKit.getUserId();
			
			String sql  = " from "+TABLE_NAME+" o   where 1=1";
			
			
			String adminUser = Constants.ADMIN_USER;
			SysUser user = SysUser.dao.findById(userId);
			
			if(user.getPosition().equals("2") || user.getPosition().equals("6")||adminUser.indexOf(ShiroKit.getUsername())!=-1) {
				
			}else {
				
				sql=sql+" and  o.lettercbrid='"+userId+"'";
			}
			//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
			if(StrKit.notBlank(startTime)){
				//sql = sql + " and o.letterdate>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
				sql = sql + " and o.letterdate>='"+startTime+"'";
			}
			if(StrKit.notBlank(endTime)){
				//sql = sql + " and o.letterdate<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
				sql = sql + " and o.letterdate<='"+endTime+"'";
			}
			
			sql = sql + " order by o.ctime desc";


			List<OaEdLetter> list = OaEdLetter.dao.find(" select * "+sql);//????????????
			
			
			String []countArray =new  String[19];
			
			
			countArray[0]="??????";
			countArray[18]=list.size()+"";
			
			for (OaEdLetter letter : list) {
				
				int index = Integer.parseInt(letter.getLettetype());
				
				if(countArray[index]==null || countArray[index].equals("")) {
					
					countArray[index]="1";
				}else {
					countArray[index]=String.valueOf(Integer.parseInt(countArray[index])+1);
				}
				
			}
			
			
			
			
			
			String  opTime="????????????";
			if(startTime.equals("")&&endTime.equals("")) {
				
				
				String stime = list.get(list.size()-1).getCtime().split(" ")[0].toString().replaceFirst("-", "???").replaceFirst("-", "???")+"???";
				
				SimpleDateFormat sdf =new SimpleDateFormat("yyyy???MM???dd???");
				
				String etime =sdf.format(new Date());
				opTime=opTime+"\t\t"+stime+"-"+etime;
			}else {
				if(!startTime.equals("")&&!endTime.equals("")) {
					startTime=startTime.replaceFirst("-","???").replaceFirst("-","???")+"???";
					endTime=endTime.replaceFirst("-","???").replaceFirst("-","???")+"???";
					opTime=opTime+"\t\t"+startTime+"-"+endTime;
				}else {
					if(startTime.equals("")) {
						String stime = list.get(list.size()-1).getCtime().split(" ")[0].toString().replaceFirst("-", "???").replaceFirst("-", "???")+"???";
						endTime=endTime.replaceFirst("-","???").replaceFirst("-","???")+"???";
						
						opTime=opTime+"\t\t"+stime+"-"+endTime;
					}else {
						
						startTime=startTime.replaceFirst("-","???").replaceFirst("-","???")+"???";
						
						SimpleDateFormat sdf =new SimpleDateFormat("yyyy???MM???dd???");
						
						
						opTime=opTime+"\t\t"+startTime+"-"+endTime;
						
					}
					
				}
				
			}
			
			
			File exportCount = Exports.exportCount(request, response, path, countArray, user.getName(),opTime);
			
			//File file = ExcelUtil.listToFile(path,rows);
			//return file;
			return exportCount;
		}
		
}