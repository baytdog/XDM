package com.pointlion.mvc.admin.sys.attachment;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.SysAttachment;

import java.util.List;
import java.util.function.Consumer;

public class AttachmentService{
	public static final AttachmentService me = new AttachmentService();
	public static final String TABLE_NAME = SysAttachment.tableName;
	
	/***
	 * 根据主键查询
	 */
	public SysAttachment getById(String id){
		return SysAttachment.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(String busid,int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o where 1=1 ";
		if(StrKit.notBlank(busid)){
			sql = sql + " and o.business_id='"+busid+"' order by create_time desc";
		}
		return Db.paginate(pnum, psize, " select * ", sql);
	}

	/**
	 * @Method getPage
	 * @param busids:
	 * @param pnum:
	 * @param psize:
	 * @Date 2023/1/3 16:36
	 * @Description  员工获取证件信息
	 * @Author king
	 * @Version  1.0
	 * @Return com.jfinal.plugin.activerecord.Page<com.jfinal.plugin.activerecord.Record>
	 */
	public Page<Record> getPage(List<String> busids, int pnum, int psize){
		String sql  = " from "+TABLE_NAME+" o where 1=1 ";
		if(busids.size()>0){
			String inIds="";
			for (String busid : busids) {
				inIds+="'"+busid+"',";
			}
			String in = inIds.replaceAll(",$", "");
			sql=sql+" and  o.business_id in ("+in+") order by o.create_time desc";
		}else{
			sql = sql + " and 1=2";
		}
		return Db.paginate(pnum, psize, " select * ", sql);
	}


	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		SysAttachment o = me.getById(id);
    		o.delete();
    	}
	}
	
	/***
	 * 获取附件数量
	 */
	public Integer getBusinessAttachmentCount(String busid){
		Record r = Db.findFirst("select count(*) c from sys_attachment a where a.business_id='"+busid+"'");
		Integer c = r.getInt("c");
		return c;
	}
	
}