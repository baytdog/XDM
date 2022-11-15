package com.pointlion.mvc.admin.oa.common;

import java.io.File;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.apply.bankaccount.OaApplyBankAccountService;
import com.pointlion.mvc.admin.oa.apply.finance.OaApplyFinanceService;
import com.pointlion.mvc.admin.oa.contract.OaContractService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.plugin.shiro.ShiroKit;

public class CommonBusinessController  extends BaseController{
	public static final OaContractService contractService = OaContractService.me;
	public static final OaApplyFinanceService financeService = OaApplyFinanceService.me;
	public static final OaApplyBankAccountService bankAccountService = OaApplyBankAccountService.me;
	public void export(){
		String id = getPara("id");
		String type = getPara("type");
		File file = null;
		try {
			if("contract".equals(type)){//如果是合同
				file = contractService.export(id, this.getRequest());
			}else if("finance".equals(type)){//如果是财务流程
				file = financeService.export(id, this.getRequest());
			}else if("bankaccount".equals(type)){//如果是银行卡开卡流程
				file = bankAccountService.export(id, this.getRequest());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderFile(file);
	}

	/***
	 * 选择业务表弹层
	 */
	public void openSelectBusinessDialog(){
		String table = getPara("table","");
		String otherLabelCol = getPara("otherLabelCol","");
		String otherValueCol = getPara("otherValueCol","");
		String uuid = getPara("uuid","");
		String extCol = getPara("extCol","");
		setAttr("table",table);
		setAttr("otherLabelCol",otherLabelCol);
		setAttr("otherValueCol",otherValueCol);
		setAttr("uuid",uuid);
		setAttr("extCol",extCol);
		renderIframe("selectBusinessDialog.html");
	}

	/****
	 * 组装选择数据
	 */
	public void selectBusinessPage(){
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		String table = getPara("table","");
		int pnum = Integer.valueOf(curr);
		int psize = Integer.valueOf(pageSize);
		String sql  = " from "+table+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1 ";
		String userId = ShiroKit.getUserId();
		//数据权限
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		sql = sql + " order by o.create_time desc";
		Page<Record> page = Db.paginate(pnum, psize, " select o.*,p.PROC_DEF_ID_ defid ", sql);
		renderPage(page.getList(),"",page.getTotalRow());
	}

}
