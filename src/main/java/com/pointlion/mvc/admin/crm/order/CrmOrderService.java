package com.pointlion.mvc.admin.crm.order;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.CrmOrder;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;

public class CrmOrderService{
	public static final CrmOrderService me = new CrmOrderService();
	public static final String TABLE_NAME = CrmOrder.tableName;

	/***
	 * query by id
	 */
	public CrmOrder getById(String id){
		return CrmOrder.dao.findById(id);
	}

	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String typeName,String orderCode,String orderNum){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(typeName)){
			sql = sql + " and o.type_name like '%"+typeName+"%'";
		}
		if(StrKit.notBlank(orderCode)){
			sql = sql + " and o.order_code like '%"+orderCode+"%'";
		}
		if(StrKit.notBlank(orderNum)){
			sql = sql + " and o.customer_code like '%"+orderNum+"%'";
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
			CrmOrder o = me.getById(id);
			o.delete();
		}
	}

	/***
	 * 导入
	 */
	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		boolean bool = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>1){
						SimpleUser user = ShiroKit.getLoginUser();
						String orgid = ShiroKit.getUserOrgId();
						String orgName = ShiroKit.getUserOrgName();
						String time = DateUtil.getCurrentTime();
						for(int i = 1;i<list.size();i++){//从第二行开始取
							List<String> l = list.get(i);
							String cpo = l.get(3);
//							CrmOrder old = CrmOrder.dao.findFirst("select * from crm_order o where o.customer_code='"+cpo+"'");
//							if(old!=null){
//								String msg = "第"+(i+1)+"行(客户PO:"+cpo+")已存在重复数据。<br/>";
//								message.add(msg);
//								result.put("success",false);
//							}else{
//                            接单日期、专案代码、客户名称、投单单号、品名/图号、投单数量不可为空
							if(StrKit.isBlank(getStr(l,0))){
								String msg = "第"+(i+1)+"行(客户PO:"+cpo+")，接单日期为空。<br/>";
								message.add(msg);
								result.put("success",false);
							}else if(StrKit.isBlank(getStr(l,1))){
								String msg = "第"+(i+1)+"行(客户PO:"+cpo+")，专案代码为空。<br/>";
								message.add(msg);
								result.put("success",false);
							}else if(StrKit.isBlank(getStr(l,4))){
								String msg = "第"+(i+1)+"行(客户PO:"+cpo+")，客户名称为空。<br/>";
								message.add(msg);
								result.put("success",false);
							}else if(StrKit.isBlank(getStr(l,5))){
								String msg = "第"+(i+1)+"行(客户PO:"+cpo+")，投单单号为空。<br/>";
								message.add(msg);
								result.put("success",false);
							}else if(StrKit.isBlank(getStr(l,6))){
								String msg = "第"+(i+1)+"行(客户PO:"+cpo+")，品名/图号为空。<br/>";
								message.add(msg);
								result.put("success",false);
							}else if(StrKit.isBlank(getStr(l,7))){
								String msg = "第"+(i+1)+"行(客户PO:"+cpo+")，投单数量为空。<br/>";
								message.add(msg);
								result.put("success",false);
							}else{
								CrmOrder order = new CrmOrder();
								order.setId(UuidUtil.getUUID());
								order.setUserid(user.getId());
								order.setCreateUserName(user.getName());
								order.setOrgId(orgid);
								order.setOrgName(orgName);
//                                order.setNum(getStr(l,0));//项次
								order.setOrderDate(getStr(l,0));//接单日期
								order.setProjectCode(getStr(l,1));//专案代码
								order.setCustomerCode(getStr(l,2));//客户PO
								order.setPoCount(getStr(l,3));//PO数量
								order.setCustomerName(getStr(l,4));//客户名称
								order.setOrderCode(getStr(l,5));//投单单号
								order.setTypeName(getStr(l,6));//品名/图号
								order.setOrderCount(getStr(l,7));//投单数量
								order.setSellCount(getStr(l,8));//销售数量
								order.setDollar(getStr(l,9));//美金
								order.setExchangeRate(getStr(l,10));//汇率
								order.setPrice(getStr(l,11));//单价
								order.setMoney(getStr(l,12));//总价
								order.setProductCount(getStr(l,13));//出货数量
								order.setOutPdtDate(getStr(l,14));//出货日期
								order.setWaitCount(getStr(l,15));//待出货数量
								order.setLifelessCount(getStr(l,16));//呆滞数量
								order.setReceivePdtDate(getStr(l,17));//验收日期
								order.setOrderNum(getStr(l,17));//对账单号
								order.setAccountDate(getStr(l,19));//对账日期
								order.setInvoiceDate(getStr(l,20));//开票日期
								order.setInvoiceCode(getStr(l,21));//发票代码
								order.setInvoiceMoney(getStr(l,22));//发票金额
								order.setPayDate(getStr(l,23));//付款日期
								order.setPayMoney(getStr(l,24));//付款金额
								order.setOperateUserName(user.getName());//业务员名称
								order.setCreateTime(time);
								order.save();
							}
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

	public static String getStr(List<String> l,int i){
		String re = "";
		try{
			re = l.get(i);
		}catch(Exception e){
			re = "";
		}
		return re;
	}
	/***
	 * 导出
	 * @return
	 */
	public File exportExcel(String path,String typeName,String orderCode,String orderNum){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(typeName)){
			sql = sql + " and o.type_name like '%"+typeName+"%'";
		}
		if(StrKit.notBlank(orderCode)){
			sql = sql + " and o.order_code like '%"+orderCode+"%'";
		}
		if(StrKit.notBlank(orderNum)){
			sql = sql + " and o.order_num like '%"+orderNum+"%'";
		}
		sql = sql + " order by o.create_time desc";


		List<CrmOrder> list = CrmOrder.dao.find(" select * "+sql);//查询全部
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> first = new ArrayList<String>();
		first.add("项次");//0
		first.add("接单日期");//1
		first.add("专案代码");//2
		first.add("客户PO#");//3
		first.add("PO数量");//4
		first.add("客户名称");//5
		first.add("投单单号");//6
		first.add("品名/图号");//7
		first.add("投单数量");//8
		first.add("销售数量");//9
		first.add("美金");//10
		first.add("汇率");//11
		first.add("单价(RMB)");//12
		first.add("总金额(RMB)");//13
		first.add("出货数量");//14
		first.add("出货日期");//15
		first.add("待出货数量");//16
		first.add("呆滞数量");//17
		first.add("验收日期");//18
		first.add("对账单号");//19
		first.add("对账日期");//20
		first.add("开票日期");//21
		first.add("发票号码");//22
		first.add("发票金额");//23
		first.add("付款日期");//24
		first.add("付款金额");//25
		first.add("业务员名称");//26
		rows.add(first);
		for(CrmOrder order:list){
			List<String> row = new ArrayList<String>();
			row.add(order.getNum());//0
			row.add(order.getOrderDate());//1
			row.add(order.getProjectCode());//2
			row.add(order.getCustomerCode());//3
			row.add(order.getPoCount());//4
			row.add(order.getCustomerName());//5
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
			row.add(order.getLifelessCount());//17-呆滞
			row.add(order.getReceivePdtDate());//18
			row.add(order.getOrderNum());//19--对账单号
			row.add(order.getAccountDate());//20--对账日期
			row.add(order.getInvoiceDate());//21
			row.add(order.getInvoiceCode());//22
			row.add(order.getInvoiceMoney());//23
			row.add(order.getPayDate());//24
			row.add(order.getPayMoney());//25
			row.add(order.getOperateUserName());//26
			rows.add(row);
		}
		File file = ExcelUtil.listToFile(path,rows);
		return file;
	}
}