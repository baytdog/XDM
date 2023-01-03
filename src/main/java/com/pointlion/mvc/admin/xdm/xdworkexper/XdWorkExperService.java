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


		List<XdWorkExper> list = XdWorkExper.dao.find(" select * "+sql);//查询全部
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
		first.add("姓名");
		second.add("");

		for (int i = 1; i <=maxLen ; i++) {
			first.add("工作经历"+i);
			second.add("入职日期");
			second.add("离职日期");
			second.add("服务公司");
			second.add("职务");
			second.add("地点");
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



			/*	first.add("员工编号");
		first.add("姓名");//0
		first.add("身份证号");
		first.add("性别");//1
		first.add("所属部门");//2
		first.add("单元");//3
		first.add("成本项目");//3
		first.add("入职日期");//3
		first.add("转正日期");//3
		first.add("离职日期");//3
		first.add("就职状态");//3
		first.add("出生日期");//3
		first.add("在职工龄");//3
		first.add("年龄");//3
		first.add("退休日期");//3
		first.add("退休状态");//3
		first.add("员工性质");//3
		first.add("职位");//3
		first.add("工作岗位");//3
		first.add("联系方式");//3
		first.add("民族");//3
		first.add("政治面貌");//3
		first.add("婚姻");//3
		first.add("最高学历");//3
		first.add("学历非全日制");//3
		first.add("学校非全日制");//3
		first.add("专业非全日制");//3
		first.add("学历全日制");//3
		first.add("学校全日制");//3
		first.add("专业全日制");//3
		first.add("学位");//3
		first.add("户籍");//3
		first.add("出生地");//3
		first.add("籍贯");//3
		first.add("现住址");//3
		first.add("户口所在地");//3
		first.add("退伍军人");//3
		first.add("参加工作时间");//3
		first.add("合同起始日期");//3
		first.add("合同结束日期");//3
		first.add("合同期数");//3
		first.add("合同性质");//3
		first.add("专业技术等级");//3
		first.add("专业技术职务");//3
		first.add("职业资格等级");//3
		first.add("职业资格");//3
		first.add("证书");//3
		first.add("特长");//3
		first.add("紧急联系人");//3
		first.add("银行账号");//3
		first.add("公积金账号");//3
		first.add("招聘来源");//3
		first.add("薪资");//3
		first.add("薪资变动状况");//3
		first.add("调职记录");//3


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
			row.add(emp.getPositivedate());//转正日期
			row.add(emp.getDepartime());//0
			row.add(emp.getInductionstatus()==null?"":emp.getInductionstatus().toString());//0
			row.add(emp.getBirthday());//出生日期
			row.add(emp.getSeniority());//0
			row.add(emp.getAge().toString());//0
			row.add(emp.getRetiretime());//0
			row.add("");//退休状态
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
			row.add(emp.getBanaccount());//银行账号
			row.add(emp.getFundaccount());//公积金账号
			row.add(emp.getRecruitsource());
			row.add(emp.getSalary()==null?"":emp.getSalary().toString());
			row.add("");//薪资变动
			row.add("");//调职记录
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
						for(int i = 1;i<list.size();i++){//从第二行开始取
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
							result.put("success",true);//正常执行完毕
						}
					}else{
						result.put("success",false);//正常执行完毕
						message.add("excel中无内容");
						result.put("message", StringUtils.join(message," "));
					}
					result.put("message",StringUtils.join(message," "));
					if((Boolean) result.get("success")){//正常执行完毕
						return true;
					}else{//回滚
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