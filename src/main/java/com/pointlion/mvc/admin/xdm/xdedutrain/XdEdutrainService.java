package com.pointlion.mvc.admin.xdm.xdedutrain;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.model.XdEdutrain;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.XdOperUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import com.pointlion.util.DictMapping;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.SQLException;
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
		sql = sql + " order by o.ctime desc,o.eid,enrolldate desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	String empid="";
    	for(String id : idarr){
    		XdEdutrain o = me.getById(id);
			empid=o.getEid();
    		o.delete();
			XdOperUtil.logSummary(id,o, XdOperEnum.D.name(),XdOperEnum.UNAPPRO.name(),0);
    	}

    	XdOperUtil.updateEdu(empid);
	}

	public List<XdEdutrain> getEduTrainList(String  employeeId){
		//String sql="select * from "+TABLE_NAME;
		String sql="select * from "+TABLE_NAME+" where eid='"+employeeId+"' order by enrolldate desc";
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
		sql = sql + " order by o.ctime desc,o.eid,o.enrolldate desc";


		List<XdEdutrain> list = XdEdutrain.dao.find(" select * "+sql);//????????????
		Map<String,Integer> mapCount=new HashMap<>();
		Map<String,List<XdEdutrain>> mapObje=new HashMap<>();
		Map<String, Map<String, String>> dictMapping= DictMapping.dictMappingValueToName();
		Map<String, String> eudMap = dictMapping.get("edu");
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
		first.add("??????");
		second.add("");

		for (int i = 1; i <=maxLen ; i++) {
			first.add("??????/????????????"+i);
			second.add("????????????");
			second.add("????????????");
			second.add("??????/??????????????????");
			second.add("??????");
			second.add("??????");
			second.add("??????");
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
					row.add(edutrain.getEdubg()==null?"":eudMap.get(edutrain.getEdubg()));
					row.add(edutrain.getGraduatdate()==null?"":(edutrain.getGraduatdate().equals("0")?"?????????":"????????????"));
				}
				for (int i = 0; i < (maxLen - xdWorkExpers.size()); i++) {
					row.add("");
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



	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Map<String,String> orgMap= DictMapping.orgMapping("1");
		Map<String,String> projectsMap=DictMapping.projectsMapping();
		Map<String, Map<String, String>> dictMapping= DictMapping.dictMapping();
		Map<String, String> eudMap = dictMapping.get("edu");
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>1){
						SimpleUser user = ShiroKit.getLoginUser();
						String time = DateUtil.getCurrentTime();
						for(int i = 1;i<list.size();i++){//?????????????????????
							List<String> eduStr = list.get(i);
							XdEdutrain edutrain=new XdEdutrain();
							edutrain.setId(UuidUtil.getUUID());
							edutrain.setCtime(time);
							edutrain.setCuser(user.getId());
							if(eduStr.get(1)==null ||"".equals(eduStr.get(1).trim())){
								continue;
							}
							XdEmployee employee = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + eduStr.get(1) + "'");
							edutrain.setEid(employee.getId());
							edutrain.setEname(eduStr.get(1));
							edutrain.setEnrolldate(eduStr.get(2)==null?"":eduStr.get(2));
							edutrain.setGraduatdate(eduStr.get(3)==null?"":eduStr.get(3));
							edutrain.setTrainOrgname(eduStr.get(4)==null?"":eduStr.get(4));
							edutrain.setMajor(eduStr.get(5)==null?"":eduStr.get(5));
							edutrain.setEdubg(eduStr.get(6)==null?"":eudMap.get(eduStr.get(6)));
							edutrain.setGrade(eduStr.get(6)==null?"":(eduStr.get(6).equals("?????????")==true?"0":"1"));
							edutrain.save();

						}
						if(result.get("success")==null){
							result.put("success",true);//??????????????????
						}
					}else{
						result.put("success",false);//??????????????????
						message.add("excel????????????");
						result.put("message", StringUtils.join(message," "));
					}
					result.put("message",StringUtils.join(message," "));
					if((Boolean) result.get("success")){//??????????????????
						return true;
					}else{//??????
						return false;
					}
				}catch(Exception e){
					return false;
				}
			}
		});
		return result;
	}
}