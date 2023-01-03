package com.pointlion.mvc.admin.xdm.xdempcert;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class XdEmpCertService{
	public static final XdEmpCertService me = new XdEmpCertService();
	public static final String TABLE_NAME = XdEmpCert.tableName;
	
	/***
	 * query by id
	 */
	public XdEmpCert getById(String id){
		return XdEmpCert.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String dept,String name,String certTitle,String certAuth,String sny){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o where 1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.department = '"+dept+"'";
		}
		if(StrKit.notBlank(name)){
			sql = sql + " and o.ename like '%"+name+"%'";
		}
		if(StrKit.notBlank(certTitle)){
			sql = sql + " and o.certTile like '%"+certTitle+"%'";
		}
		if(StrKit.notBlank(certAuth)){
			sql = sql + " and o.certAuthName like '%"+certAuth+"%'";
		}
		if(StrKit.notBlank(sny)){
			sql = sql + " and o.sny ='"+sny+"'";
		}
		sql = sql + " order by o.ctime desc";
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
    		XdEmpCert o = me.getById(id);
    		o.delete();
    	}
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
							List<String> empCertStr = list.get(i);
							XdEmpCert empCert=new XdEmpCert();
							empCert.setId(UuidUtil.getUUID());
							empCert.setCtime(time);
							empCert.setCuser(user.getId());
							empCert.setStatus("1");
							if(empCertStr.get(1)==null ||"".equals(empCertStr.get(1).trim())){
								continue;
							}
							String orgName = empCertStr.get(1);
							SysOrg sysOrg = SysOrg.dao.findFirst("select * from sys_org where name='" + orgName + "'");
							empCert.setDepartment(sysOrg.getId());
							String name = empCertStr.get(2);
