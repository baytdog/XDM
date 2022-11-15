package com.pointlion.config.routes;

import com.jfinal.config.Routes;
import com.pointlion.mvc.admin.crm.company.CrmCustomerCompanyController;
import com.pointlion.mvc.admin.crm.customer.CrmCustomerController;
import com.pointlion.mvc.admin.crm.order.CrmOrderController;
import com.pointlion.mvc.admin.crm.relation.CrmCustomerRelationController;
import com.pointlion.mvc.admin.crm.visit.CrmCustomerVisitController;

public class CRMRoutes extends Routes {

    @Override
    public void config() {
        setBaseViewPath("/WEB-INF/admin/crm");
        add("/admin/crm/customer", CrmCustomerController.class,"/customer");//客户管理
        add("/admin/crm/company", CrmCustomerCompanyController.class,"/company");//客户公司管理
        add("/admin/crm/relation", CrmCustomerRelationController.class,"/relation");//客户关系管理
        add("/admin/crm/visit", CrmCustomerVisitController.class,"/visit");//客户来访管理
        add("/admin/crm/order", CrmOrderController.class,"/order");//客户来访管理
    }
}
