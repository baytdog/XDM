package com.pointlion.util;

import com.jfinal.kit.StrKit;
import com.pointlion.mvc.common.model.*;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: king
 * @date: 2023/2/18 14:18
 * @description: 考勤通用相关
 */
public class CheckAttendanceUtil {
    public static Map<String, XdShift> shfitsMap(){
        List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift");
        return xdShifts.stream().collect(Collectors.toMap(XdShift::getShiftname, xdShift -> xdShift));

    }

    public static double getWokrHours(String year,String month){
        XdWorkHour workHours = XdWorkHour.dao.findFirst("select * from  xd_work_hour where year='" + year + "' and  month='" + month + "'");
        return (workHours==null?0:workHours.getWorkHour());
    }

    public static void main(String[] args) {
        XdOvertimeSummary os=new XdOvertimeSummary();
        XdSettleOvertimeSummary sos=new XdSettleOvertimeSummary();
        os.setId(123L);
        os.setEmpName("abc");
        try {
            BeanUtils.copyProperties(sos,os);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(sos);


    }

    public static StringBuffer getHolidays(List<XdDayModel> modelList){

        StringBuffer sb=new StringBuffer();
        XdDayModel dayModel = modelList.get(modelList.size() - 1);

        String id = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.parse(dayModel.getDays()).plusDays(1));
        XdDayModel nextMonFirstDay = XdDayModel.dao.findById(id);
        for (XdDayModel xdDayModel : modelList) {
            if("1".equals(xdDayModel.getIsnatHoliday())){
                sb.append(xdDayModel.getId()).append(",");
            }
        }
        if("1".equals(nextMonFirstDay.getIsnatHoliday())){
            sb.append(nextMonFirstDay.getId()).append(",");
        }
        return sb;
    }

    public static  XdOvertimeSummary getOverTimeSummary(String ... agrs){
        String sql="select * from  xd_overtime_summary where 1=1 ";
        if(StrKit.notBlank(agrs[0])){
            sql=sql+" and emp_name='"+agrs[0]+"'";
        }
        if(StrKit.notBlank(agrs[1])){
            sql=sql+" and apply_date='"+agrs[1]+"'";
        }
        if(StrKit.notBlank(agrs[2])){
            sql=sql+" and apply_type='"+agrs[2]+"'";
        }
        /*if(StrKit.notBlank(agrs[3])){
            sql=sql+" and apply_start='"+agrs[3]+"'";
        }

        if(StrKit.notBlank(agrs[4])){
            sql=sql+" and apply_end='"+agrs[4]+"'";
        }*/

     /*   XdOvertimeSummary otSummary = XdOvertimeSummary.dao.findFirst(
                "select * from  xd_overtime_summary where apply_date='" + ymdInLine +
                        "' and emp_name='" + empName +
                        "' and apply_type='0'");*/

        return  XdOvertimeSummary.dao.findFirst(sql);
    }

/*
    public static  List<XdOvertimeSummary> getOtSummaryList(String empName,String applyDate,String applyType){
        String sql="select * from  xd_overtime_summary where 1=1 ";
        if(StrKit.notBlank(empName)){
            sql=sql+" and emp_name='"+empName+"'";
        }
        if(StrKit.notBlank(applyDate)){
            sql=sql+" and apply_date='"+applyDate+"'";
        }
        if(StrKit.notBlank(applyType)){
            sql=sql+" and apply_type='"+applyType+"'";
        }

        return  XdOvertimeSummary.dao.find(sql);
    }*/

    public static  List<XdOvertimeSummary> getOtSummaryList(String yearMonth,String applyType,String source){
        String sql="select * from  xd_overtime_summary where 1=1 ";

        if(StrKit.notBlank(yearMonth)){
            sql=sql+" and super_days like'"+yearMonth+"%'";
        }
        if(StrKit.notBlank(applyType)){
            sql=sql+" and apply_type='"+applyType+"'";
        }
        if(StrKit.notBlank(source)){
            sql=sql+" and source='"+source+"'";
        }

        return  XdOvertimeSummary.dao.find(sql);
    }


    /*public static void rollBackAttendanceRelation(String year,String month,String empName){
        String ym=year+month;
        XdAttendanceSummary summary = XdAttendanceSummary.dao.findFirst("select * from  xd_attendance_summary " +
                "where schedule_year_month='"+ym+"' and  emp_name='"+empName+"'");




    }*/
}
