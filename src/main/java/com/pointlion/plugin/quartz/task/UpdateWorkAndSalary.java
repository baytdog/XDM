package com.pointlion.plugin.quartz.task;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.model.XdEffict;
import com.pointlion.mvc.common.model.XdEmployee;
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
            int fieldtype = effict.get("fieldtype");
            String now = LocalDate.now().format(dtf);

            if(now.equals(effectDate)){
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

            }
        }
    }
}
