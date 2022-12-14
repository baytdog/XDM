package com.pointlion.mvc.admin.xdm.xdworkexper;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.model.*;
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
import java.util.function.Consumer;
import java.util.stream.Stream;

public class XdWorkExperService{
	public static final XdWorkExperService me = new XdWorkExperService();
	public static final String TABLE_NAME = XdWorkExper.tableName;
	
	/***
	 * query by id
	 */
	public XdWorkExper getById(String id){
		return XdWorkExper.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String name, String workUnit, String job, String adrr, String entryDate, String dimissionDate){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;

		if(ShiroKit.getUserOrgId().equals("1")){

		}else{

		}
		if (StrKit.notBlank(name)) {
			sql = sql + " and o.ename like '%"+name +"%'";
		}
		if (StrKit.notBlank(workUnit)) {
			sql = sql + " and o.serviceunit like '%"+workUnit +"%'";
		}
		if (StrKit.notBlank(job)) {
			sql = sql + " and o.job like '%"+job +"%'";
		}
		if (StrKit.notBlank(adrr)) {
			sql = sql + " and o.addr like '%"+adrr+"%'";
		}
		if(StrKit.notBlank(entryDate)){
			sql = sql + " and o.entrydate='"+ entryDate+"'";
		}
		if (StrKit.notBlank(dimissionDate)) {
			sql = sql + " and o.departdate='"+dimissionDate+"'";
		}
		sql = sql + " order by o.ctime desc,ename,entrydate desc";
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
    		XdWorkExper o = me.getById(id);
    		o.delete();
			XdOperUtil.logSummary(id,o, XdOperEnum.D.name(),XdOperEnum.UNAPPRO.name(),0);
    	}
	}




    public List<XdWorkExper> getWorkExperList(String employeeId) {
//		String sql="select * from "+TABLE_NAME;
 		String sql="select * from "+TABLE_NAME+" where eid='"+employeeId+"' order by entrydate desc";
		List<XdWorkExper> list = XdWorkExper.dao.find(sql);
		return  list;
    }

	public File exportExcel(String path, String name, String workUnit, String job, String adrr, String entryDate, String dimissionDate){

		String userId = ShiroKit.getUserId();
		String sql  = "from "+TABLE_NAME+" o   where 1=1";
		if (StrKit.notBlank(name)) {
			sql = sql + " and o.ename like '%"+name +"%'";
		}
		if (StrKit.notBlank(workUnit)) {
			sql = sql + " and o.serviceunit like '%"+workUnit +"%'";
		}
		if (StrKit.notBlank(job)) {
			sql = sql + " and o.job like '%"+job +"%'";
		}
		if (StrKit.notBlank(adrr)) {
			sql = sql + " and o.addr like '%"+adrr+"%'";
		}
		if(StrKit.notBlank(entryDate)){
			sql = sql + " and o.entrydate='"+ entryDate+"'";
		}
		if (StrKit.notBlank(dimissionDate)) {
			sql = sql + " and o.departdate='"+dimissionDate+"'";
		}
		sql = sql + " order by o.ctime desc,o.eid";


		List<XdWorkExper> list = XdWorkExper.dao.find(" select * "+sql);//????????????
		Map<String,Integer> mapCount=new HashMap<>();
		Map<String,List<XdWorkExper>> mapObje=new HashMap<>();
		for (XdWorkExper workExper : list) {
			if(mapCount.get(workExper.getEid())==null){
				mapCount.put(workExper.getEid(),1);
				List<XdWorkExper> workExperList=new ArrayList<>();
				workExperList.add(workExper);
				mapObje.put(workExper.getEid(),workExperList);
			}else{
				Integer integer = mapCount.get(workExper.getEid());
				mapCount.put(workExper.getEid(),integer+1);
				List<XdWorkExper> workExperList = mapObje.get(workExper.getEid());
				workExperList.add(workExper);
				mapObje.put(workExper.getEid(),workExperList );
			}
		}

		Collection<Integer> values = mapCount.values();
		Stream<Integer> sorted = values.stream().sorted((o1,o2) ->  -o1.compareTo(o2));
		Integer maxLen = sorted.findFirst().get();
		System.out.println(maxLen);



		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> first = new ArrayList<String>();
		List<String> second = new ArrayList<String>();
		first.add("??????");
		second.add("");

		for (int i = 1; i <=maxLen ; i++) {
			first.add("????????????"+i);
			second.add("????????????");
			second.add("????????????");
			second.add("????????????");
			second.add("??????");
			second.add("??????");
		}

		rows.add(first);
		rows.add(second);
		Collection<List<XdWorkExper>> collWorkExper = mapObje.values();
		Stream<List<XdWorkExper>> sorted1 = collWorkExper.stream().sorted((o1, o2) -> -(o1.size() - o2.size()));

		sorted1.forEach(new Consumer<List<XdWorkExper>>() {
			@Override
			public void accept(List<XdWorkExper> xdWorkExpers) {
				List<String> row = new ArrayList<String>();

				for (int i = 0; i < xdWorkExpers.size(); i++) {
					XdWorkExper workExper = xdWorkExpers.get(i);
					if(i==0){
						row.add(workExper.getEname());
					}
					row.add(workExper.getEntrydate());
					row.add(workExper.getDepartdate());
					row.add(workExper.getServiceunit());
					row.add(workExper.getJob());
					row.add(workExper.getAddr());
				}

				for (int i = 0; i < (maxLen - xdWorkExpers.size()); i++) {
					row.add("");
					row.add("");
					row.add("");
					row.add("");
					row.add("");
				}

				rows.add(row);

			}
		});



			/*	first.add("????????????");
		first.add("??????");//0
		first.add("????????????");
		first.add("??????");//1
		first.add("????????????");//2
		first.add("??????");//3
		first.add("????????????");//3
		first.add("????????????");//3
		first.add("????????????");//3
		first.add("????????????");//3
		first.add("????????????");//3
		first.add("????????????");//3
		first.add("????????????");//3
		first.add("??????");//3
		first.add("????????????");//3
		first.add("????????????");//3
		first.add("????????????");//3
		first.add("??????");//3
		first.add("????????????");//3
		first.add("????????????");//3
		first.add("??????");//3
		first.add("????????????");//3
		first.add("??????");//3
		first.add("????????????");//3
		first.add("??????????????????");//3
		first.add("??????????????????");//3
		first.add("??????????????????");//3
		first.add("???????????????");//3
		first.add("???????????????");//3
		first.add("???????????????");//3
		first.add("??????");//3
		first.add("??????");//3
		first.add("?????????");//3
		first.add("??????");//3
		first.add("?????????");//3
		first.add("???????????????");//3
		first.add("????????????");//3
		first.add("??????????????????");//3
		first.add("??????????????????");//3
		first.add("??????????????????");//3
		first.add("????????????");//3
		first.add("????????????");//3
		first.add("??????????????????");//3
		first.add("??????????????????");//3
		first.add("??????????????????");//3
		first.add("????????????");//3
		first.add("??????");//3
		first.add("??????");//3
		first.add("???????????????");//3
		first.add("????????????");//3
		first.add("???????????????");//3
		first.add("????????????");//3
		first.add("??????");//3
		first.add("??????????????????");//3
		first.add("????????????");//3


		rows.add(first);
		for(XdEmployee emp:list){
			List<String> row = new ArrayList<String>();
			row.add(emp.getEmpnum());//0
			row.add(emp.getName());//0
			row.add(emp.getIdnum());//0
			row.add(emp.getGender().toString());//0
			row.add(emp.getDepartment()==null?"":emp.getDepartment().toString());//0
			row.add(emp.getUnitname()==null?"":emp.getUnitname().toString());//0
			row.add(emp.getCostitem()==null?"":emp.getCostitem().toString());//0
			row.add(emp.getEntrytime());//0
			row.add(emp.getPositivedate());//????????????
			row.add(emp.getDepartime());//0
			row.add(emp.getInductionstatus()==null?"":emp.getInductionstatus().toString());//0
			row.add(emp.getBirthday());//????????????
			row.add(emp.getSeniority());//0
			row.add(emp.getAge().toString());//0
			row.add(emp.getRetiretime());//0
			row.add("");//????????????
			row.add(emp.getEmprelation());
			row.add(emp.getPosition()==null?"":emp.getPosition().toString());//0
			row.add(emp.getWorkstation());//0
			row.add(emp.getTel());//0
			row.add(emp.getNational());//0
			row.add(emp.getPoliticsstatus());//0
			row.add(emp.getMarried()==null?"":emp.getMarried().toString());//0
			row.add(emp.getTopedu());//0
			row.add(emp.getEdubg1());//0
			row.add(emp.getSchool1());//0
			row.add(emp.getMajor1());//0
			row.add(emp.getEdubg2());//0
			row.add(emp.getSchool2());//0
			row.add(emp.getMajor2());//0
			row.add(emp.getTopdegree());//0
			row.add(emp.getCensusregister());//0
			row.add(emp.getBirthplace());//0
			row.add(emp.getNativeplace());//0
			row.add(emp.getPresentaddr());//0
			row.add(emp.getCensusregisteraddr());//0
			row.add(emp.getIssoldier()==null?"":emp.getIssoldier().toString());//0
			row.add(emp.getWorktime());//0
			row.add(emp.getContractstartdate());//0
			row.add(emp.getContractenddate());//0
			row.add(emp.getContractclauses()==null?"":emp.getContractclauses().toString());//0
			row.add(emp.getEmprelation());//0
			row.add(emp.getProtechgrade());//0
			row.add(emp.getProtechposts());//0
			row.add(emp.getVocaqualifilevel());//0
			row.add(emp.getVocaQualifilevel1());//0
			row.add(emp.getCertificates());
			row.add(emp.getSpecialty());
			row.add(emp.getEmcontact());
			row.add(emp.getBanaccount());//????????????
			row.add(emp.getFundaccount());//???????????????
			row.add(emp.getRecruitsource());
			row.add(emp.getSalary()==null?"":emp.getSalary().toString());
			row.add("");//????????????
			row.add("");//????????????
			rows.add(row);
		}
		File file = ExcelUtil.listToFile(path,rows);
		return file;*/
		File file = ExcelUtil.workExperFile(path,rows);
		return file;
	}
	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>1){
						SimpleUser user = ShiroKit.getLoginUser();
						String time = DateUtil.getCurrentTime();
						for(int i = 1;i<list.size();i++){//?????????????????????
							List<String> workStr = list.get(i);
							XdWorkExper workExper=new XdWorkExper();
							workExper.setId(UuidUtil.getUUID());
							workExper.setCtime(time);
							workExper.setCuser(user.getId());
							if(workStr.get(1)==null ||"".equals(workStr.get(1).trim())){
								continue;
							}
							XdEmployee employee = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + workStr.get(1) + "'");
							workExper.setEid(employee.getId());
							workExper.setEname(workStr.get(1));
							workExper.setServiceunit(workStr.get(2)==null?"":workStr.get(2));
							workExper.setJob(workStr.get(3)==null?"":workStr.get(3));
							workExper.setAddr(workStr.get(4)==null?"":workStr.get(4));
							workExper.setEntrydate(workStr.get(5)==null?"":workStr.get(5));
							workExper.setDepartdate(workStr.get(5)==null?"":workStr.get(5));
							workExper.save();

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