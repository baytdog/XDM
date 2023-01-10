
package com.pointlion.mvc.common.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		//系统设置的
		arp.addMapping("sys_user", "id", SysUser.class);//用户
		arp.addMapping("sys_menu", "id", SysMenu.class);//菜单
		arp.addMapping("sys_role", "id", SysRole.class);//角色
		arp.addMapping("sys_role_menu", "id", SysRoleMenu.class);//角色对应功能权限
		arp.addMapping("sys_role_user", "id", SysRoleUser.class);//用户角色
		arp.addMapping("sys_role_org", "id", SysRoleOrg.class);//用户组织结构-数据权限
		arp.addMapping("sys_org", "id", SysOrg.class);//组织结构
		arp.addMapping("sys_friend", "id", SysFriend.class);//用户好友
		arp.addMapping("sys_custom_setting", "id", SysCustomSetting.class);//自定义设置
		arp.addMapping("sys_point", "id", SysPoint.class);//积分
		arp.addMapping("sys_point_user", "id", SysPointUser.class);//用户积分
		arp.addMapping("sys_attachment", "id", SysAttachment.class);//系统附件
		arp.addMapping("sys_mobile_message", "id", SysMobileMessage.class);//系统短信模块
		arp.addMapping("sys_data_auth_rule", "id", SysDataAuth.class);//数据权限配置信息表
		arp.addMapping("sys_log", "id", SysLog.class);//系统日志表
		arp.addMapping("sys_dct", "id", SysDct.class);//系统字典表
		arp.addMapping("sys_dct_group", "id", SysDctGroup.class);//系统字典分组表
		arp.addMapping("SCH_JOB_EXECUTE", "ID", SchJobExecute.class);//定时任务执行管理
		arp.addMapping("SCH_JOB", "ID", SchJob.class);//定时任务字典管理
		//内容管理的
		arp.addMapping("cms_content", "id", CmsContent.class);//内容
		arp.addMapping("cms_type", "id", CmsType.class);//内容类型
		//即时通讯的
		arp.addMapping("chat_history", "id", ChatHistory.class);//群聊
		//办公的
		arp.addMapping("act_re_model", "ID_", ActReModel.class);//流程模型
		arp.addMapping("act_re_procdef", "ID_", ActReProcdef.class);
		arp.addMapping("v_tasklist", "TASKID", VTasklist.class);//任务--视图
		arp.addMapping("v_email", "id", VEmail.class);//任务--视图
		//arp.addMapping("v_hotline_letter", "id", VHotlineLetter.class);//任务--视图
		
		arp.addMapping("oa_notice", "id", OaNotice.class);//通知公告
		arp.addMapping("oa_notice_user", "id", OaNoticeUser.class);//通知公告收
		arp.addMapping("oa_bumph", "id", OaBumph.class);//公文
		arp.addMapping("oa_bumph_org", "id", OaBumphOrg.class);//公文主送抄送单位
		arp.addMapping("oa_bumph_org_user", "id", OaBumphOrgUser.class);//公文主送抄送人员表
		arp.addMapping("oa_apply_business_travel", "id", OaApplyBusinessTravel.class);//出差申请
		arp.addMapping("oa_apply_business_travel_report", "id", OaApplyBusinessTravelReport.class);//出差申请报告
		arp.addMapping("oa_apply_buy", "id", OaApplyBuy.class);//采购申请
		arp.addMapping("oa_apply_business_card", "id", OaApplyBusinessCard.class);//名片印刷申请
		arp.addMapping("oa_apply_borrow_money", "id", OaApplyBorrowMoney.class);//借款申请
		arp.addMapping("oa_apply_cost", "id", OaApplyCost.class);//费用申请
		arp.addMapping("oa_apply_common", "id", OaApplyCommon.class);//通用申请
		arp.addMapping("oa_apply_custom", "id", OaApplyCustom.class);//自定义流程
		arp.addMapping("oa_apply_buy_item", "id", OaApplyBuyItem.class);//采购申请明细
		arp.addMapping("oa_apply_leave", "id", OaApplyLeave.class);//请假
		arp.addMapping("oa_apply_use_car", "id", OaApplyUseCar.class);//车辆借用
		arp.addMapping("oa_apply_hotel", "id", OaApplyHotel.class);//宾馆预定申请
		arp.addMapping("oa_apply_office_object_buy", "id", OaApplyOfficeObjectBuy.class);//办公用品申购
		arp.addMapping("oa_apply_office_object", "id", OaApplyOfficeObject.class);//办公用品申领
		arp.addMapping("oa_apply_seal", "id", OaApplySeal.class);//用章申请
		arp.addMapping("oa_apply_train_ticket", "id", OaApplyTrainTicket.class);//车船票申请
		arp.addMapping("oa_apply_usercar_work", "id", OaApplyUsercarWork.class);//私车公用补贴申请
		arp.addMapping("oa_apply_gift", "id", OaApplyGift.class);//礼品申请
		arp.addMapping("oa_apply_meetroom", "id", OaApplyMeetroom.class);//会议室申请
		arp.addMapping("oa_apply_user_change_station", "id", OaApplyUserChangeStation.class);//调岗
		arp.addMapping("oa_apply_user_dimission", "id", OaApplyUserDimission.class);//离职
		arp.addMapping("oa_apply_user_regular", "id", OaApplyUserRegular.class);//转正
		arp.addMapping("oa_project", "id", OaProject.class);//项目立项申请
		arp.addMapping("oa_project_build", "id", OaProjectBuild.class);//项目立项申请
		arp.addMapping("oa_project_change_member", "id", OaProjectChangeMember.class);//项目成员变更
		arp.addMapping("oa_project_express_confirm", "id", OaProjectExpressConfirm.class);//项目单据快递确认
		arp.addMapping("oa_contract", "id", OaContract.class);//合同字典
		arp.addMapping("oa_contract_apply", "id", OaContractApply.class);//合同申请
		arp.addMapping("oa_contract_pay", "id", OaContractPay.class);//合同付款
		arp.addMapping("oa_contract_invoice", "id", OaContractInvoice.class);//合同开票
		arp.addMapping("oa_contract_change", "id", OaContractChange.class);//合同更改
		arp.addMapping("oa_contract_stop", "id", OaContractStop.class);//合同终止
		arp.addMapping("oa_apply_finance", "id", OaApplyFinance.class);//财务申请
		arp.addMapping("oa_apply_bank_account", "id", OaApplyBankAccount.class);//银行卡开销户申请
		arp.addMapping("oa_apply_work_overtime", "id", OaApplyWorkOvertime.class);//加班申请
		arp.addMapping("oa_flow_carbon_c", "id", OaFlowCarbonC.class);//流程抄送
		//资产管理的
		arp.addMapping("ams_asset", "id", AmsAsset.class);//资产管理
		arp.addMapping("ams_asset_allot", "id", AmsAssetAllot.class);//资产调配
		arp.addMapping("ams_asset_borrow", "id", AmsAssetBorrow.class);//资产借用
		arp.addMapping("ams_asset_dispose", "id", AmsAssetDispose.class);//资产处置
		arp.addMapping("ams_asset_need", "id", AmsAssetNeed.class);//资产需求
		arp.addMapping("ams_asset_receive", "id", AmsAssetReceive.class);//资产领用
		arp.addMapping("ams_asset_repair", "id", AmsAssetRepair.class);//资产报修
		arp.addMapping("ams_asset_sign_in", "id", AmsAssetSignIn.class);//资产录入
		//客户管理的
		arp.addMapping("crm_customer", "id", CrmCustomer.class);//客户管理
		arp.addMapping("crm_customer_company", "id", CrmCustomerCompany.class);//客户公司管理
		arp.addMapping("crm_customer_relation", "id", CrmCustomerRelation.class);//客户关系管理
		arp.addMapping("crm_customer_visit", "id", CrmCustomerVisit.class);//客户来访管理
		arp.addMapping("crm_order", "id", CrmOrder.class);//客户订单管理
		//一些可以用的资源（可能已经没用了）
		arp.addMapping("dct_resource_car", "id", DctResourceCar.class);//汽车字典管理
		arp.addMapping("dct_resource_meetroom", "id", DctResourceMeetroom.class);//会议室管理
		//抓取引擎
		arp.addMapping("grap_website", "id", GrapWebsite.class);
		arp.addMapping("grap_db_source", "id", GrapDbSource.class);
		arp.addMapping("grap_url", "id", GrapUrl.class);
		arp.addMapping("grap_url_col", "id", GrapUrlCol.class);
		arp.addMapping("grap_zibo_environment_gas", "id", GrapZiboEnvironmentGas.class);
		//工具类页面--过磅单
		arp.addMapping("tool_weight","id",ToolWeight.class);
		
		
		arp.addMapping("oa_outm", "id", OaOutm.class);
		arp.addMapping("oa_pic_news", "id", OaPicNews.class);
		arp.addMapping("oa_work_plan", "id", OaWorkPlan.class);
		arp.addMapping("oa_bumph_user", "id", OaBumphUser.class);
		arp.addMapping("oa_notices", "id",OaNotices.class);
		arp.addMapping("oa_menu_dict", "id",OaMenuDict.class);
		arp.addMapping("oa_showinfo", "id",OaShowinfo.class);
		arp.addMapping("oa_email", "id",OaEmail.class);
		arp.addMapping("oa_documents", "id",OaDocuments.class);
		arp.addMapping("oa_meetroom", "id",OaMeetroom.class);
		arp.addMapping("oa_dict", "id",OaDict.class);
		arp.addMapping("oa_dict_group","id", OaDictGroup.class);
		arp.addMapping("oa_test","id", OaTest.class);
		arp.addMapping("oa_answers","id", OaAnswers.class);
		arp.addMapping("oa_testresult","id", OaTestresult.class);
		arp.addMapping("oa_hotline","id", OaHotline.class);
		arp.addMapping("oa_hotline_user","id", OaHotlineUser.class);
		arp.addMapping("oa_senddoc","id", OaSenddoc.class);
		arp.addMapping("oa_senddoc_step","id", OaSenddocStep.class);
		arp.addMapping("oa_step_history","id", OaStepHistory.class);
		arp.addMapping("oa_letter","id", OaLetter.class);
		arp.addMapping("oa_letter_user","id", OaLetterUser.class);
		arp.addMapping("oa_steps","id", OaSteps.class);
		arp.addMapping("oa_types","id", OaTypes.class);
		arp.addMapping("oa_work_plan_remarks","id", OaWorkPlanRemarks.class);
		arp.addMapping("oa_nodes","id", OaNodes.class);
		arp.addMapping("oa_email_son","id", OaEmailSon.class);
		arp.addMapping("oa_ed_letter","id", OaEdLetter.class);
		arp.addMapping("oa_depart_notices","id", OaDepartNotices.class);
		arp.addMapping("oa_departments_files","id", OaDepartmentsFiles.class);
		arp.addMapping("oa_search","id", OaSearch.class);
		arp.addMapping("oa_rz", "id",OaRz.class);
		arp.addMapping("oa_tab_file", "id",OaTabFile.class);
		arp.addMapping("oa_bumph_rz", "id",OaBumphRz.class);
		arp.addMapping("oa_steps_rz", "id",OaStepsRz.class);
		//欣迪
		arp.addMapping("xd_employee","id",XdEmployee.class);
		arp.addMapping("xd_edutrain","id",XdEdutrain.class);
		arp.addMapping("xd_schoolinfo","id",XdSchoolinfo.class);
		arp.addMapping("xd_work_exper","id",XdWorkExper.class);
		arp.addMapping("xd_oplog_summary","id",XdOplogSummary.class);
		arp.addMapping("xd_oplog_detail","id",XdOplogDetail.class);
		arp.addMapping("xd_steps","id",XdSteps.class);
		arp.addMapping("xd_shift","id",XdShift.class);
		arp.addMapping("xd_dict","id",XdDict.class);
		arp.addMapping("xd_dict_group","id",XdDictGroup.class);
		arp.addMapping("xd_certificate","id",XdCertificate.class);
		arp.addMapping("xd_emp_cert","id",XdEmpCert.class);
		arp.addMapping("xd_contract_info","id",XdContractInfo.class);
		arp.addMapping("xd_projects","id",XdProjects.class);
		arp.addMapping("xd_effict","id",XdEffict.class);
		arp.addMapping("xd_records","id",XdRecords.class);
		arp.addMapping("xd_day_model","id",XdDayModel.class);
		arp.addMapping("xd_schedule","id",XdSchedule.class);
		arp.addMapping("xd_schedule_summary","id",XdScheduleSummary.class);
		arp.addMapping("xd_schedule_detail","id",XdScheduleDetail.class);
	}
}

