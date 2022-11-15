package com.pointlion.mvc.admin.oa.apply.usecar;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaApplyUseCar;

public class OaApplyUseCarService{
	public static final OaApplyUseCarService me = new OaApplyUseCarService();
	private static final String TABLE_NAME = OaApplyUseCar.tableName;
	
	/***
	 * 根据主键查询
	 */
	public OaApplyUseCar getById(String id){
		return OaApplyUseCar.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_ where 1=1 order by o.create_time desc";
		return Db.paginate(pnum, psize, " select o.*,p.PROC_DEF_ID_ defid ", sql);
	}
	
	/***
	 * 根据车id查询该车是否被借用
	 */
	public Boolean isInBorrow(String id){
		String sql = "select * from "+TABLE_NAME+" o where o.car_id='"+id+"' and o.status='1'";
		List<OaApplyUseCar> list = OaApplyUseCar.dao.find(sql);
		if(list!=null&&list.size()>0){
			return true;//有借用的
		}else{
			return false;//没有借用的
		}
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyUseCar o = me.getById(id);
    		o.delete();
    	}
	}
	
}