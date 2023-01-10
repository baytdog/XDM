package com.pointlion.config.routes;

import com.jfinal.config.Routes;
import com.pointlion.mvc.admin.oa.dict.OaDictController;
import com.pointlion.mvc.admin.xdm.xdcertificate.XdCertificateController;
import com.pointlion.mvc.admin.xdm.xddict.XdDictController;
import com.pointlion.mvc.admin.xdm.xdedutrain.XdEdutrainController;
import com.pointlion.mvc.admin.xdm.xdempcert.XdEmpCertController;
import com.pointlion.mvc.admin.xdm.xdemployee.XdEmployeeController;
import com.pointlion.mvc.admin.xdm.xdprojects.XdProjectsController;
import com.pointlion.mvc.admin.xdm.xdrecords.XdRecordsController;
import com.pointlion.mvc.admin.xdm.xdschedule.XdScheduleController;
import com.pointlion.mvc.admin.xdm.xdschedulesummary.XdScheduleSummaryController;
import com.pointlion.mvc.admin.xdm.xdschoolinfo.XdSchoolinfoController;
import com.pointlion.mvc.admin.xdm.xdshift.XdShiftController;
import com.pointlion.mvc.admin.xdm.xdsteps.XdStepsController;
import com.pointlion.mvc.admin.xdm.xdworkexper.XdWorkExperController;
import com.pointlion.mvc.common.model.XdRecords;

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
        add("/admin/xd/schedulesummary", XdScheduleSummaryController.class,"/schedulesummary");


    }
}
