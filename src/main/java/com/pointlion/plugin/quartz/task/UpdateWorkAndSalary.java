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
 * @description:
 */
public class UpdateWorkAndSalary implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Record> effictList = Db.find("select * from  xd_effict where status='0'");


        for (Record effict : effictList) {

            /*XdEffict effict1;
            effict1 = (XdEffict) effict;*/

            String values = effict.get("values");
            String effectDate = effict.get("effectDate");
            LocalDate parse = LocalDate.parse(effectDate, dtf);
            int fieldtype = effict.get("fieldtype");
            String now = LocalDate.now().format(dtf);
            String remarks = effict.get("remarks");

            if(LocalDate.now().isAfter(parse)||now.equals(effectDate)){
                String eid = effict.get("eid");
                XdEmployee emp = XdEmployee.dao.findById(eid);
                XdRecords records =new XdRecords();
                if(fieldtype==1){
                    emp.setWorkstation(values);
                    String workRecord = (emp.getChrecord() == null ? "" : emp.getChrecord() + "\t") + "原岗位: " + emp.getWorkstation() + "-" + "最新岗位: " + values;
                    emp.setChrecord(workRecord);
                    emp.update();
                    records.setFieldType(2);
                    records.setOldValue(emp.getWorkstation());
                    records.setNewValue(values);
                }

                if(fieldtype==2){
                    emp.setSalary(Integer.valueOf(values));
                    String salaryRecord = (emp.getSaladjrecord() == null ? "" : emp.getSaladjrecord() + "\t") + "原工资: " + emp.getSalary() + " - " + "最新工资: " + values;
                    emp.setSaladjrecord(salaryRecord);
                    emp.update();
                    records.setFieldType(1);
                }


                records.setEid(eid);
                records.setEffictDate(effectDate);
                records.setRemarks(remarks);
                records.setCuser("系统定时");
                records.setCtime(DateUtil.getCurrentTime());
                records.save();

                Integer id = effict.getInt("id");
                XdEffict xdeffict = XdEffict.dao.findById(id);
                xdeffict.setStatus("1");
                xdeffict.setUtime(DateUtil.getCurrentTime());
                xdeffict.setUuser("系统定时");
                xdeffict.update();
            }
           /* if(now.equals(effectDate)){
                String eid = effict.get("eid");
                XdEmployee emp = XdEmployee.dao.findById(eid);

                if(fieldtype==1){
                    emp.setWorkstation(values);
                    String workRecord = (emp.getChrecord() == null ? "" : emp.getChrecord() + "\t") + "原岗位: " + emp.getWorkstation() + "-" + "最新岗位: " + values;
                    emp.setChrecord(workRecord);
                    emp.update();
                }

                if(fieldtype==2){
                    emp.setSalary(Integer.valueOf(values));
                    String salaryRecord = (emp.getSaladjrecord() == null ? "" : emp.getSaladjrecord() + "\t") + "原工资: " + emp.getSalary() + " - " + "最新工资: " + values;
                    emp.setSaladjrecord(salaryRecord);
                    emp.update();
                }

                Integer id = effict.getInt("id");
                XdEffict xdeffict = XdEffict.dao.findById(id);
                xdeffict.setStatus("1");
                xdeffict.setUtime(DateUtil.getCurrentTime());
                xdeffict.setUuser(ShiroKit.getUserId());
                xdeffict.update();

            }*/
        }
    }
}
