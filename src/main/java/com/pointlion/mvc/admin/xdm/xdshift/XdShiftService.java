package com.pointlion.mvc.admin.xdm.xdshift;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.model.XdShift;
import com.pointlion.mvc.common.utils.JSONUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XdShiftService{
	public static final XdShiftService me = new XdShiftService();
	public static final String TABLE_NAME = XdShift.tableName;

	/***
	 * query by id
	 */
	public XdShift getById(String id){
		return XdShift.dao.findById(id);
	}

	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String shiftname,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o where o.status='1'";
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		/*if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}*/
		if(StrKit.notBlank(shiftname)){
			sql = sql + " and o.shiftname like '%"+shiftname+"%'";
		}
		sql = sql + " order by o.sort_num";
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
			XdShift o = me.getById(id);
			o.setStatus("0");
//			o.delete();
			o.update();
		}

	}


	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		System.out.println(list);
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>1){
						SimpleUser user = ShiroKit.getLoginUser();
						String time = DateUtil.getCurrentTime();
						for(int i = 1;i<list.size();i++){//从第二行开始取
							List<String> shiftStr = list.get(i);
							System.out.println(shiftStr);
							XdShift shift=new XdShift();
							shift.setId(UuidUtil.getUUID());
							shift.setCtime(time);
							shift.setCuser(user.getId());
							shift.setStatus("1");
							if(shiftStr.get(0)==null ||"".equals(shiftStr.get(0).trim())){
								continue;
							}
							shift.setShiftname(shiftStr.get(0));
							shift.setBusitime(shiftStr.get(1));
							shift.setUnbusitime(shiftStr.get(2));
							shift.setTimebucket(shiftStr.get(3));
							shift.setHours(shiftStr.get(4));
							shift.setMiddleshift(shiftStr.get(5));
							shift.setNigthshift(shiftStr.get(6));
							shift.setShiftcost(shiftStr.get(7));
							shift.setDutyTime(shiftStr.get(8));
							shift.setDutyHours(Double.valueOf(shiftStr.get(9).equals("")?"0":shiftStr.get(9)));
							shift.setDutyamount(shiftStr.get(10));
							shift.setDutydesc(shiftStr.get(11));
							shift.setRemarks(shiftStr.get(12));
							shift.setSortNum(i);
							shift.save();
						}
						if(result.get("success")==null){
							result.put("success",true);//正常执行完毕
						}
					}else{
						result.put("success",false);//正常执行完毕
						message.add("excel中无内容");
						result.put("message", StringUtils.join(message," "));
					}
					result.put("message",StringUtils.join(message," "));
					if((Boolean) result.get("success")){//正常执行完毕
						return true;
					}else{//回滚
						return false;
					}
				}catch(Exception e){
					return false;
				}
			}
		});
		return result;
	}

}