//							SysUser sysUser = SysUser.dao.findFirst("select * from sys_user  where name='" + name + "'");
							XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + name + "'");
							if(emp==null){
								empCert.setEname(name);
							}else{
								empCert.setEid(emp.getId());
								empCert.setEname(name);
							}
							String certName = empCertStr.get(3);
							XdCertificate certificate = XdCertificate.dao.findFirst("select * from xd_certificate where certificateTitle='" + certName + "'");
							if(certificate!=null){
								empCert.setCertTile(certName);
								empCert.setCertId(certificate.getId());
							}else{
								empCert.setCertTile(certName);
							}
							String level = empCertStr.get(4);
							String  levles="";
							if(level.contains("A1")){
								XdDict dict = XdDict.dao.findFirst("select * from xd_dict where  type ='certLevel' and name ='A1'");
								levles=levles+","+dict.getValue();
							}
							if(level.contains("A2")){
								XdDict dict = XdDict.dao.findFirst("select * from xd_dict where  type ='certLevel' and name ='A2'");
								levles=levles+","+dict.getValue();
							}
							if(level.contains("B1")){
								XdDict dict = XdDict.dao.findFirst("select * from xd_dict where  type ='certLevel' and name ='B1'");
								levles=levles+","+dict.getValue();
							}
							if(level.contains("B2")){
								XdDict dict = XdDict.dao.findFirst("select * from xd_dict where  type ='certLevel' and name ='B2'");
								levles=levles+","+dict.getValue();
							}
							if(level.contains("D")){
								XdDict dict = XdDict.dao.findFirst("select * from xd_dict where  type ='certLevel' and name ='D'");
								levles=levles+","+dict.getValue();
							}
							if(level.contains("E")){
								XdDict dict = XdDict.dao.findFirst("select * from xd_dict where  type ='certLevel' and name ='E'");
								levles=levles+","+dict.getValue();
							}
							if(level.contains("F")){
								XdDict dict = XdDict.dao.findFirst("select * from xd_dict where  type ='certLevel' and name ='F'");
								levles=levles+","+dict.getValue();

							}
							String levels = levles.replaceAll("^,","");
							empCert.setCertLevel(levels);
							String auth = empCertStr.get(5);
							XdDict licenseAuth = XdDict.dao.findFirst("select * from xd_dict where  type  = 'licenseauth' and name ='" + auth + "'");

							empCert.setCertAuthValue(Integer.valueOf(licenseAuth.getValue()));
							empCert.setCertAuthName(auth);
							String opendate = empCertStr.get(6);
							empCert.setOpenDate(opendate);
							String validity = empCertStr.get(7);
							empCert.setValidity(validity);
							if(empCertStr.get(8).contains("-")){
								empCert.setCloseDate(empCertStr.get(8));
							}else{
								try {
									if("长期".equals(validity)){
										empCert.setCloseDate("长期");
									}else{
										LocalDate parse = LocalDate.parse(opendate);
										LocalDate localDate = parse.plusMonths(Integer.valueOf(validity) * 12);
										LocalDate localDate1 = localDate.minusDays(1);
										empCert.setCloseDate(localDate1.toString());

									}
								} catch (NumberFormatException e) {
									e.printStackTrace();
									empCert.setCloseDate(empCertStr.get(8));
								}
							}

							//String closedate = empCertStr.get(8);

							//empCert.setCloseDate(closedate);
							String idnum = empCertStr.get(9);
							empCert.setIdnum(idnum);
							String certnum = empCertStr.get(10);
							empCert.setCertnum(certnum);
							String remark = empCertStr.get(11);
							empCert.setRemak(remark);
							String certstatus = empCertStr.get(12);
							if(certstatus!=null && !"".equals(certstatus)){
								if(certstatus.contains("复印件")){
									empCert.setCertstatus("0");
								}else{
									empCert.setCertstatus("1");
								}
							}
							String sny = empCertStr.get(16);
							empCert.setSny(sny);
							String sn = empCertStr.get(17);
							empCert.setSn(sn);
							empCert.save();
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

	public File exportExcel(String path, String name, String trainOrgname, String major, String edubg, String enrolldate, String graduatdate){

	/*	String userId = ShiroKit.getUserId();
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
		sql = sql + " order by o.ctime desc,o.eid";*/

	String sql=" select ct.certTile," +
			"sum(case ct.department when '4' then  ct.count else 0 end) '内场单元部'," +
			"sum(case ct.department when '2' then  ct.count else 0 end) '外场单元部'," +
			"sum(case ct.department when '3' then  ct.count else 0 end) '设施一部'," +
			"sum(case ct.department when '5' then ct.count else 0 end) '工程/设施二部'," +
			"sum(case ct.department when '887163179b6c4eaabd06c44e47e41f92' then  ct.count else 0 end) '职能部门'" +
			"from (select certTile,department,count(*)  as count from xd_emp_cert  where certTile is not null group by certTile,department) ct group by certTile" ;





		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> first = new ArrayList<String>();
		List<String> second = new ArrayList<String>();
		first.add("序号");
		first.add("证书");
		first.add("人数         项目");
		first.add("内场单元部");
		first.add("外场单元部");
		first.add("设施一部");
		first.add("工程/设施二部");
		first.add("职能部门");
		second.add("合计");
		second.add("");
		second.add("");
		second.add("");
		second.add("");
		second.add("");
		second.add("");
		second.add("");
		rows.add(first);
		rows.add(second);

		List<Record> records = Db.find(sql);
		for (int i = 0; i < records.size(); i++) {
			List<String> row = new ArrayList<String>();

			row.add(String.valueOf(i+1));
			row.add(records.get(i).get("certTile"));

			String ncdyb = records.get(i).getStr("内场单元部");
			String wcdyb = records.get(i).getStr("外场单元部");
			String ssyb = records.get(i).getStr("设施一部");
			String eb = records.get(i).getStr("工程/设施二部");
			String znbm = records.get(i).getStr("职能部门");
			row.add(String.valueOf(Integer.valueOf(ncdyb)+Integer.valueOf(wcdyb)+Integer.valueOf(ssyb)+Integer.valueOf(eb)+Integer.valueOf(znbm)));
			row.add(ncdyb);
			row.add(wcdyb);
			row.add(ssyb);
			row.add(eb);
			row.add(znbm);
			rows.add(row);
		}


		File file = ExcelUtil.empCertFile(path,rows);
		return file;
	}
	public File exportExcel(String path, String certTitle){

		String sql  = "select * from "+TABLE_NAME+" o   where 1=1";
		if(StrKit.notBlank(certTitle)){
			sql = sql + " and o.certTile like '%"+certTitle+"%'";
		}
		sql = sql + " order by o.ctime desc,o.eid";


		List<XdEmpCert> xdEmpCerts = XdEmpCert.dao.find(sql);



		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> first = new ArrayList<String>();
		List<String> second = new ArrayList<String>();
		if(xdEmpCerts==null){
			first.add(certTitle);
		}else{
			first.add(xdEmpCerts.get(0).getCertTile());
		}


		second.add("序号");
		second.add("部门");
		second.add("姓名");
		second.add("证书名称");
		second.add("证书等级");
		second.add("发证机关");
		second.add("发证日期");
		second.add("有效期/年");
		second.add("到期日");
		second.add("身份证号码");
		second.add("证书编号");
		second.add("备注");
		second.add("");
		rows.add(first);
		rows.add(second);
		for (int i = 0; i < xdEmpCerts.size(); i++) {
			List<String> row = new ArrayList<String>();
			XdEmpCert empCert = xdEmpCerts.get(i);
			row.add(String.valueOf(i+1));
			SysOrg sysOrg = SysOrg.dao.findFirst("select * from sys_org where id ='" + empCert.getDepartment() + "'");
			row.add(sysOrg.getName());
			row.add(empCert.getEname());
			row.add(empCert.getCertTile());
			String certLevel = empCert.getCertLevel();
			String levels="";
			if(certLevel!=null&& !certLevel.trim().equals("")){

				String[] split = certLevel.split(",");
				for (String s : split) {
					XdDict dict = XdDict.dao.findFirst("select * from xd_dict where type = 'certLevel' and value='" + s + "'");
					levels=levels+","+dict.getName();
				}
				levels = levels.replaceAll(",", "");
			}
			row.add(levels);
			row.add(empCert.getCertAuthName());
			row.add(empCert.getOpenDate());
			row.add(empCert.getValidity());
			row.add(empCert.getCloseDate());
			row.add(empCert.getIdnum());
			row.add(empCert.getCertnum());
			row.add(empCert.getRemak());
			String certstatus = empCert.getCertstatus();
			if(certstatus==null|| certstatus.equals("")){
				certstatus="";
			}else{
				if(certstatus.equals("1")){
					certstatus="√";
				}else{
					certstatus="纸质复印件";
				}
			}
			row.add(certstatus);

			rows.add(row);
		}



		File file = ExcelUtil.empCertByTitleFile(path,rows);
		return file;
	}
}