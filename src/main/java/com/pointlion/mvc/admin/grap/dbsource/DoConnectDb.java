package com.pointlion.mvc.admin.grap.dbsource;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.pointlion.mvc.common.model.GrapDbSource;

public class DoConnectDb {

    public void doConnect(GrapDbSource o) throws Exception{
        String url = "";
        if(o.getDbType().equals("mysql")){
            url = "jdbc:mysql://"+o.getDbIp()+":"+o.getPort()+"/"+o.getDbName()+"?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC";
        }else if(o.getDbType().equals("oracle")){
            url = "jdbc:oracle:thin:@"+o.getDbIp()+":"+o.getPort()+":"+o.getDbName();
        }else if(o.getDbType().equals("sqlserver")){
            url = "jdbc:sqlserver://"+o.getDbIp()+":"+o.getPort()+";DatabaseName="+o.getDbName();
        }
        String configName = o.getDbConfigName();//不能是main的名字
        if(DbKit.getConfig(configName)!=null){
        	DbKit.removeConfig(configName);
        }
        DruidPlugin dp = new DruidPlugin(url, o.getDbUserName(), o.getDbPassword());
        ActiveRecordPlugin arp = new ActiveRecordPlugin(configName,dp);
        arp.setDialect(new OracleDialect());  
        dp.start();
        arp.start();
        if(DbKit.getConfig(configName)==null){
        	DbKit.addConfig(arp.getConfig());
        }
    }
}
