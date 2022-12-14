package com.pointlion.config.routes;

import com.jfinal.config.Routes;
import com.pointlion.mvc.admin.oa.apply.businesstravel.OaApplyBusinessTravelController;
import com.pointlion.mvc.admin.oa.apply.businesstravelreport.OaApplyBusinessTravelReportController;
import com.pointlion.mvc.admin.oa.apply.buy.OaApplyBuyController;
import com.pointlion.mvc.admin.oa.apply.cost.OaApplyCostController;
import com.pointlion.mvc.admin.oa.apply.custom.OaApplyCustomController;
import com.pointlion.mvc.admin.oa.apply.leave.OaApplyLeaveController;
import com.pointlion.mvc.admin.oa.apply.userchangestation.OaApplyUserChangeStationController;
import com.pointlion.mvc.admin.oa.apply.userdimission.OaApplyUserDimissionController;
import com.pointlion.mvc.admin.oa.apply.userregular.OaApplyUserRegularController;
import com.pointlion.mvc.admin.oa.apply.workovertime.OaApplyWorkOvertimeController;
import com.pointlion.mvc.admin.oa.common.CommonBusinessController;
import com.pointlion.mvc.admin.oa.contract.OaContractController;
import com.pointlion.mvc.admin.oa.dct.resourcecar.DctResourceCarController;
import com.pointlion.mvc.admin.oa.dct.resourcemeetroom.DctResourceMeetroomController;
import com.pointlion.mvc.admin.oa.departmentsfiles.OaDepartmentsFilesController;
import com.pointlion.mvc.admin.oa.departnotices.OaDepartNoticesController;
import com.pointlion.mvc.admin.oa.dict.OaDictController;
import com.pointlion.mvc.admin.oa.edletter.OaEdLetterController;
import com.pointlion.mvc.admin.oa.meetroom.OaMeetroomController;
import com.pointlion.mvc.admin.oa.notice.NoticeController;
import com.pointlion.mvc.admin.oa.notices.OaNoticesController;
import com.pointlion.mvc.admin.oa.old.tabfile.OaTabFileController;
import com.pointlion.mvc.admin.oa.outm.OaOutmController;
import com.pointlion.mvc.admin.oa.picnew.OaPicNewsController;
import com.pointlion.mvc.admin.oa.project.OaProjectController;
import com.pointlion.mvc.admin.oa.project.build.OaProjectBuildController;
import com.pointlion.mvc.admin.oa.project.changemember.OaProjectChangeMemberController;
import com.pointlion.mvc.admin.oa.project.expressconfirm.OaProjectExpressConfirmController;
import com.pointlion.mvc.admin.oa.stephistory.OaStepHistoryController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowController;
import com.pointlion.mvc.admin.oa.workflow.flowimg.FlowImgController;
import com.pointlion.mvc.admin.oa.workflow.flowtask.FlowTaskController;
import com.pointlion.mvc.admin.oa.workflow.model.ModelController;
import com.pointlion.mvc.admin.oa.workflow.model.ModelEditorJsonRestResource;
import com.pointlion.mvc.admin.oa.workflow.model.ModelSaveRestResource;
import com.pointlion.mvc.admin.oa.workflow.rest.ProcessDefinitionDiagramLayoutResource;
import com.pointlion.mvc.admin.oa.workflow.rest.ProcessInstanceDiagramLayoutResource;
import com.pointlion.mvc.admin.oa.workflow.rest.ProcessInstanceHighlightsResource;
import com.pointlion.mvc.admin.oa.workflow.rest.StencilsetRestResource;
import com.pointlion.mvc.admin.workplan.OaWorkPlanController;

public class OARoutes extends Routes{

