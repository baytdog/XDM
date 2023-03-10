package com.pointlion.plugin.quartz.task;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.model.XdEffict;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.model.XdRecords;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author: king
 * @date: 2023/1/2 17:40
 * @description: 更新工作岗位、薪资、星级
 */
public class UpdateWorkAndSalary implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        List<XdEffict> objList = XdEffict.dao.find("select * from  xd_effict where status='0'");
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String nowDate = LocalDate.now().format(dtf);
        for (XdEffict xdEffict : objList) {
            String effectDate = xdEffict.getEffectDate();
            if(effectDate.equals(nowDate)){
                XdEmployee employee = XdEmployee.dao.findById(xdEffict.getEid());
                employee.setWorkstation(xdEffict.getNewPdname());
                employee.setDepartment(xdEffict.getNewDeptId());
                employee.setSalary(xdEffict.getNewSalary().intValue());
                employee.setSalaryLevel(xdEffict.getNewSalaryLevel());
                if(!employee.getWorkstation().equals(xdEffict.getNewPdvalue())){
                    String chrecord = employee.getChrecord();
                 /*   if(chrecord!=null && !"".equals(chrecord) && !chrecord.endsWith(";")){
                        chrecord=chrecord+";";
                    }*/
//                    int size = XdEffict.dao.find("select * from  xd_effict where eid='" + employee.getId() + "' and  status ='1'").size();
                    int size=0;
                    if(chrecord==null || "".equals(chrecord)){
                        size=1;
                        chrecord="";
                    }else{
                        if(!chrecord.endsWith(";")){
                            chrecord=chrecord+";";
                        }
                        size=chrecord.split("、").length;
                    }
                    chrecord=chrecord+" "+size+"、"+xdEffict.getNewDeptName()+xdEffict.getNewPdname()+effectDate;
                    if(xdEffict.getOtherRemarks()!=null && !"".equals(xdEffict.getOtherRemarks())){
                        chrecord=chrecord+"("+xdEffict.getOtherRemarks()+")";
                    }
                    chrecord=chrecord+";";
                    employee.setChrecord(chrecord);
                }
                if(employee.getSalary()!=xdEffict.getNewSalary().intValue()){
                    String salaryRecord = employee.getSaladjrecord();
                    if(salaryRecord!=null && !"".equals(salaryRecord) && !salaryRecord.endsWith(";")){
                        salaryRecord=salaryRecord+";";
                    }
//                    int size = XdEffict.dao.find("select * from  xd_effict where eid='" + employee.getId() + "' and  status ='1'").size();
                    int size=0;
                    if(salaryRecord==null || "".equals(salaryRecord)){
                        size=1;
                        salaryRecord="";
                    }else{
                        if(!salaryRecord.endsWith(";")){
                            salaryRecord=salaryRecord+";";
                        }
                        size=salaryRecord.split("、").length;
                    }
                    salaryRecord=salaryRecord+" "+size+"、"+xdEffict.getNewSalaryLevel()+xdEffict.getNewSalary()+"\t"+effectDate;
                    if(xdEffict.getOtherRemarks()!=null && !"".equals(xdEffict.getOtherRemarks())){
                        salaryRecord=salaryRecord+"("+xdEffict.getOtherRemarks()+")";
                    }
                    salaryRecord=salaryRecord+";";
                    employee.setSaladjrecord(salaryRecord);
                }
                employee.update();
                xdEffict.setStatus("1");
                xdEffict.setUuser("定时器更新");
                xdEffict.setUtime(DateUtil.getCurrentTime());
                xdEffict.update();

            }
        }

    }

}
