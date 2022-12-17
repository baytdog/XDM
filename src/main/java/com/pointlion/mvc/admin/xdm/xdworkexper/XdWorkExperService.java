package com.pointlion.mvc.admin.xdm.xdworkexper;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN xd_employee e ON o.eid=e.id  where 1=1";
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;

		if(ShiroKit.getUserOrgId().equals("1")){

		}else{

		}
		/*if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}*/
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select o.*,e.name ", sql);
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
    	}
	}




    public List<XdWorkExper> getWorkExperList(String employeeId) {
//		String sql="select * from "+TABLE_NAME;
 		String sql="select * from "+TABLE_NAME+" where eid='"+employeeId+"'";
		List<XdWorkExper> list = XdWorkExper.dao.find(sql);
		return  list;
    }

	public File exportExcel(String path, String name, String empnum, String emprelation, String unitname, String costitem){

		String userId = ShiroKit.getUserId();

		String sql  = " from "+TABLE_NAME+" o   where 1=1";


		if (StrKit.notBlank(name)) {
			sql = sql + " and o.name like '%"+name +"%'";
		}

		if (StrKit.notBlank(empnum)) {
			sql = sql + " and o.empnum like '%"+empnum +"%'";
		}
		if (StrKit.notBlank(emprelation)) {
			sql = sql + " and o.emprelation like '%"+emprelation+"%'";
		}
		if (StrKit.notBlank(unitname)) {
			sql = sql + " and o.unitname like '%"+unitname+"%'";
		}

		if (StrKit.notBlank(costitem)) {
			sql = sql + " and o.status='"+costitem+"'";
		}
		sql = sql + " order by o.ctime desc";


		List<XdEmployee> list = XdEmployee.dao.find(" select * "+sql);//查询全部
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> first = new ArrayList<String>();
		first.add("员工编号");
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
		return file;
	}



}