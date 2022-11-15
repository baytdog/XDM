package com.pointlion.mvc.admin.sys.tool.weight;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.ToolWeight;
import com.pointlion.mvc.common.utils.office.word.WordExportUtil;
import com.pointlion.mvc.common.utils.ModelToMapUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ToolWeightService{
	public static final ToolWeightService me = new ToolWeightService();
	public static final String TABLE_NAME = ToolWeight.tableName;

	/***
	 * 根据主键查询
	 */
	public ToolWeight getById(String id){
		return ToolWeight.dao.findById(id);
	}

	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where o.userid='"+ShiroKit.getUserId()+"'  order by o.create_time desc";
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
			ToolWeight o = me.getById(id);
			o.delete();
		}
	}

	/***
	 * 导出
	 * @throws Exception
	 */
	public File export(String id, HttpServletRequest request) throws Exception{
		ToolWeight o = ToolWeight.dao.findById(id);
		o.setWeight1(o.getWeight1()+"kg");
		o.setWeight2(o.getWeight2()+"kg");
		o.setWeight3(o.getWeight3()+"kg");
		o.setBigWeight(o.getBigWeight()+"公斤");
		SimpleDateFormat sdfTime = new SimpleDateFormat( "yyyy年MM月dd日 HH:mm");
		String date = sdfTime.format(new Date());
		String path = request.getSession().getServletContext().getRealPath("")+"/WEB-INF/admin/sys/tool/weight/";
		String templateUrl = path +"weight.docx";
		String exportURL = path+o.getId()+".docx";
		Map<String , Object > data = ModelToMapUtil.ModelToMap2(o);
		data.put("weight",data.get("big_weight"));
		data.put("date",date);
		WordExportUtil.export(data, templateUrl, exportURL);
		File file = new File(exportURL);
		if(file.exists()){
			return file;
		}else{
			return null;
		}
	}

}