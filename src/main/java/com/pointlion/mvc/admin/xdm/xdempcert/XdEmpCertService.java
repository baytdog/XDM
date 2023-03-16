package com.pointlion.mvc.admin.xdm.xdempcert;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.XdOperUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public Page<Record> getPage(int pNum,int pSize,String dept,String name,String certTitle,String certAuth,String sny,String year,String month,String ctime){
//		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o where status='1'";
		if(StrKit.notBlank(dept)){
			String deptIds="";
			for (String deptId : dept.split(",")) {
				deptIds=deptIds+"'"+deptId+"'"+",";
			}
			deptIds=deptIds.replaceAll(",$","");
			sql = sql + " and o.department in ("+deptIds+")";
		}
		if(StrKit.notBlank(name)){
			sql = sql + " and o.ename like '%"+name+"%'";
		}
		if(StrKit.notBlank(certTitle)){
			sql = sql + " and o.certid = '"+certTitle+"'";
		}
		if(StrKit.notBlank(certAuth)){
			sql = sql + " and o.certauthvalue = '"+certAuth+"'";
		}
		if(StrKit.notBlank(sny)){
			sql = sql + " and o.sny ='"+sny+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.sny  like '"+year+"年"+"%'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.sny  like '%"+month+"月"+"'";
		}
		if(StrKit.notBlank(ctime)){
			sql = sql + " and o.ctime  like '"+ctime+"%'";
		}
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pNum, pSize, " select * ", sql);
	}


	public Page<Record> getPage(int pNum,int pSize,String dept,String sny){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where status='1' and o.closeDate  is not null and (TO_DAYS(str_to_date(o.closeDate, '%Y-%m-%d')) - TO_DAYS(now()))<180";

		if(StrKit.notBlank(dept)){
			sql = sql + " and o.department = '"+dept+"'";
		}
		if(StrKit.notBlank(sny)){
			sql = sql + " and o.sny ='"+sny+"'";
		}

		sql = sql + " order by o.closeDate ";
		return Db.paginate(pNum, pSize, "  select o.*, TO_DAYS(str_to_date(o.closeDate, '%Y-%m-%d')) - TO_DAYS(now()) diffdate ,o.closeDate endtime", sql);
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
			XdOperUtil.updateEmpCert(o);

			XdCertLog log =new XdCertLog();

			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
			String[] ymd = sdf.format(new Date()).split("-");
			log.setYear(Integer.valueOf(ymd[0]));
			log.setMonth(Integer.valueOf(ymd[1]));
			log.setEid(o.getEid());
			log.setEname(o.getEname());
			log.setCertTitle(o.getCertTile());
			log.setNum(1);
			log.setDeptId(o.getDepartment());
			SysOrg org = SysOrg.dao.findById(o.getDepartment());
			log.setDeptName(org==null?"":org.getName());
			log.setLogType("删除");
			log.setCreateDate(DateUtil.getCurrentTime());
			log.setCreateUser(ShiroKit.getUserId());
			log.save();

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
						Map<String,String> certLevelMap=new HashMap<>();
						XdDict.dao.find("select * from xd_dict where  type ='certLevel' ")
								.stream().forEach(xdDict->certLevelMap.put(xdDict.getName(),xdDict.getValue()));
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

							String  levels= dealCertLevel(level,certLevelMap);

							empCert.setCertLevel(levels.replaceAll("^,",""));
							String auth = empCertStr.get(5);
							XdDict licenseAuth = XdDict.dao.findFirst("select * from xd_dict where  type  = 'licenseauth' and name ='" + auth + "'");
							if(licenseAuth!=null){
								empCert.setCertAuthValue(Integer.valueOf(licenseAuth.getValue()));
								empCert.setCertAuthName(auth);
							}else {
								empCert.setCertAuthName(auth);
							}
//							XdOperUtil.numToDateFormat(empStr.get(16))

							String opendate = empCertStr.get(6);
							empCert.setOpenDate(opendate);

							String validity = empCertStr.get(7);
							empCert.setValidity(validity);
							String cell8 = empCertStr.get(8);
							if(!"".equals(cell8)){
								if(!cell8.contains("-") && !containChinese(cell8)){
									cell8=	XdOperUtil.numToDateFormat(cell8);
								}
							}
							String cell9 = empCertStr.get(9);
							if(!"".equals(cell9)){
								if(cell9.contains("-") || containChinese(cell9)){
									cell8=cell9;
								}else{
									cell9=	XdOperUtil.numToDateFormat(cell9);
									cell8=cell9;
								}

							}
							empCert.setCloseDate(cell8);
							empCert.setValidateDate(cell9);
							/*if(empCertStr.get(8).contains("-")){
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
*/
							//String closedate = empCertStr.get(8);

							//empCert.setCloseDate(closedate);
							String idnum = empCertStr.get(10);
							empCert.setIdnum(idnum);
							String certnum = empCertStr.get(11);
							empCert.setCertnum(certnum);
							String holder = empCertStr.get(12);
							if(!"".equals(holder)){
								if("员工持有".equals(holder)){
									holder="0";
								}else{
									holder="1";
								}
							}
							empCert.setHolder(holder);
							String remark = empCertStr.get(13);
							empCert.setRemak(remark);
							/*String certstatus = empCertStr.get(12);
							if(certstatus!=null && !"".equals(certstatus)){
								if(certstatus.contains("复印件")){
									empCert.setCertstatus("0");
								}else{
									empCert.setCertstatus("1");
								}
							}*/
							if(cell8.contains("-")){
								String[] ymd = cell8.split("-");

								empCert.setSny(ymd[0]+"年"+ymd[1]+"月");
							}else{
								empCert.setSny(cell8);
							}
							/*String sny = empCertStr.get(16);
							empCert.setSny(sny);*/
							/*String sn = empCertStr.get(17);
							empCert.setSn(sn);*/
							empCert.save();
							if(emp!=null){
								String certificates = emp.getCertificates();
								if(certificates!=null && !"".equals(certificates)){
									if(!certificates.contains(empCert.getCertTile())){
										certificates=certificates+"、"+empCert.getCertTile();
									}
								}else{
									certificates=empCert.getCertTile();
								}
								emp.setCertificates(certificates);
								emp.update();
							}



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

	private boolean  containChinese(String str){
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		String s = "adbdi地老天荒work";
		Matcher m = p.matcher(str);
	 return m.find();
	}

	private String dealCertLevel(String level,Map<String,String> certLevelMap) {
		String levles = "";
		if(level.contains("A1")){
			levles=levles+","+certLevelMap.get("A1");
		}
		if(level.contains("A2")){
			levles=levles+","+certLevelMap.get("A2");
		}
		if(level.contains("B1")){
			levles=levles+","+certLevelMap.get("B1");
		}
		if(level.contains("B2")){
			levles=levles+","+certLevelMap.get("B2");
		}
		if(level.contains("D")){
			levles=levles+","+certLevelMap.get("D");
		}
		if(level.contains("E")){
			levles=levles+","+certLevelMap.get("E");
		}
		if(level.contains("F")){
			levles=levles+","+certLevelMap.get("F");
		}
		if(level.contains("级")){
			if(level.contains("/")){
				String[] split = level.split("/");
				for (String s : split) {
					levles=levles+","+certLevelMap.get(s);
				}

			}else{
				levles=certLevelMap.get(level);
			}

		}
		return levles;
	}

	public File exportExcel(String path){


	String sql=" select ct.certTile," +
			"sum(case ct.name when '内场单元部' then  ct.count else 0 end) '内场单元部'," +
			"sum(case ct.name when '外场单元部' then  ct.count else 0 end) '外场单元部'," +
			"sum(case ct.name when '设施一部' then  ct.count else 0 end) '设施一部'," +
			"sum(case ct.name when '工程/设施二部' then ct.count else 0 end) '工程/设施二部'," +
			"sum(case ct.name when '职能部门' then  ct.count else 0 end) '职能部门'" +
			"from (SELECT c.certTile,o.`name`,count(*) AS count " +
			"FROM xd_emp_cert c left join sys_org o " +
			" on c.department=o.id" +
			" WHERE certTile IS NOT NULL " +
			" GROUP BY c.certTile,o.`name`) ct group by certTile" ;

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

		String sql  = "select * from "+TABLE_NAME+" o   where status='1'";
		if(StrKit.notBlank(certTitle)){
			sql = sql + " and o.certId = '"+certTitle+"'";
		}
		sql = sql + " order by certTile,o.department";


		List<XdEmpCert> xdEmpCerts = XdEmpCert.dao.find(sql);



		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> first = new ArrayList<String>();
		List<String> second = new ArrayList<String>();
		if(StrKit.notBlank(certTitle)){
			XdCertificate certType = XdCertificate.dao.findById(certTitle);
			first.add(certType.getCertificateTitle());
		}else{
			first.add("公司所有证书");
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
		second.add("审证复证/继续教育\n" +
				"时间");
		second.add("身份证号码");
		second.add("证书编号");
		second.add("备注1");
		second.add("备注2");
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
			row.add(empCert.getValidateDate());
			row.add(empCert.getIdnum());
			row.add(empCert.getCertnum());
			String holder = empCert.getHolder();
			if(holder==null){
				row.add("");
			}else{
				row.add("0".equals(holder)?"员工持有":"公司持有");
			}

			/*String certstatus = empCert.getCertstatus();
			if(certstatus==null|| certstatus.equals("")){
				certstatus="";
			}else{
				if(certstatus.equals("1")){
					certstatus="√";
				}else{
					certstatus="纸质复印件";
				}
			}
			row.add(certstatus);*/

			row.add(empCert.getRemak());

			rows.add(row);
		}



		File file = ExcelUtil.empCertByTitleFile(path,rows);
		return file;
	}
}