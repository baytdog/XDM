package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdWorkExper;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;

@SuppressWarnings("serial")
public class XdWorkExper extends BaseXdWorkExper<XdWorkExper> {
	public static final XdWorkExper dao = new XdWorkExper();
	public static final String tableName = "xd_work_exper";

	/***
	 * query by id
	 */
	public XdWorkExper getById(String id){
		return XdWorkExper.dao.findById(id);
	}

	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
		String idarr[] = ids.split(",");
		for(String id : idarr){
			XdWorkExper o = XdWorkExper.dao.getById(id);
			o.delete();
		}
	}

	public void save(XdWorkExper workExper ){
		workExper.setId(UuidUtil.getUUID());
		//workExper.setEid(o.getId());
		workExper.setEntrydate(workExper.getEntrydate().length()>9?workExper.getEntrydate().substring(0,10):"");
		workExper.setDepartdate(workExper.getDepartdate().length()>9?workExper.getDepartdate().substring(0,10):"");
		workExper.setCtime(DateUtil.getCurrentTime());
		workExper.setCuser(ShiroKit.getUserId());
		workExper.save();
	}

	public void loadObj(XdWorkExper workExper){
		workExper.setId(UuidUtil.getUUID());
		//workExper.setEid(o.getId());
		workExper.setEntrydate(workExper.getEntrydate().length()>9?workExper.getEntrydate().substring(0,10):"");
		workExper.setDepartdate(workExper.getDepartdate().length()>9?workExper.getDepartdate().substring(0,10):"");
		workExper.setCtime(DateUtil.getCurrentTime());
		workExper.setCuser(ShiroKit.getUserId());
	}

}