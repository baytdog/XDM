package com.pointlion.config.routes;

import com.jfinal.config.Routes;
import com.pointlion.mvc.admin.grap.dbsource.GrapDbSourceController;
import com.pointlion.mvc.admin.grap.url.GrapUrlController;
import com.pointlion.mvc.admin.grap.url.bus.GrapBusinessController;
import com.pointlion.mvc.admin.grap.urlcol.GrapUrlColController;
import com.pointlion.mvc.admin.grap.website.GrapWebsiteController;

public class GrapRoutes extends Routes{

    @Override
    public void config() {
        setBaseViewPath("/WEB-INF/admin/grap");
        add("/admin/grap/website",GrapWebsiteController.class,"/website");//抓取站点管理
        add("/admin/grap/dbsource",GrapDbSourceController.class,"/dbsource");//抓取数据源管理
        add("/admin/grap/url",GrapUrlController.class,"/url");//抓取链接管理
        add("/admin/grap/urlcol",GrapUrlColController.class,"/urlcol");//抓取存入列管理

        add("/admin/grap/business",GrapBusinessController.class,"/business");
    }
}
