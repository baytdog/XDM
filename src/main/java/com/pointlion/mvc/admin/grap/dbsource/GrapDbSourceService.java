package com.pointlion.mvc.admin.grap.dbsource;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.GrapDbSource;

public class GrapDbSourceService{
	public static final GrapDbSourceService me = new GrapDbSourceService();
	public static final String TABLE_NAME = GrapDbSource.tableName;
	
	/***
	 * 根据主键查询
	 */
	public GrapDbSource getById(String id){
		return GrapDbSource.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<GrapDbSource> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o order by o.create_time desc";
		return GrapDbSource.dao.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		GrapDbSource o = me.getById(id);
    		o.delete();
    	}
	}
	
	
	public Boolean testConnect(GrapDbSource o) throws Exception{
		Boolean r = false;
        String dbtype = o.getDbType();//数据库类型
        String serverIp = o.getDbIp();//数据库ip地址
        String dbname = o.getDbName();//数据库名
        String username = o.getDbUserName();//用户名称
        String password = o.getDbPassword();//密码
        String port = o.getPort();//端口
        if(StringUtils.isBlank(dbtype)){
            throw new Exception("没有正确的数据库类型！");
        }else if(StringUtils.isBlank(serverIp)){
        	throw new Exception("没有填写ip地址！");
        }else if(StringUtils.isBlank(dbname)){
        	throw new Exception("没有填写数据库名称！");
        }else if(StringUtils.isBlank(username)){
        	throw new Exception("没有填写用户名！");
        }else if(StringUtils.isBlank(password)){
        	throw new Exception("没有填写密码！");
        }else if(StringUtils.isBlank(port)){
        	throw new Exception("没有填写端口！");
        }else{
        	try{
                DoConnectDb cn = new DoConnectDb();
                cn.doConnect(o);
                r = true;
        	}catch(Exception e1){
            	e1.printStackTrace();
            	r = false;
            }
        }
        return r;
	}
	
	/***
	 * 断开连接
	 * @param id
	 */
	public void disConnect(GrapDbSource db){
    	DbKit.removeConfig(db.getDbConfigName());
	}
}