	@Override
	public void config() {
		setBaseViewPath("/WEB-INF/admin/oa");
		//?????????????????????
		add("/admin/oa/common/business",CommonBusinessController.class,"/common");
		//?????????
		add("/admin/oa/model",ModelController.class,"/workflow/model");//?????????-??????
		add("/admin/oa/workflow",WorkFlowController.class,"/workflow");//?????????
		/***????????????****/
		add("/admin/oa/notice",NoticeController.class,"/notice");//????????????
		//????????????
		//add("/admin/oa/workplan",WorkPlanController.class,"/workplan");//????????????
		add("/admin/oa/workplan",OaWorkPlanController.class,"/workplan");//????????????
		//??????
		//add("/admin/oa/bumph",BumphController.class,"/bumph");//????????????---???????????????????????????
		
		add("/admin/oa/outm",OaOutmController.class,"/outm");//????????????---???????????????????????????
		
		add("/admin/oa/notices",OaNoticesController.class,"/notices");
		//add("/admin/oa/documents",OaDocumentsController.class,"/documents");
		
		add("/admin/oa/picnews",OaPicNewsController.class,"/picnews");
		//add("/admin/oa/email",OaEmailController.class,"/email");
		//add("/admin/oa/showinfo",OaShowinfoController.class,"/showinfo");
		add("/admin/oa/meetroom",OaMeetroomController.class,"/meetroom");
		add("/admin/oa/dict",OaDictController.class,"/dict");
		//add("/admin/oa/hotline",OaHotlineController.class,"/hotline");
		//add("/admin/oa/senddoc",OaSenddocController.class,"/senddoc");
		//add("/admin/oa/letter",OaLetterController.class,"/letter");
		add("/admin/oa/stephistory",OaStepHistoryController.class,"/stephistory");
		add("/admin/oa/edletter",OaEdLetterController.class,"/edletter");
		add("/admin/oa/departnotices",OaDepartNoticesController.class,"/departnotices");
		add("/admin/oa/departmentsfiles",OaDepartmentsFilesController.class,"/departmentsfiles");
		add("/admin/oa/tabfile",OaTabFileController.class,"/tabfile");
		
		
		
		
		
		//????????????
		add("/admin/oa/project/build",OaProjectBuildController.class,"/project/build");//????????????
		add("/admin/oa/project/changemember",OaProjectChangeMemberController.class,"/project/changemember");//??????????????????
		add("/admin/oa/project/expressconfirm",OaProjectExpressConfirmController.class,"/project/expressconfirm");
		add("/admin/oa/project",OaProjectController.class,"/project");//????????????
		//????????????
		add("/admin/oa/contract",OaContractController.class,"/contract");//????????????
		//add("/admin/oa/contract/apply",OaContractApplyController.class,"/contract/apply");//????????????
		//add("/admin/oa/contract/change",OaContractChangeController.class,"/contract/change");//????????????
		//add("/admin/oa/contract/invoice",OaContractInvoiceController.class,"/contract/invoice");//????????????
		//add("/admin/oa/contract/pay",OaContractPayController.class,"/contract/pay");//????????????
		//add("/admin/oa/contract/stop",OaContractStopController.class,"/contract/stop");//????????????
		//????????????---???????????????????????????
		add("/admin/oa/workflow/flowtask",FlowTaskController.class,"/workflow/flowtask");//??????????????????
		//add("/admin/oa/apply/common", OaApplyCommonController.class,"/apply/common");//????????????
		add("/admin/oa/apply/businesstravel", OaApplyBusinessTravelController.class,"/apply/businesstravel");//????????????
		add("/admin/oa/apply/businesstravelreport", OaApplyBusinessTravelReportController.class,"/apply/businesstravelreport");//????????????
		//add("/admin/oa/apply/businesscard",OaApplyBusinessCardController.class,"/apply/businesscard");//??????????????????
		add("/admin/oa/apply/buy",OaApplyBuyController.class,"/apply/buy");//????????????
		add("/admin/oa/apply/custom",OaApplyCustomController.class,"/apply/custom");//???????????????
		add("/admin/oa/apply/leave",OaApplyLeaveController.class,"/apply/leave");//?????????
		//add("/admin/oa/apply/usecar",OaApplyUseCarController.class,"/apply/usecar");//????????????
		//add("/admin/oa/apply/hotel",OaApplyHotelController.class,"/apply/hotel");//??????????????????
		//add("/admin/oa/apply/officeobjectbuy", OaApplyOfficeObjectBuyController.class,"/apply/officeobjectbuy");//??????????????????
		//add("/admin/oa/apply/officeobject",OaApplyOfficeObjectController.class,"/apply/officeobject");//??????????????????
		//add("/admin/oa/apply/seal",OaApplySealController.class,"/apply/seal");//????????????
		//add("/admin/oa/apply/trainticket",OaApplyTrainTicketController.class,"/apply/trainticket");//???????????????
		//add("/admin/oa/apply/usercarwork",OaApplyUsercarWorkController.class,"/apply/usercarwork");//????????????????????????
		//add("/admin/oa/apply/gift",OaApplyGiftController.class,"/apply/gift");//????????????
		//add("/admin/oa/apply/meetroom",OaApplyMeetroomController.class,"/apply/meetroom");//???????????????
		add("/admin/oa/apply/userchangestation",OaApplyUserChangeStationController.class,"/apply/userchangestation");//????????????
		add("/admin/oa/apply/userdimission",OaApplyUserDimissionController.class,"/apply/userdimission");//????????????
		add("/admin/oa/apply/userregular",OaApplyUserRegularController.class,"/apply/userregular");//????????????
		add("/admin/oa/apply/cost",OaApplyCostController.class,"/apply/cost");//????????????
		//add("/admin/oa/apply/finance",OaApplyFinanceController.class,"/apply/finance");//???????????????
		add("/admin/oa/apply/workovertime", OaApplyWorkOvertimeController.class,"/apply/workovertime");//????????????
		//add("/admin/oa/apply/bankaccount",OaApplyBankAccountController.class,"/apply/bankaccount");//????????????????????????
		//add("/admin/oa/apply/borrowmoney", OaApplyBorrowMoneyController.class,"/apply/borrowmoney");//????????????
		//???????????????
		add("/admin/oa/dct/reso/admin/oa/apply/usecar/urcecar",DctResourceCarController.class,"/dct/resourcecar");//??????????????????
		add("/admin/oa/dct/resourcemeetroom",DctResourceMeetroomController.class,"/dct/resourcemeetroom");//???????????????
		//????????????????????????????????????????????????
		add("/admin/oa/process-instance/highlights",ProcessInstanceHighlightsResource.class);//modeler
		add("/admin/oa/process-instance/diagram-layout",ProcessInstanceDiagramLayoutResource.class);//modeler
		add("/admin/oa/process-definition/diagram-layout",ProcessDefinitionDiagramLayoutResource.class);//modeler
		add("/admin/oa/workflow/flowimg", FlowImgController.class);//??????????????????
		add("/admin/oa/modelEditor/save",ModelSaveRestResource.class);
		add("/admin/oa/modelEditor/json",ModelEditorJsonRestResource.class);
		add("/admin/oa/editor/stencilset",StencilsetRestResource.class);
	}

}
