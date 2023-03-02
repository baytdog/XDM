package com.pointlion.mvc.admin.xdm.xdtotaltimeonline;

import cn.hutool.poi.excel.ExcelWriter;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.util.CheckAttendanceUtil;
import com.pointlion.util.DictMapping;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class XdTotalTimeOnlineService{
	public static final XdTotalTimeOnlineService me = new XdTotalTimeOnlineService();
	public static final String TABLE_NAME = XdTotalTimeOnline.tableName;
	
	/***
	 * query by id
	 */
	public XdTotalTimeOnline getById(String id){
		return XdTotalTimeOnline.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pNum,int pSize,String days){
		Db.delete("delete from xd_total_time_online");
		Db.delete("delete from  xd_total_online_shifts");
		if("".equals(days)){
			days=DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());
		}
		String[] ymd = days.split("-");
		String ym=ymd[0]+ymd[1];
		List<XdAttendanceSummary> xdAttendanceSummaries = XdAttendanceSummary.dao.find("select * from  xd_attendance_summary " +
				"where schedule_year_month='" + ym + "' and day" + ymd[2] + " !=''");

		Map<String ,int[]> mapCount=new HashMap<>();

		Map<String ,String[]> mapShifts=new HashMap<>();
		Class<? super XdAttendanceSummary> superclass = XdAttendanceSummary.class.getSuperclass();
		Map<String, XdShift> stringXdShiftMap = CheckAttendanceUtil.shfitsMap();
		int[] countArr=null;
		String [] shiftsArr=null;
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Long shiftStart=0L;
		Long shiftEnd=0L;
		Long start=0L;
		Long end= 0L;
		String nextDay = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.parse(days).plusDays(1));
		for (XdAttendanceSummary summary : xdAttendanceSummaries) {
			if (mapCount.get(summary.getDeptName() + "," + summary.getProjectName()) == null) {
				countArr = new int[25];
				mapCount.put(summary.getDeptName() + "," + summary.getProjectName(), countArr);
				shiftsArr=new String[25];
				mapShifts.put(summary.getDeptName() + "," + summary.getProjectName(), shiftsArr);

			} else {
				countArr = mapCount.get(summary.getDeptName() + "," + summary.getProjectName());
				shiftsArr=mapShifts.get(summary.getDeptName() + "," + summary.getProjectName());
			}
			try {
				Method method = superclass.getMethod("getDay" + ymd[2]);
				String shiftName = (String) method.invoke(summary);
				XdShift shift = stringXdShiftMap.get(shiftName);

				if (shift.getBusitime() != null && !"".equals(shift.getBusitime())) {
					if ("1".equals(shift.getSpanDay())) {

						shiftStart=sdf.parse(days+" "+shift.getBusitime()).getTime();
						shiftEnd=sdf.parse(nextDay+" "+shift.getUnbusitime()).getTime();

					}else{
						shiftStart=sdf.parse(days+" "+shift.getBusitime()).getTime();
						shiftEnd=sdf.parse(days+" "+shift.getUnbusitime()).getTime();
					}




					for (int i = 0; i <24 ; i++) {
						int next=i+1;
						start=sdf.parse(days+" "+i+":00").getTime();
						end=sdf.parse(days+" "+next+":00").getTime();
						if(i==23){
							end=sdf.parse(nextDay+" "+"0:00").getTime();

						}
						if(start>=shiftStart && end<=shiftEnd){
							countArr[i]+=1;
							if(shiftsArr[i]==null){
								shiftsArr[i]=shiftName;
							}else{
								if(!shiftsArr[i].contains(shiftName)){
									shiftsArr[i]=shiftsArr[i]+","+shiftName;
								}
							}
						}
					}


				}

			} catch (Exception e) {
				e.printStackTrace();
			}


		}

		Class<? super XdTotalTimeOnline> clazz = XdTotalTimeOnline.class.getSuperclass();

		Map<String ,XdTotalTimeOnline> totalMap=new HashMap<>();
		XdTotalTimeOnline total=null;
		int count=1;
		if(!mapCount.isEmpty()){
			Set<String> set = mapCount.keySet();
			for (String key : set) {
				XdTotalTimeOnline online=new XdTotalTimeOnline();
				online.setId(UuidUtil.getUUID());
				online.setDeptName(key.split(",")[0]);
				online.setProjectName(key.split(",")[1]);

				if(totalMap.get(key.split(",")[0])==null){
					total=new XdTotalTimeOnline();
					total.setId(UuidUtil.getUUID());
					total.setDeptName(key.split(",")[0]);
					total.setProjectName("合计");
					totalMap.put(key.split(",")[0],total);
				}else{
					total=totalMap.get(key.split(",")[0]);
				}

				int[] ints = mapCount.get(key);
				for (int i = 0; i < ints.length; i++) {
					int suffix=i+1;
					try {
						Method method = clazz.getMethod("setTime" + suffix, Integer.class);
						method.invoke(online,ints[i]);

						Method getMethod =	clazz.getMethod("getTime" + suffix);
						Integer old = (Integer) getMethod.invoke(total)==null?0:(Integer) getMethod.invoke(total);
						method.invoke(total,old+ints[i]);

					} catch (Exception e) {
						e.printStackTrace();
					}


				}
				online.setCreateTime(DateUtil.getCurrentTime());
				online.setSortnum(count);
				online.save();
				count++;
//				total.save();

			}
		}


		Set<String> setTotal = totalMap.keySet();
		for (String s : setTotal) {
			XdTotalTimeOnline online = totalMap.get(s);
			online.setCreateTime(DateUtil.getCurrentTime());
			online.setSortnum(count);
			online.save();
			count++;
		}

		Class<? super XdTotalOnlineShifts> clazz1 = XdTotalOnlineShifts.class.getSuperclass();
		Map<String ,XdTotalOnlineShifts> totalShiftsMap=new HashMap<>();
		XdTotalOnlineShifts totalShifts=null;
		if(!mapShifts.isEmpty()){
			Set<String> set = mapShifts.keySet();
			for (String key : set) {
				XdTotalOnlineShifts online=new XdTotalOnlineShifts();
				online.setId(UuidUtil.getUUID());
				online.setDeptName(key.split(",")[0]);
				online.setProjectName(key.split(",")[1]);

				if(totalShiftsMap.get(key.split(",")[0])==null){
					totalShifts=new XdTotalOnlineShifts();
					totalShifts.setId(UuidUtil.getUUID());
					totalShifts.setDeptName(key.split(",")[0]);
					totalShifts.setProjectName("合计");
					totalShiftsMap.put(key.split(",")[0],totalShifts);
				}else{
					totalShifts=totalShiftsMap.get(key.split(",")[0]);
				}

				String[] shifts = mapShifts.get(key);
				System.out.println("============="+shifts.toString());
				for (int i = 0; i < shifts.length; i++) {
					int suffix=i+1;
					try {
						Method method = clazz1.getMethod("setShifts" + suffix, String.class);
						method.invoke(online,shifts[i]);
						Method getMethod =	clazz1.getMethod("getShifts" + suffix);
						String val = (String) getMethod.invoke(totalShifts)==null?"":(String) getMethod.invoke(totalShifts);
						String shiftVal = shifts[i] == null ? "" : shifts[i];
						if(!"".equals(shiftVal)){
							val=val+","+shiftVal;
						}

						Set<String> shiftSet =new HashSet<>();
						shiftSet.addAll(Arrays.asList(val.split(",")));
						String terminal="";
						for (String s : shiftSet) {
							if(!"".equals(s)){

								terminal=terminal+","+s;
							}
						}

						method.invoke(totalShifts,terminal.replaceAll("^,",""));


					} catch (Exception e) {
						e.printStackTrace();
					}


				}
				online.save();
			//	totalShifts.save();

			}
		}
		Set<String> setShift = totalShiftsMap.keySet();
		for (String s : setShift) {
			XdTotalOnlineShifts totalOnlineShifts = totalShiftsMap.get(s);

			totalOnlineShifts.save();
		}


		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o    where 1=1";


		sql = sql + " order by o.dept_name,sortnum";
		return Db.paginate(pNum, pSize, " select * ", sql);
	}


	public Page<Record> getPage(int pNum,int pSize,String days,String hours,String min,String endHours,String endMin){
		Db.delete("delete from xd_total_time_online");
		Db.delete("delete from  xd_total_online_shifts");
		if("".equals(days)){
			days=DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());
		}
		String[] ymd = days.split("-");
		String ym=ymd[0]+ymd[1];
		List<XdAttendanceSummary> xdAttendanceSummaries = XdAttendanceSummary.dao.find("select * from  xd_attendance_summary " +
				"where schedule_year_month='" + ym + "' and day" + ymd[2] + " !=''");

		if(StrKit.notBlank(min)){
			min="00";
		}
		if(StrKit.notBlank(endMin)){
			endMin="00";
		}


		Map<String ,Integer> mapCount=new HashMap<>();

		Map<String ,String> mapShifts=new HashMap<>();

		Class<? super XdAttendanceSummary> superclass = XdAttendanceSummary.class.getSuperclass();
		Map<String, XdShift> stringXdShiftMap = CheckAttendanceUtil.shfitsMap();


		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Long shiftStart=0L;
		Long shiftEnd=0L;
		Long start=0L;
		Long end= 0L;
		String nextDay = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.parse(days).plusDays(1));
		String keys="";
		for (XdAttendanceSummary summary : xdAttendanceSummaries) {

			keys=summary.getDeptName() + "," + summary.getProjectName();
			if (mapCount.get(keys) == null) {
				mapCount.put(keys, 0);
				mapShifts.put(keys, "");
			}

			try {
				Method method = superclass.getMethod("getDay" + ymd[2]);
				String shiftName = (String) method.invoke(summary);
				XdShift shift = stringXdShiftMap.get(shiftName);

				if (shift.getBusitime() != null && !"".equals(shift.getBusitime())) {
					shiftStart=sdf.parse(days+" "+shift.getBusitime()).getTime();
					if ("1".equals(shift.getSpanDay())) {
						shiftEnd=sdf.parse(nextDay+" "+shift.getUnbusitime()).getTime();
					}else{
						shiftEnd=sdf.parse(days+" "+shift.getUnbusitime()).getTime();
					}
					if(StrKit.notBlank(endHours)){
							start=sdf.parse(days+" "+hours+":"+min).getTime();
							end=sdf.parse(days+" "+endHours+":"+endMin).getTime();
					}else{
							start=sdf.parse(days+" "+hours+":"+min).getTime();
							end=start;
					}

					if(start>=shiftStart && end<=shiftEnd){
						mapCount.put(keys,mapCount.get(keys)+1);
						String shiftStr = mapShifts.get(keys);
						if(shiftStr.equals("")){
							mapShifts.put(keys,shiftName.trim());
						}else{
							if(!shiftStr.contains(shiftName.trim())){
								mapShifts.put(keys,shiftStr+","+shiftName.trim());
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}


		}


		Map<String ,XdTotalTimeOnline> totalMap=new HashMap<>();
		XdTotalTimeOnline total=null;
		int count=1;
		if(!mapCount.isEmpty()){
			Set<String> set = mapCount.keySet();
			for (String key : set) {
				String[] deptProject = key.split(",");
				XdTotalTimeOnline online=new XdTotalTimeOnline();
				online.setId(UuidUtil.getUUID());
				online.setDeptName(deptProject[0]);
				online.setProjectName(deptProject[1]);

				if(totalMap.get(deptProject[0])==null){
					total=new XdTotalTimeOnline();
					total.setId(UuidUtil.getUUID());
					total.setDeptName(key.split(",")[0]);
					total.setProjectName("合计");
					totalMap.put(key.split(",")[0],total);
				}else{
					total=totalMap.get(key.split(",")[0]);
				}

				Integer num = mapCount.get(key);

				online.setTime25(mapCount.get(key));

				Integer old =   total.getTime25()==null?0:total.getTime25();
				total.setTime25(old+num);

				online.setCreateTime(DateUtil.getCurrentTime());
				online.setSortnum(count);
				online.save();
				count++;

			}
		}


		Set<String> setTotal = totalMap.keySet();
		for (String key : setTotal) {
			XdTotalTimeOnline online = totalMap.get(key);
			online.setCreateTime(DateUtil.getCurrentTime());
			online.setSortnum(count);
			online.save();
			count++;
		}

		Class<? super XdTotalOnlineShifts> clazz1 = XdTotalOnlineShifts.class.getSuperclass();

		Map<String ,XdTotalOnlineShifts> totalShiftsMap=new HashMap<>();

		XdTotalOnlineShifts totalShifts=null;

		if(!mapShifts.isEmpty()){
			Set<String> set = mapShifts.keySet();
			for (String key : set) {
				String[] deptProject = key.split(",");
				XdTotalOnlineShifts online=new XdTotalOnlineShifts();
				online.setId(UuidUtil.getUUID());
				online.setDeptName(deptProject[0]);
				online.setProjectName(deptProject[1]);

				if(totalShiftsMap.get(deptProject[0])==null){
					totalShifts=new XdTotalOnlineShifts();
					totalShifts.setId(UuidUtil.getUUID());
					totalShifts.setDeptName(deptProject[0]);
					totalShifts.setProjectName("合计");
					totalShiftsMap.put(deptProject[0],totalShifts);
				}else{
					totalShifts=totalShiftsMap.get(deptProject[0]);
				}

				String shifts = mapShifts.get(key);
				online.setShifts25(shifts);

				String old = totalShifts.getShifts25()==null?"":totalShifts.getShifts25();

				Set<String> hashSet=new HashSet();
				if(!"".equals(shifts)){
					old=old+","+shifts;
				}
				old=old.replaceAll("^,","");
				hashSet.addAll(Arrays.asList(old.split(",")));
				//hashSet.f
				String setValues="";
				for (String setValue : hashSet) {
					if(!setValue.equals("")){

						setValues=setValue+","+setValues;
					}

				}


				totalShifts.setShifts25(setValues.replaceAll(",$",""));
				online.save();
				//	totalShifts.save();

			}
		}
		Set<String> setShift = totalShiftsMap.keySet();
		for (String key : setShift) {
			XdTotalOnlineShifts totalOnlineShifts = totalShiftsMap.get(key);
			totalOnlineShifts.save();
		}


		String sql  = " from "+TABLE_NAME+" o    where 1=1";


		sql = sql + " order by o.dept_name,sortnum";
		return Db.paginate(pNum, pSize, " select * ", sql);
	}



	public Page<Record> getOnlinePage(int pNum,int pSize,String days,String id,String field){

		XdTotalTimeOnline online = XdTotalTimeOnline.dao.findById(id);

		XdTotalOnlineShifts shift=XdTotalOnlineShifts.dao.findFirst(" select * from xd_total_online_shifts where dept_name='"
				+online.getDeptName()+"' and project_name='"+online.getProjectName()+"'");

		int suffix = Integer.parseInt(field.replaceAll("time",""));
		String  shiftNames="";
		try {
			Method method = XdTotalOnlineShifts.class.getSuperclass().getMethod("getShifts" + suffix);
			shiftNames= (String)method.invoke(shift);

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		String[] shiftArr = shiftNames.split(",");
		String inStr="";
		for (String shiftName : shiftArr) {
			inStr=inStr+"'"+shiftName+"'"+",";
		}



		String[] ymd = days.split("-");
		String ym=ymd[0]+ymd[1];
		String sql="";
		if(online.getProjectName().equals("合计")){

			sql  =  " from  xd_attendance_summary o   where schedule_year_month='" + ym + "' and dept_name='"+online.getDeptName()+"' and day" + ymd[2] + " in ("+inStr.replaceAll(",$","")+")";
		}else{

			sql  =  " from  xd_attendance_summary o   where schedule_year_month='" + ym + "' and dept_name='"+online.getDeptName()+"'  and project_name='"+online.getProjectName()+"' and day" + ymd[2] + " in ("+inStr.replaceAll(",$","")+")";
		}


		System.out.println(sql);

		//sql = sql + " order by o.emp_num,sortnum";


		return Db.paginate(pNum, pSize, " select dept_name,project_name,emp_num,emp_name ,day"+ymd[2]+" as shift", sql);
	}
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdTotalTimeOnline o = me.getById(id);
    		o.delete();
    	}
	}



	public File exportExcel(String path, String workDate){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";

		sql = sql + " order by dept_name, sortnum";


		List<XdTotalTimeOnline> list = XdTotalTimeOnline.dao.find(" select * "+sql);//查询全部

		String title=workDate+"在岗统计";
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> titleRow=new ArrayList<String>();
		titleRow.add(title);
		rows.add(titleRow);

		List<String> first = new ArrayList<String>();
		List<String> second = new ArrayList<String>();
		first.add("部门");
		first.add("项目");
		second.add("");
		second.add("");
		for (int j = 0; j <24 ; j++) {
			first.add(j+":00");
			if(j<23){
				second.add(j+1+":00");
			}
		}

		second.add("0:00");
		rows.add(first);
		rows.add(second);

		Class<? super XdTotalTimeOnline> superclass = XdTotalTimeOnline.class.getSuperclass();
		for(int j = 0; j < list.size(); j++){
			List<String> row = new ArrayList<String>();
			XdTotalTimeOnline online = list.get(j);
			row.add(online.getDeptName());
			row.add(online.getProjectName());
			for (int i = 1; i <25 ; i++) {
				try {
					Method method = superclass.getMethod("getTime" + i);
					 int num = (int)method.invoke(online);
					row.add(String.valueOf(num));
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}




			rows.add(row);
		}
		//File file = ExcelUtil.attendanceFile(path,rows,xdDayModels.size());


		ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);
		writer.write(rows, true);
		int size = rows.get(1).size();
		writer.merge(0,0,0,size-1,rows.get(0).get(0),false);
		writer.merge(1,2,0,0,rows.get(1).get(0),false);
		writer.merge(1,2,1,1,rows.get(1).get(1),false);
		//关闭writer，释放内存
		writer.close();
		return new File(path);
		//return file;
	}


	public File exportEmpExcel(String path, String workDate){
		String[] ymd = workDate.split("-");
		String ym = ymd[0] + ymd[1];

		String sql  = " select * from  xd_attendance_summary" +
				" where schedule_year_month='"+ym+"' and day"+ymd[2]+" !='' order by dept_name";



		List<XdAttendanceSummary> list = XdAttendanceSummary.dao.find(sql);//查询全部

		String title=workDate+"在岗人员信息";
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> titleRow=new ArrayList<String>();
		titleRow.add(title);
		rows.add(titleRow);

		List<String> first = new ArrayList<String>();
		first.add("部门");
		first.add("项目");
		first.add("姓名");
		first.add("工号");
		first.add("班次");
		rows.add(first);
		Map<String, XdShift>  nameToXdShiftMap = CheckAttendanceUtil.shfitsMap();

		Class<? super XdAttendanceSummary> superclass = XdAttendanceSummary.class.getSuperclass();
		for(int j = 0; j < list.size(); j++){
			List<String> row = new ArrayList<String>();
			XdAttendanceSummary summary = list.get(j);
			try {
				Method method = superclass.getMethod("getDay" + ymd[2]);
				String shiftName = (String)method.invoke(summary);
				XdShift shift = nameToXdShiftMap.get(shiftName);
				if(shift.getBusitime()!=null && !"".equals(shift.getBusitime())){
					row.add(summary.getDeptName());
					row.add(summary.getProjectName());
					row.add(summary.getEmpName());
					row.add(summary.getEmpNum());
					row.add(shiftName);
					rows.add(row);
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}
		//File file = ExcelUtil.attendanceFile(path,rows,xdDayModels.size());


		ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);
		writer.write(rows, true);
		int size = rows.get(1).size();
		writer.merge(0,0,0,size-1,rows.get(0).get(0),false);
	/*	writer.merge(1,2,0,0,rows.get(1).get(0),false);
		writer.merge(1,2,1,1,rows.get(1).get(1),false);*/
		//关闭writer，释放内存
		writer.close();
		return new File(path);
		//return file;
	}
}