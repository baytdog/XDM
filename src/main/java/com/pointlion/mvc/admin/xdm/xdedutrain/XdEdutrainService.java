package com.pointlion.mvc.admin.xdm.xdedutrain;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdEdutrain;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

public class XdEdutrainService{
	public static final XdEdutrainService me = new XdEdutrainService();
	public static final String TABLE_NAME = XdEdutrain.tableName;
	
	/***
	 * query by id
	 */
	public XdEdutrain getById(String id){
		return XdEdutrain.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String name,String trainOrgname,String major,String edubg,String enrolldate,String graduatdate){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where 1=1";
	//	sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(name)){
			sql = sql + " and o.ename like '%"+name+"%'";
		}
		if(StrKit.notBlank(trainOrgname)){
			sql = sql + " and o.trainOrgname like '%"+trainOrgname+"%'";
		}
		if(StrKit.notBlank(major)){
			sql = sql + " and o.major like '%"+major+"%'";
		}
		if(StrKit.notBlank(edubg)){
			sql = sql + " and o.edubg like '%"+edubg+"%'";
		}
		if(StrKit.notBlank(enrolldate)){
			sql = sql + " and o.enrolldate = '"+enrolldate+"'";
		}
		if(StrKit.notBlank(graduatdate)){
			sql = sql + " and o.graduatdate = '"+graduatdate+"'";
		}
		sql = sql + " order by o.ctime desc,o.eid";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdEdutrain o = me.getById(id);
    		o.delete();
    	}
	}

	public List<XdEdutrain> getEduTrainList(String  employeeId){
		//String sql="select * from "+TABLE_NAME;
		String sql="select * from "+TABLE_NAME+" where eid='"+employeeId+"'";
		List<XdEdutrain> list = XdEdutrain.dao.find(sql);
		return  list;
	}


	public File exportExcel(String path, String name,String trainOrgname,String major,String edubg,String enrolldate,String graduatdate){

		String userId = ShiroKit.getUserId();
		String sql  = "from "+TABLE_NAME+" o   where 1=1";
		if(StrKit.notBlank(name)){
			sql = sql + " and o.ename like '%"+name+"%'";
		}
		if(StrKit.notBlank(trainOrgname)){
			sql = sql + " and o.trainOrgname like '%"+trainOrgname+"%'";
		}
		if(StrKit.notBlank(major)){
			sql = sql + " and o.major like '%"+major+"%'";
		}
		if(StrKit.notBlank(edubg)){
			sql = sql + " and o.edubg like '%"+edubg+"%'";
		}
		if(StrKit.notBlank(enrolldate)){
			sql = sql + " and o.enrolldate = '"+enrolldate+"'";
		}
		if(StrKit.notBlank(graduatdate)){
			sql = sql + " and o.graduatdate = '"+graduatdate+"'";
		}
		sql = sql + " order by o.ctime desc,o.eid";


		List<XdEdutrain> list = XdEdutrain.dao.find(" select * "+sql);//查询全部
		Map<String,Integer> mapCount=new HashMap<>();
		Map<String,List<XdEdutrain>> mapObje=new HashMap<>();
		for (XdEdutrain xdEdutrain : list) {
			if(mapCount.get(xdEdutrain.getEid())==null){
				mapCount.put(xdEdutrain.getEid(),1);
				List<XdEdutrain> workExperList=new ArrayList<>();
				workExperList.add(xdEdutrain);
				mapObje.put(xdEdutrain.getEid(),workExperList);
			}else{
				Integer integer = mapCount.get(xdEdutrain.getEid());
				mapCount.put(xdEdutrain.getEid(),integer+1);
				List<XdEdutrain> workExperList = mapObje.get(xdEdutrain.getEid());
				workExperList.add(xdEdutrain);
				mapObje.put(xdEdutrain.getEid(),workExperList );
			}
		}

		Collection<Integer> values = mapCount.values();
		Stream<Integer> sorted = values.stream().sorted((o1, o2) ->  -o1.compareTo(o2));
		Integer maxLen = sorted.findFirst().get();
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> first = new ArrayList<String>();
		List<String> second = new ArrayList<String>();
		first.add("姓名");
		second.add("");

		for (int i = 1; i <=maxLen ; i++) {
			first.add("教育/培训经历"+i);
			second.add("入学日期");
			second.add("毕业日期");
			second.add("学校/培训机构名称");
			second.add("专业");
			second.add("学历");
		}

		rows.add(first);
		rows.add(second);
		Collection<List<XdEdutrain>> collWorkExper = mapObje.values();
		Stream<List<XdEdutrain>> sorted1 = collWorkExper.stream().sorted((o1, o2) -> -(o1.size() - o2.size()));

		sorted1.forEach( xdWorkExpers->{
				List<String> row = new ArrayList<String>();
				for (int i = 0; i < xdWorkExpers.size(); i++) {
					XdEdutrain edutrain = xdWorkExpers.get(i);
					if(i==0){
						row.add(edutrain.getEname());
					}
					row.add(edutrain.getEnrolldate());
					row.add(edutrain.getGraduatdate());
					row.add(edutrain.getTrainOrgname());
					row.add(edutrain.getMajor());
					row.add(edutrain.getEdubg());
				}
				for (int i = 0; i < (maxLen - xdWorkExpers.size()); i++) {
					row.add("");
					row.add("");
					row.add("");
					row.add("");
					row.add("");
				}

				rows.add(row);

		});



		File file = ExcelUtil.workExperFile(path,rows);
		return file;
	}

}