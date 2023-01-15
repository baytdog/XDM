package com.pointlion.config.routes;

import com.jfinal.config.Routes;
import com.pointlion.mvc.admin.oa.dict.OaDictController;
import com.pointlion.mvc.admin.xdm.xdattendance.XdAttendanceController;
import com.pointlion.mvc.admin.xdm.xdattendancedetail.XdAttendanceDetailController;
import com.pointlion.mvc.admin.xdm.xdattendancesummary.XdAttendanceSummaryController;
import com.pointlion.mvc.admin.xdm.xdcertificate.XdCertificateController;
import com.pointlion.mvc.admin.xdm.xddict.XdDictController;
import com.pointlion.mvc.admin.xdm.xdedutrain.XdEdutrainController;
import com.pointlion.mvc.admin.xdm.xdempcert.XdEmpCertController;
import com.pointlion.mvc.admin.xdm.xdemployee.XdEmployeeController;
import com.pointlion.mvc.admin.xdm.xdovertime.XdOvertimeController;
import com.pointlion.mvc.admin.xdm.xdovertimesummary.XdOvertimeSummaryController;
import com.pointlion.mvc.admin.xdm.xdprojects.XdProjectsController;
import com.pointlion.mvc.admin.xdm.xdrcp.XdRcpController;
import com.pointlion.mvc.admin.xdm.xdrcpsummary.XdRcpSummaryController;
import com.pointlion.mvc.admin.xdm.xdrecords.XdRecordsController;
import com.pointlion.mvc.admin.xdm.xdschedule.XdScheduleController;
import com.pointlion.mvc.admin.xdm.xdscheduledetail.XdScheduleDetailController;
import com.pointlion.mvc.admin.xdm.xdschedulesummary.XdScheduleSummaryController;
import com.pointlion.mvc.admin.xdm.xdschoolinfo.XdSchoolinfoController;
import com.pointlion.mvc.admin.xdm.xdsettleovertime.XdSettleOvertimeController;
import com.pointlion.mvc.admin.xdm.xdsettleovertimesummary.XdSettleOvertimeSummaryController;
import com.pointlion.mvc.admin.xdm.xdshift.XdShiftController;
import com.pointlion.mvc.admin.xdm.xdsteps.XdStepsController;
import com.pointlion.mvc.admin.xdm.xdworkexper.XdWorkExperController;
import com.pointlion.mvc.common.model.XdRecords;
import com.pointlion.mvc.common.model.XdScheduleDetail;

/**
 * @author thunoerobot
 * version 1.0.0
 * @title XDRoutes
 * @description
 * @create 2022/11/17 15:05
 **/
public class XDRoutes extends Routes {
    @Override
    public void config() {
        setBaseViewPath("/WEB-INF/admin/xdm");
        add("/admin/xd/xdedutrain", XdEdutrainController.class,"/xdedutrain");
        add("/admin/xd/xdemployee", XdEmployeeController.class,"/xdemployee");
        add("/admin/xd/xdschoolinfo", XdSchoolinfoController.class,"/xdschoolinfo");
        add("/admin/xd/xdworkexper", XdWorkExperController.class,"/xdworkexper");
        add("/admin/xd/steps", XdStepsController.class,"/steps");
        add("/admin/xd/xdshift", XdShiftController.class,"/xdshift");
        add("/admin/xd/xddict", XdDictController.class,"/xddict");
        add("/admin/xd/xdcertificate", XdCertificateController.class,"/xdcertificate");
        add("/admin/xd/xdempcert", XdEmpCertController.class,"/xdempcert");
        add("/admin/xd/xdprojects", XdProjectsController.class,"/xdprojects");
        add("/admin/xd/records", XdRecordsController.class,"/records");
        add("/admin/xd/xdschedule", XdScheduleController.class,"/xdschedule");
        add("/admin/xd/xdschedulesummary", XdScheduleSummaryController.class,"/xdschedulesummary");
        add("/admin/xd/xdscheduledeital", XdScheduleDetailController.class,"/xdscheduledeital");


        add("/admin/xd/xdattendance", XdAttendanceController.class,"/xdattendance");
        add("/admin/xd/xdattendancesummary", XdAttendanceSummaryController.class,"/xdattendancesummary");
        add("/admin/xd/xdattendancedeital", XdAttendanceDetailController.class,"/xdscheduledeital");


        add("/admin/xd/xdovertime", XdOvertimeController.class,"/xdovertime");
        add("/admin/xd/xdovertimesummary", XdOvertimeSummaryController.class,"/xdovertimesummary");

        add("/admin/xd/xdsettleovertime", XdSettleOvertimeController.class,"/xdsettleovertime");
        add("/admin/xd/xdsettleovertimesummary", XdSettleOvertimeSummaryController.class,"/xdsettleovertimesummary");

        add("/admin/xd/xdrcp", XdRcpController.class,"/xdrcp");
        add("/admin/xd/xdrcpsummary", XdRcpSummaryController.class,"/xdrcpsummary");


    }
}
