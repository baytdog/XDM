package com.pointlion.config.routes;

import com.jfinal.config.Routes;
import com.pointlion.mvc.admin.ams.allot.AmsAssetAllotController;
import com.pointlion.mvc.admin.ams.asset.AmsAssetController;
import com.pointlion.mvc.admin.ams.borrow.AmsAssetBorrowController;
import com.pointlion.mvc.admin.ams.dispose.AmsAssetDisposeController;
import com.pointlion.mvc.admin.ams.need.AmsAssetNeedController;
import com.pointlion.mvc.admin.ams.receive.AmsAssetReceiveController;
import com.pointlion.mvc.admin.ams.repair.AmsAssetRepairController;
import com.pointlion.mvc.admin.ams.signin.AmsAssetSignInController;

public class AMSRoutes extends Routes{

	@Override
	public void config() {
		setBaseViewPath("/WEB-INF/admin/ams");
		add("/admin/ams/asset",AmsAssetController.class,"/asset");//资产管理----资产字典
		add("/admin/ams/repair",AmsAssetRepairController.class,"/repair");//资产报修----报修
		add("/admin/ams/borrow",AmsAssetBorrowController.class,"/borrow");//资产借用----借用其他部门的资产设备
		add("/admin/ams/dispose",AmsAssetDisposeController.class,"/dispose");//资产处置----资产处理
		add("/admin/ams/receive",AmsAssetReceiveController.class,"/receive");//资产领用----入库之后，从数据库中领用到本部门
		add("/admin/ams/allot",AmsAssetAllotController.class,"/allot");//资产调配---把自己部门的资产调配给其他部门
		add("/admin/ams/need",AmsAssetNeedController.class,"/need");//资产需要----需要购买
		add("/admin/ams/signin",AmsAssetSignInController.class,"/signin");//资产录入----录入新的资产
	}

}
