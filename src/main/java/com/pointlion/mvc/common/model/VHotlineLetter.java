package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseVHotlineLetter;
@SuppressWarnings("serial")
public class VHotlineLetter extends BaseVHotlineLetter<VHotlineLetter> {
	public static final VHotlineLetter dao = new VHotlineLetter();
	public static final String tableName = "v_hotline_letter";
	
	/***
	 * query by id
	 */
	public VHotlineLetter getById(String id){
		return VHotlineLetter.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		VHotlineLetter o = VHotlineLetter.dao.getById(id);
    		o.delete();
    	}
	}
	
}